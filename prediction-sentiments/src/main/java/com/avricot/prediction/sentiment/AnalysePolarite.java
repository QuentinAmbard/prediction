package com.avricot.prediction.sentiment;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
	
	private static final String DATA_TXT_SENTOKEN_LINGPIPE = "D:\\Dev\\2012\\workspace\\lingpipe\\data\\txt_sentoken";
	
	
	String[] tweet1 = {"J'aime bien le candidat Sarkozy qui tient un grand meeting pour d�noncer la fraude, en le finan�ant sur le budget du pr�sident Sarkozy.",
	"L'#UMP, premier parti de France, avec des militants d�termin�s � faire r��lire Nicolas #Sarkozy http://bit.ly/skqpUb",
	"Allaitement : Barth�s, charg� de com' de Sarkozy http://goo.gl/6gQnA",
	"Union europ�enne : dans la crise de la dette, les bouffons se succ�dent. Apr�s #Berlusconi, c'est au tour de #Sarkozy : http://bit.ly/tioir9",
	"Chasse aux fraudeurs, rigueur, vote des �trangers: Nicolas Sarkozy et l'UMP � droite toute pour la pr�sidentielle http://ow.ly/7uZig",
	"Sarkozy est incroyable, je l'aime",
	"S est excellent",
	"Francois Hollande est le candidat id�al"};
	
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
	 * Entraine le d�tecteur de polarit�
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
	 * Evalue la polarit� de tweets
	 * @throws IOException
	 */
	void evaluateTweets() throws IOException {
		
		List<Tweet> tweetList = tweeterRepository.findAllByChecked(false);
		
		Classification classification = null;
		for (Tweet tweet : tweetList) {
			final String tweetValue = tweet.getValue();
			classification = mClassifier.classify(tweetValue);
			LOG.info(tweetValue +" => " + classification.bestCategory());
			List<String> Urls = URLUtils.URLInString(tweetValue);
			if(!Urls.isEmpty()) {
				for (String url : Urls) {
					/* SCANNER L'ARTICLE */
					evaluateURLPolarite(url);
				}
			} 
			System.out.println("CAT = " + classification.bestCategory());	
		}
    }
	
	/**
	 * Permet de r�cup�rer la polarit� d'un string
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
	 * Analyse la polarit� d'une URL en scannant la page et en traitant
	 * une par une les phrase contenant le nom d'un candidat
	 * @param currentUrl
	 * @return
	 * @throws IOException
	 */
	void evaluateURLPolarite(String currentUrl) throws IOException {
		
		candidats = candidatRespository.findAll();
		
		LOG.info("URL scann�e : " + currentUrl);
		Document doc = Jsoup.connect(currentUrl).get();
		String[] splittedArticle = (doc.body().text()).split("\\.");
		for (String phrase : splittedArticle) {
			for (Candidat candidat : candidats) {
				//TODO G�rer les surnoms
				if(phrase.toLowerCase().indexOf(candidat.getCandidatName().toString().toLowerCase()) != -1) {
					LOG.info("Phrase sur " + candidat.getCandidatName().toString() + " =>> " + phrase);
				}
			}
		}
	}
	
}
