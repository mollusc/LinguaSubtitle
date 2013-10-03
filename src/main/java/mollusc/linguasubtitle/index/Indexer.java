package mollusc.linguasubtitle.index;

import mollusc.linguasubtitle.stemming.Stemmator;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 06.09.13
 * Index of content subtitle
 */
public class Indexer implements Iterable<String> {

	//<editor-fold desc="Private Field">
	/**
	 * Word index
	 */
	private Map<String, ArrayList<IndexWord>> index;

	/**
	 * Subtitle
	 */
	private final Subtitle subtitle;

	/**
	 * Language of the subtitle
	 */
	private final String language;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class Indexer
	 *
	 * @param subtitle container of speeches
	 * @param language language of subtitle
	 */
	public Indexer(Subtitle subtitle, String language) {
		this.subtitle = subtitle;
		this.language = language;
		initialIndex();
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">

	/**
	 * Get the list of stems
	 *
	 * @return Keys is a stem object, Value is quantity of the stem in the
	 *         subtitles
	 */
	public Map<Stemmator, Integer> getListStems() {
		Map<Stemmator, Integer> result = new HashMap<Stemmator, Integer>();
		for (String stemString : index.keySet()) {
			ArrayList<IndexWord> indexWords = index.get(stemString);

			String currentWord = "";
			for (IndexWord indexWord : indexWords) {
				Speech speech = subtitle.getSpeech(indexWord.indexSpeech);
				String word = speech.content.substring(indexWord.start, indexWord.end);

				if (currentWord.isEmpty()) {
					currentWord = word;
					continue;
				}

				if (word.length() < currentWord.length())
					currentWord = word;

				if (Character.isLowerCase(word.charAt(0)) || Character.isLowerCase(currentWord.charAt(0)))
					currentWord = currentWord.toLowerCase();
			}
			if (!currentWord.isEmpty()) {
				Stemmator stemmator = new Stemmator(currentWord, language);
				result.put(stemmator, indexWords.size());
			}
		}
		return result;
	}

	/**
	 * Get list of IndexWord by key - stemString
	 *
	 * @param stemString stem of the word
	 * @return array of IndexWord
	 */
	public ArrayList<IndexWord> get(String stemString) {
		return index.get(stemString);
	}

	@Override
	public Iterator<String> iterator() {
		return index.keySet().iterator();
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">

	/**
	 * Index initialization
	 */
	private void initialIndex() {
		index = new HashMap<String, ArrayList<IndexWord>>();
		int indexSpeech = 0;
		for (Speech speech : subtitle) {
			String text = speech.content;
			Matcher matcherTag = Pattern.compile("<[^>]*>").matcher(text);
			while (matcherTag.find()) {
				String fill = repeatChar(' ', matcherTag.group().length());
				text = text.substring(0, matcherTag.start()) + fill + text.substring(matcherTag.end(), text.length());
			}


			Pattern patternWord = Pattern.compile("[\\p{L}']{3,}");
			Matcher matcherWord = patternWord.matcher(text);
			while (matcherWord.find()) {
				String word = matcherWord.group();
				String stem = Stemmator.stemmingWord(word.toLowerCase(), language);
				if (!index.containsKey(stem))
					index.put(stem, new ArrayList<IndexWord>());
				IndexWord indexWord = new IndexWord(word, stem, indexSpeech, matcherWord.start(), matcherWord.end());
				index.get(stem).add(indexWord);
			}
			indexSpeech++;
		}
	}

	/**
	 * Repeat char c n times
	 */
	private static String repeatChar(char c, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
	//</editor-fold>
}
