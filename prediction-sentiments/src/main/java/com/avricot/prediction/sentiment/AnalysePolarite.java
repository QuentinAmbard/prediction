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
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.google.common.base.Charsets;

@Service
public class AnalysePolarite {
	
	@Inject
	CandidatRespository candidatRespository;
	
	private static Logger LOG = Logger.getLogger(AnalysePolarite.class);
	
	private static final String DATA_TXT_SENTOKEN_LINGPIPE = "D:\\Dev\\2012\\workspace\\lingpipe\\data\\txt_sentoken";
	
	String[] tweet1 = {"J'aime bien le candidat Sarkozy qui tient un grand meeting pour dénoncer la fraude, en le finançant sur le budget du président Sarkozy.",
	"L'#UMP, premier parti de France, avec des militants déterminés à faire réélire Nicolas #Sarkozy http://bit.ly/skqpUb",
	"Allaitement : Barthès, chargé de com' de Sarkozy http://goo.gl/6gQnA",
	"Union européenne : dans la crise de la dette, les bouffons se succèdent. Après #Berlusconi, c'est au tour de #Sarkozy : http://bit.ly/tioir9",
	"Chasse aux fraudeurs, rigueur, vote des étrangers: Nicolas Sarkozy et l'UMP à droite toute pour la présidentielle http://ow.ly/7uZig",
	"Sarkozy est incroyable, je l'aime",
	"S est excellent",
	"Francois Hollande est le candidat idéal"};
	
	private List<Candidat> candidats;
	private File mPolarityDir;
	private String[] mCategories;
	private DynamicLMClassifier<NGramProcessLM> mClassifier;
	
	public void run() throws ClassNotFoundException, IOException {

		mPolarityDir = new File(DATA_TXT_SENTOKEN_LINGPIPE);
		mCategories = mPolarityDir.list();
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
		
//		train();
//		evaluatePolarite("http://www.liberation.fr/politiques/01012372674-presidentielle-hollande-et-sarkozy-au-coude-a-coude-au-1er-tour-sondage-lh2");
		evaluate();
	}

	void train() throws IOException {
	    for (int i = 0; i < mCategories.length; ++i) {
	        String category = mCategories[i];
	        Classification classification = new Classification(category);
	        File dir = new File(mPolarityDir, mCategories[i]);
	        File[] trainFiles = dir.listFiles();
	        for (int j = 0; j < trainFiles.length; ++j) {
            	File trainFile = trainFiles[j];
            	String review = com.google.common.io.Files.toString(trainFile, Charsets.ISO_8859_1);
                Classified<CharSequence> classified = new Classified<CharSequence>(review,classification);
                mClassifier.handle(classified);
	        }
	    }
	}
	
	void evaluate() throws IOException {
		Classification classification = null;
		for (int i = 0; i < tweet1.length ; i++) {
			classification = mClassifier.classify(tweet1[i]);
			List<String> Urls = URLUtils.URLInString(tweet1[i]);
			if(!Urls.isEmpty()) {
				for (String url : Urls) {
					/* SCANNER L'ARTICLE */
					evaluatePolarite(url);
				}
			} 
			System.out.println("CAT = " + classification.bestCategory());	
		}
    }
	
	String evaluatePolarite(String currentUrl) throws IOException {
		
		candidats = candidatRespository.findAll();
		
		LOG.info("URL scannée : " + currentUrl);
		Document doc = Jsoup.connect(currentUrl).get();
		String[] splittedArticle = (doc.body().text()).split(".");
		for (String phrase : splittedArticle) {
			for (Candidat candidat : candidats) {
				//TODO Gérer les surnoms
//				LOG.info("Phrase sur " + candidat.getName().toString() + " =>> " + phrase);
				if(phrase.indexOf(candidat.getName().toString()) != -1) {
				}
			}
		}
		
//		Classification classification = mClassifier.classify(tweet1[i]);
		return null;
	}
	
}
