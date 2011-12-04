package com.avricot.prediction.sentiment;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.report.Polarity;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.sentiment.services.URLUtils;

@Service
public class AnalysePolarite {
	
	@Inject
	CandidatRespository candidatRespository;
	
	@Inject
	TweetRepository tweeterRepository;
	
	private static Logger LOG = Logger.getLogger(AnalysePolarite.class);
	
	private StringBuffer positiveTweets = new StringBuffer(); 
	private StringBuffer negativeTweets = new StringBuffer();
	private StringBuffer neutralTweets = new StringBuffer();
	private StringBuffer notFrenchTweets = new StringBuffer();
	
	private List<Tweet> tweetsToEvaluate;
	private List<Candidat> candidats;
	private String[] mCategories = {"positive", "negative", "neutral", "not_french"};
	private DynamicLMClassifier<NGramProcessLM> mClassifier;

	public void run() throws ClassNotFoundException, IOException {
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		candidats = candidatRespository.findAll();

		//TODO : comprendre pourquoi  http://t.co/IrJVg85u fait completement péter le parseur
		
		do {
			tweetsToEvaluate = tweeterRepository.findNoPolarity(300);
			
			if(!tweetsToEvaluate.isEmpty()) {
				LOG.info("\n\n\n>>>>>>>>>>>>>>>>> SIZE = " +  tweetsToEvaluate.size() + "<<<<<<<<<<<<<<<<<\n\n");
				train();
				evaluateTweets();
				
				tweeterRepository.save(tweetsToEvaluate);
				LOG.info(tweetsToEvaluate.size() + " tweets traités et sauvegardés.");
			}
		} while (tweeterRepository.count() > 0);
	}

	/**
	 * 
	 */
	void prepareTweetsForTrain() {
		List<Tweet> positifs = tweeterRepository.findAllByChecked(true);
		for (Tweet tweet : positifs) {
			if(tweet.getPolarity() == Polarity.POSITIVE)
				positiveTweets.append(tweet.getValue());
			else if(tweet.getPolarity() == Polarity.NEGATIVE)
				negativeTweets.append(tweet.getValue());
			else if(tweet.getPolarity() == Polarity.NEUTRAL)
				neutralTweets.append(tweet.getValue());
			else if(tweet.getPolarity() == Polarity.NOT_FRENCH)
				notFrenchTweets.append(tweet.getValue());
		}
		
		tweetCleaner(neutralTweets.toString());
		tweetCleaner(positiveTweets.toString());
		tweetCleaner(negativeTweets.toString());
		tweetCleaner(notFrenchTweets.toString());
	}
	
	/**
	 * Nettoie les tweets avant de les injecter dans l'entrainement du détecteur
	 * @param tweet
	 * @return
	 */
	String tweetCleaner(String tweet) {
		for (Candidat candidat : candidats) {
			tweet = tweet.replaceAll("\\#", "");
			tweet = tweet.replaceAll("(?i)"+candidat.getDisplayName(), "");
			tweet = tweet.replaceAll("(?i)"+candidat.getCandidatName().toString(), "");
			for (String nickname : candidat.getNicknames()) {
				tweet = tweet.replaceAll("(?i)"+nickname, "");
			}
			tweet = tweet.replaceAll("RT", "");
			/* Pseudos Twitter */
			tweet = tweet.replaceAll("/[@]\\w+/", "");
			//TODO GERER LES URLS
//			tweet = tweet.replaceAll("(.*://)", "");
		}
		
		return tweet;
	}
	
	
	/**
	 * Entraine le détecteur de polarité
	 * @throws IOException
	 */
	void train() throws IOException {
		prepareTweetsForTrain();
		
        Classification classificationPos = new Classification("positive");
        Classification classificationNeg = new Classification("negative");
        Classification classificationNeu = new Classification("neutral");
        Classification classificationNotFrench = new Classification("not_french");
        
        Classified<CharSequence> classifiedPos = new Classified<CharSequence>(positiveTweets, classificationPos);
        Classified<CharSequence> classifiedNeg = new Classified<CharSequence>(negativeTweets, classificationNeg);
        Classified<CharSequence> classifiedNeu = new Classified<CharSequence>(neutralTweets, classificationNeu);
        Classified<CharSequence> classifiedNotF = new Classified<CharSequence>(notFrenchTweets, classificationNotFrench);
        
        mClassifier.handle(classifiedPos);
        mClassifier.handle(classifiedNeg);
        mClassifier.handle(classifiedNeu);
        mClassifier.handle(classifiedNotF);
	}
	
	/**
	 * Evalue la polarité de tweets
	 * @throws IOException
	 */
	void evaluateTweets() throws IOException {
		Classification classification = null;
		for (Tweet tweet : tweetsToEvaluate) {
			final String tweetValue = tweet.getValue();
			List<String> urls = URLUtils.URLInString(tweetValue);
			
			if(!urls.isEmpty()) {
				classification = evaluateURLPolarite(urls);
			} else {
				classification = mClassifier.classify(tweetValue);	
			}
			
			final String bestCategory = classification.bestCategory();
//			LOG.info(tweetValue +" => " + bestCategory);
			if(bestCategory.equalsIgnoreCase("positive"))
				tweet.setPolarity(Polarity.POSITIVE);
			else if(bestCategory.equalsIgnoreCase("negative"))
				tweet.setPolarity(Polarity.NEGATIVE);
			else if(bestCategory.equalsIgnoreCase("neutral"))
				tweet.setPolarity(Polarity.NEUTRAL);
			else if(bestCategory.equalsIgnoreCase("not_french"))
				tweet.setPolarity(Polarity.NOT_FRENCH);
		}
    }
	
	/**
	 * Permet de récupérer la polarité d'un string
	 * @param s
	 * @return
	 * @throws IOException
	 */
	Classification evaluate(String s) throws IOException {
		Classification classification = mClassifier.classify(s);
		return classification;	
    }

	/**
	 * Analyse la polarité d'une URL en scannant la page et en traitant
	 * une par une les phrase contenant le nom d'un candidat
	 * @param currentUrl
	 * @return
	 * @throws IOException
	 */
	Classification evaluateURLPolarite(List<String> urls) throws IOException {
		StringBuffer toAnalyse = new StringBuffer("");
		for (String currentUrl : urls) {
			LOG.info("URL scannée : " + currentUrl);
			try {
				String[] splittedArticle = null;
				Document doc = Jsoup.connect(currentUrl).timeout(2500).get();
				if(doc.body() != null && !doc.body().text().isEmpty()) {
					splittedArticle = (doc.body().text()).split("\\.");
					for (String phrase : splittedArticle) {
						for (Candidat candidat : candidats) {
							if(phrase.toLowerCase().indexOf(candidat.getCandidatName().toString().toLowerCase()) != -1) {
								toAnalyse.append(phrase);
							}
						}
					}
				}
			} catch (IOException e) {
				LOG.error("ERREUR EN SCANNANT URL : " + currentUrl);
			} catch (Exception e) {
				LOG.error("EXCEPTION");
			}
		}
		if(!toAnalyse.equals("")) {
			return evaluate(toAnalyse.toString());
		} else {
			return null;
		}
	}
}
