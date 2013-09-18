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
	private Subtitle subtitle;

	/**
	 * Language of the subtitle
	 */
	private String language;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public Indexer(Subtitle subtitle, String language)
	{
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
	 * subtitles
	 */
	public Map<Stemmator, Integer> getListStems() {
		Map<Stemmator, Integer> result = new HashMap<Stemmator, Integer>();
		for (String stemString : index.keySet()) {
			ArrayList<IndexWord> indexWords = index.get(stemString);

			String currentWord = "";
			for (IndexWord indexWord : indexWords) {
				Speech speech = subtitle.getSpeech(indexWord.indexSpeech);
				String word = speech.content.substring(indexWord.start,indexWord.end);

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
	 * @param stemString
	 * @return
	 */
	public ArrayList<IndexWord> get(String stemString)
	{
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
		ArrayList<Pair<Integer, Integer>> excludePosition = new ArrayList<Pair<Integer, Integer>>();
		int indexSpeech = -1;
		for (Speech speech : subtitle) {
			indexSpeech++;
			Pattern patternTag = Pattern.compile("<[^>]*>");
			Matcher matcherTag = patternTag.matcher(speech.content);
			while (matcherTag.find()) {
				excludePosition.add(new Pair<Integer, Integer>(matcherTag.start(), matcherTag.end()));
			}


			Pattern patternWord = Pattern.compile("[\\w']{3,}");
			Matcher matcherWord = patternWord.matcher(speech.content);
			while (matcherWord.find()) {
				boolean isExclude = false;
				for (Pair<Integer,Integer> pair : excludePosition)
				{
					if(pair.getLeft() < matcherWord.start() && matcherWord.end() < pair.getRight())
					{
						isExclude = true;
						break;
					}
				}
				String word = matcherWord.group();
				if(isExclude || Character.isDigit(word.charAt(0)))
					continue;

				String stem = Stemmator.stemmingWord(word.toLowerCase(), language);
				if (!index.containsKey(stem))
					index.put(stem, new ArrayList<IndexWord>());
				IndexWord indexWord = new IndexWord(word, stem, indexSpeech, matcherWord.start(), matcherWord.end());
				index.get(stem).add(indexWord);
			}
		}
	}

	//</editor-fold>
}
