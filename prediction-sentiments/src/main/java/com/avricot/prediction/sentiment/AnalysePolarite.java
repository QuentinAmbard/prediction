package com.avricot.prediction.sentiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.sentiment.services.URLUtils;
import com.google.common.base.Charsets;

@Service
public class AnalysePolarite {
	
	@Inject
	CandidatRespository candidatRespository;
	
	@Inject
	TweetRepository tweeterRepository;
	
	private static Logger LOG = Logger.getLogger(AnalysePolarite.class);
	
	private static final String DATA_TXT_SENTOKEN_LINGPIPE = "C:\\Users\\Jeremy\\Dropbox\\2012\\workspace\\lingpipe\\data\\txt_sentoken";
	
	private List<Candidat> candidats;
	private File mPolarityDir;
	private String[] mCategories;
	private DynamicLMClassifier<NGramProcessLM> mClassifier;
	
	public void run() throws ClassNotFoundException, IOException {
		mPolarityDir = new File(DATA_TXT_SENTOKEN_LINGPIPE);
		mCategories = mPolarityDir.list();
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		
		train();
		evaluateTweets();
	}

	/**
	 * Entraine le détecteur de polarité
	 * @throws IOException
	 */
	void train() throws IOException {
	    for (int i = 0; i < mCategories.length; ++i) {
	        String category = mCategories[i];
	        Classification classification = new Classification(category);
	        File dir = new File(mPolarityDir, mCategories[i]);
	        File[] trainFiles = dir.listFiles();
	        for (int j = 0; j < trainFiles.length; ++j) {
            	File trainFile = trainFiles[j];
            	String review = com.google.common.io.Files.toString(trainFile, Charsets.ISO_8859_1);
                Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                mClassifier.handle(classified);
	        }
	    }
	}
	
	/**
	 * Evalue la polarité de tweets
	 * @throws IOException
	 */
	void evaluateTweets() throws IOException {
		
		List<Tweet> tweetList = tweeterRepository.findAllByChecked(false);
		Classification tmpClass = null;
		Classification classification = null;
		for (Tweet tweet : tweetList) {
			final String tweetValue = tweet.getValue();
			List<String> Urls = URLUtils.URLInString(tweetValue);
			if(!Urls.isEmpty()) {
				List<Classification> classListe = new ArrayList<Classification>();
				for (String url : Urls) {
					tmpClass = evaluateURLPolarite(url);
					if(tmpClass != null) {
						/* Comment on exploite cette liste ? */
						classListe.add(evaluateURLPolarite(url));
					}
					LOG.info("URL SCANNEE");
				}
			} else {
				classification = mClassifier.classify(tweetValue);	
				LOG.info(tweetValue +" => " + classification.bestCategory());
			}			
		}
    }
	
	/**
	 * Permet de récupérer la polarité d'un string
	 * @param s
	 * @return
	 * @throws IOException
	 */
	Classification evaluate(String s) throws IOException {
		Classification classification = null;
		classification = mClassifier.classify(s);
		return classification;	
    }

	/**
	 * Analyse la polarité d'une URL en scannant la page et en traitant
	 * une par une les phrase contenant le nom d'un candidat
	 * @param currentUrl
	 * @return
	 * @throws IOException
	 */
	Classification evaluateURLPolarite(String currentUrl) throws IOException {
		
		candidats = candidatRespository.findAll();
		StringBuffer toAnalyse = new StringBuffer("");
		
		LOG.info("URL scannée : " + currentUrl);
		try {
			Document doc = Jsoup.connect(currentUrl).timeout(0).get();
			String[] splittedArticle = (doc.body().text()).split("\\.");
			for (String phrase : splittedArticle) {
				for (Candidat candidat : candidats) {
					if(phrase.toLowerCase().indexOf(candidat.getCandidatName().toString().toLowerCase()) != -1) {
						LOG.info("Phrase sur " + candidat.getCandidatName().toString() + " =>> " + phrase);
						toAnalyse.append(phrase);
					}
				}
			}
		} catch (IOException e) {
			LOG.error("ERREUR EN SCANNANT URL : " + currentUrl);
		}
		if(!toAnalyse.equals("")) {
			return evaluate(toAnalyse.toString());
		} else {
			return null;
		}
	}
	
}
