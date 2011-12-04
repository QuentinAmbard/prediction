package com.avricot.prediction.web.service;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.repository.tweet.TweetRepository;

@Service
public class ImageTweetService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ImageTweetService.class);
	@Inject
	TweetRepository tweetRepository;

	private static final int HEIGHT = 9;
	private static final int WIDTH = 6;

	private final HashMap<CandidatName, String> cachedData = new HashMap<Candidat.CandidatName, String>();

	public String getData(CandidatName candidatName) {
		if (!cachedData.containsKey(candidatName)) {
			try {
				initData(candidatName);
			} catch (IOException e) {
				LOGGER.error("pb with candidatName=" + candidatName.name(), e);
				return null;
			}
		}
		return cachedData.get(candidatName);
	}

	public void initData(CandidatName candidatName) throws IOException {

		String fileName = candidatName.toString().toLowerCase() + ".jpg";
		List<Tweet> tweets = tweetRepository.findByCandidatName(candidatName, 100);
		String currentTweet = tweets.get(0).getValue().replaceAll(" ", "");
		cachedData.put(candidatName, this.createString(currentTweet, tweets, fileName));
	}

	private String createString(String currentTweet, List<Tweet> tweets, String fileName) throws IOException {
		Image img = null;
		ClassPathResource resource = new ClassPathResource("image/" + fileName);
		img = ImageIO.read(resource.getInputStream());
		System.out.println(img.getWidth(null) + " " + img.getHeight(null));
		System.out.println();
		return handlepixels(img, 0, 0, img.getWidth(null), img.getHeight(null), currentTweet, tweets);
	}

	private void handlesinglepixel(int x, int y, int pixel, int position, int[] redArray, int[] greenArray, int[] blueArray) {
		int alpha = (pixel >> 24) & 0xff;
		redArray[position] = (pixel >> 16) & 0xff;
		greenArray[position] = (pixel >> 8) & 0xff;
		blueArray[position] = (pixel) & 0xff;
		// Deal with the pixel as necessary...
	}

	private String handlepixels(Image img, int x, int y, int w, int h, String currentTweet, List<Tweet> tweets) throws IOException {
		int[] pixels = new int[w * h];
		int[] redArray = new int[w * h];
		int[] greenArray = new int[w * h];
		int[] blueArray = new int[w * h];
		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, pixels, 0, w);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("interrupted waiting for pixels!");
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
			System.err.println("image fetch aborted or errored");
		}
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				handlesinglepixel(x + i, y + j, pixels[j * w + i], j * w + i, redArray, greenArray, blueArray);
			}
		}
		return buildString(w, h, redArray, greenArray, blueArray, currentTweet, tweets);
	}

	private String buildString(int w, int h, int[] redArray, int[] greenArray, int[] blueArray, String currentTweet, List<Tweet> tweets) {
		StringBuilder stringBuild = new StringBuilder();
		stringBuild.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" /></head><body><p>");
		int tweetIndex = 0;
		int tweetOffset = 0;
		stringBuild.append("<a  target=\"_blank\" href=\"http://www.twitter.com/#!/" + tweets.get(tweetIndex).getUserId() + "\" id=" + tweetIndex + ">");
		int red;
		int green;
		int blue;
		String redString;
		String blueString;
		String greenString;
		for (int j = 0; j < Math.floor(h / HEIGHT); j++) {
			for (int i = 0; i < Math.floor(w / WIDTH); i++) {
				blue = green = red = 0;
				for (int a = 0; a < HEIGHT; a++) {
					for (int b = 0; b < WIDTH; b++) {
						blue += blueArray[(j * HEIGHT + a) * w + i * WIDTH + b];
						green += greenArray[(j * HEIGHT + a) * w + i * WIDTH + b];
						red += redArray[(j * HEIGHT + a) * w + i * WIDTH + b];
					}
				}
				red = red / (WIDTH * HEIGHT);
				green = green / (WIDTH * HEIGHT);
				blue = blue / (WIDTH * HEIGHT);
				redString = Integer.toHexString(red);
				greenString = Integer.toHexString(green);
				blueString = Integer.toHexString(blue);
				if (redString.length() == 1)
					redString = "0" + redString;
				if (greenString.length() == 1)
					greenString = "0" + greenString;
				if (blueString.length() == 1)
					blueString = "0" + blueString;
				if (currentTweet.length() <= j * (w / WIDTH) + i - tweetOffset) {
					tweetIndex++;
					currentTweet = tweets.get(tweetIndex).getValue().replaceAll(" ", "");
					tweetOffset = j * (w / WIDTH) + i;
					stringBuild.append("</a>");
					stringBuild.append("<a id=" + tweetIndex + " target=\"_blank\" href=\"http://www.twitter.com/#!/" + tweets.get(tweetIndex).getUserId() + "\">");
				}
				stringBuild.append("<font color=#" + redString + greenString + blueString + ">" + String.valueOf(currentTweet.charAt(j * (w / WIDTH) + i - tweetOffset))
						+ "</font>");
			}
			stringBuild.append("<br/>");
		}
		stringBuild.append("</p></body></html>");
		return stringBuild.toString();
	}
}
