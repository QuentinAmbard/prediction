package com.avricot.prediction.sentiment;

import java.io.IOException;
import java.util.Date;
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
import com.avricot.prediction.utils.DateUtils;

/**
 * Classe analysant les tweets et articles
 */

@Service
public class AnalysePolarite {
	
	@Inject
	CandidatRespository candidatRespository;
	
	@Inject
	TweetRepository tweeterRepository;
	
	private static Logger LOG = Logger.getLogger(AnalysePolarite.class);
	
	static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
	
	private StringBuffer positiveTweets = new StringBuffer(); 
	private StringBuffer negativeTweets = new StringBuffer();
	private StringBuffer neutralTweets = new StringBuffer();
	private StringBuffer notFrenchTweets = new StringBuffer();
	private StringBuffer invalidTweets = new StringBuffer();
	
	private List<Tweet> tweetsToEvaluate;
	private List<Candidat> candidats;
	private String[] mCategories = {"positive", "negative", "neutral", "not_french", "invalid"};
	private DynamicLMClassifier<NGramProcessLM> mClassifier;

	/**
	 * Méthode principale de l'analyse des tweets, elle entraîne l'analyse
	 * puis récupère des lots de 300 tweets qu'elle analyse et enregistre en base.
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void run() throws ClassNotFoundException, IOException {
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		candidats = candidatRespository.findAll();
		
		long dateMidnight =  DateUtils.getMidnightTimestamp(new Date(System.currentTimeMillis()));
		Date startDate = new Date(dateMidnight - MILLIS_IN_A_DAY);
		Date endDate = new Date(dateMidnight);
		
		/* On entraîne l'analyseur */
		train();

		LOG.info("Nombre de tweets restant à traiter = " + tweeterRepository.countNoPolarityBetween(startDate, endDate));
		
		do {
//			tweetsToEvaluate = tweeterRepository.findNoPolarity(300);
			tweetsToEvaluate = tweeterRepository.findNoPolarityBetween(300, startDate, endDate);
			if(!tweetsToEvaluate.isEmpty()) {
				LOG.info("Traitement de " + tweetsToEvaluate.size() + "tweets...");
				evaluateTweets();
				
				tweeterRepository.save(tweetsToEvaluate);
				LOG.info(tweetsToEvaluate.size() + " tweets traités et sauvegardés.");
			}
		} while (tweeterRepository.countNoPolarityBetween(startDate, endDate) > 0);
		
		LOG.info("Analyse de sentiments : Fin de traitement des tweets.");
	}

	/**
	 * Récupère tous les tweets manuellement évalués et les catégorise pour
	 * préparer l'entraînement de l'analyseur.
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
			else if(tweet.getPolarity() == Polarity.INVALID)
				invalidTweets.append(tweet.getValue());
		}
		
		tweetCleaner(neutralTweets.toString());
		tweetCleaner(positiveTweets.toString());
		tweetCleaner(negativeTweets.toString());
		tweetCleaner(notFrenchTweets.toString());
		tweetCleaner(invalidTweets.toString());
	}
	
	/**
	 * Nettoie les tweets avant de les injecter dans l'entrainement du détecteur.
	 * Suppression des pseudos, des url, des #, des RT et des noms et surnoms des candidats.
	 * @param tweet à nettoyer
	 * @return le tweet tout propre
	 */
	String tweetCleaner(String tweet) {
		for (Candidat candidat : candidats) {
			tweet = tweet.replaceAll("\\#", "");
			tweet = tweet.replaceAll("@([A-Za-z0-9_]+)", "");
			tweet = tweet.replaceAll("(?i)"+candidat.getDisplayName(), "");
			tweet = tweet.replaceAll("(?i)"+candidat.getCandidatName().toString(), "");
			for (String nickname : candidat.getNicknames()) {
				tweet = tweet.replaceAll("(?i)"+nickname, "");
			}
			tweet = tweet.replaceAll("RT", "");
			tweet = tweet.replaceAll("http://([A-Za-z0-9_./]+)", "");
			
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
        Classification classificationInvalid = new Classification("invalid");
        
        Classified<CharSequence> classifiedPos = new Classified<CharSequence>(positiveTweets, classificationPos);
        Classified<CharSequence> classifiedNeg = new Classified<CharSequence>(negativeTweets, classificationNeg);
        Classified<CharSequence> classifiedNeu = new Classified<CharSequence>(neutralTweets, classificationNeu);
        Classified<CharSequence> classifiedNotF = new Classified<CharSequence>(notFrenchTweets, classificationNotFrench);
        Classified<CharSequence> classifiedInvalid = new Classified<CharSequence>(notFrenchTweets, classificationInvalid);
        
        mClassifier.handle(classifiedPos);
        mClassifier.handle(classifiedNeg);
        mClassifier.handle(classifiedNeu);
        mClassifier.handle(classifiedNotF);
        mClassifier.handle(classifiedInvalid);
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
			if(bestCategory.equalsIgnoreCase("positive"))
				tweet.setPolarity(Polarity.POSITIVE);
			else if(bestCategory.equalsIgnoreCase("negative"))
				tweet.setPolarity(Polarity.NEGATIVE);
			else if(bestCategory.equalsIgnoreCase("neutral"))
				tweet.setPolarity(Polarity.NEUTRAL);
			else if(bestCategory.equalsIgnoreCase("not_french"))
				tweet.setPolarity(Polarity.NOT_FRENCH);
			else if(bestCategory.equalsIgnoreCase("invalid"))
				tweet.setPolarity(Polarity.INVALID);
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
