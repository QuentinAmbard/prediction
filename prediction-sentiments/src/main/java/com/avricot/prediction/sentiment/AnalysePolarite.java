package com.avricot.prediction.sentiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

import com.google.common.base.Charsets;

public class AnalysePolarite {
	
	String[] tweet1 = {"J'aime bien le candidat Sarkozy qui tient un grand meeting pour dénoncer la fraude, en le finançant sur le budget du président Sarkozy.",
	"L'#UMP, premier parti de France, avec des militants déterminés à faire réélire Nicolas #Sarkozy http://bit.ly/skqpUb",
	"Allaitement : Barthès, chargé de com' de Sarkozy http://goo.gl/6gQnA",
	"Union européenne : dans la crise de la dette, les bouffons se succèdent. Après #Berlusconi, c'est au tour de #Sarkozy : http://bit.ly/tioir9",
	"Chasse aux fraudeurs, rigueur, vote des étrangers: Nicolas Sarkozy et l'UMP à droite toute pour la présidentielle http://ow.ly/7uZig",
	"Sarkozy est incroyable, je l'aime",
	"S est excellent",
	"Francois Hollande est le candidat idéal"};
	
	File mPolarityDir;
	String[] mCategories;
	DynamicLMClassifier<NGramProcessLM> mClassifier;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    try {
	        new AnalysePolarite(args).run();
	    } catch (Throwable t) {
	        System.out.println("Thrown: " + t);
	        t.printStackTrace(System.out);
	    }
	}
	
	void run() throws ClassNotFoundException, IOException {
		train();
		evaluate();
	}
	
	AnalysePolarite(String[] args) {
		mPolarityDir = new File("D:\\jartero\\dropbox\\Dropbox\\2012\\workspace\\lingpipe\\data\\txt_sentoken");
		mCategories = mPolarityDir.list();
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
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
			List<URL> Urls = URLUtils.URLInString(tweet1[i]);
			if(!Urls.isEmpty()) {
				for (URL url : Urls) {
					/* SCANNER L'ARTICLE */
					evaluatePolarite(url);
				}
			} 
			System.out.println("CAT = " + classification.bestCategory());	
		}
    }

	boolean isTrainingFile(File file) {
	    return file.getName().charAt(2) != '9';  // test on fold 9
	}
	
	String evaluatePolarite(URL currentUrl) {
		
		return null;
	}
	
}
