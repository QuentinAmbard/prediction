package com.avricot.prediction.newspop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.repository.candidat.CandidatRespository;

@Service
public class NewsPopularity {
	
	@Inject
	CandidatRespository candidatRespository;
	
	private List<Candidat> candidats;
	private DailyReport dailyReport;
	
	private static final String RSS_LEMONDE = "http://rss.lemonde.fr/c/205/f/3050/index.rss";
	private static final String RSS_20MINUTES = "http://flux.20minutes.fr/c/32497/f/479493/index.rss";
	
	private static final String RSS_LEFIGARO = "http://rss.lefigaro.fr/lefigaro/laune";
	private static final String RSS_LIBERATION = "http://rss.liberation.fr/rss/9/";

	private static Logger LOG = Logger.getLogger(NewsPopularity.class);
	
	public void run() throws ClassNotFoundException, IOException {
		candidats = candidatRespository.findAll();
		
		Document doc = Jsoup.connect(RSS_LEMONDE).timeout(0).get();
		
		Elements links = doc.getElementsByTag("guid");
		for (Element link : links) {
			LOG.info("URL => " + link.text());
			Document article = Jsoup.connect(link.text()).timeout(0).get();
			List<RSSCounter> rssCounterListtmp = parseForCandidatPopularity(article.body().text(), candidats);
			if(rssCounterListtmp.size() > 0) {
				for (RSSCounter rssCounter : rssCounterListtmp) {
					LOG.info("IN => " + rssCounter.candidatName + rssCounter.score);
				}
			}
		}
	}
	
	/**
	 * Permet de parcourir un article à la recherche de toutes les occurrences des noms des
	 * candidats
	 * @param articleText
	 * @param candidats
	 * @return une liste de RSSCounter
	 */
	private List<RSSCounter> parseForCandidatPopularity(String articleText, List<Candidat> candidats) {
		int counter = 0;
		List<RSSCounter> rssCounterList = new ArrayList<RSSCounter>();
		
		for (Candidat candidat : candidats) {
			counter = 0;
			counter = stringOccur(articleText.toLowerCase(), candidat.getCandidatName().toString().toLowerCase());
			if(counter != 0) {
				rssCounterList.add(new RSSCounter(candidat.getCandidatName().toString(), counter));
			}
		}
		
		return rssCounterList;
	}
	
	/**
	 * Renvoie le nombre d'occurrences de la sous-chaine de caractères spécifiée dans la chaine de caractères spécifiée
	 * @param text chaine de caractères initiale
	 * @param string sous-chaine de caractères dont le nombre d'occurrences doit etre compté
	 * @return le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
	 */
	public static final int stringOccur(String text, String string) {
		return regexOccur(text, Pattern.quote(string));
	}
	
	/**
	 * Renvoie le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
	 * @param text chaine de caractères initiale
	 * @param regex expression régulière dont le nombre d'occurrences doit etre compté
	 * @return le nombre d'occurrences du pattern spécifié dans la chaine de caractères spécifiée
	 */
	public static final int regexOccur(String text, String regex) {
		Matcher matcher = Pattern.compile(regex).matcher(text);
		int occur = 0;
		while(matcher.find()) {
			occur ++;
		}
		return occur;
	}
	
	public class RSSCounter {
		public final String candidatName;
		public final int score;
		
		public RSSCounter(String candidatName, int score) {
			this.candidatName = candidatName;
			this.score = score;
		}
	}
	
}
