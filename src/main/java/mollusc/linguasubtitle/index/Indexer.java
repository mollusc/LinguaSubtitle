package mollusc.linguasubtitle.index;

import mollusc.linguasubtitle.stemming.Stemmator;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
				String word = speech.content.substring(indexWord.start,
						indexWord.end);

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
		int idSpeech = 0;
		for (Speech speech : subtitle) {
			boolean isTag = false;
			String word = "";
			for (int j = 0; j < speech.content.length(); j++) {
				char ch = speech.content.charAt(j);
				if (!isTag) {
					if (!Character.isLetter(ch) && ch != '\'') {
						if (word.length() > 2 && !CommonUtility.tryParseInt(word)) {
							String stem = Stemmator.stemmingWord(word, language);
							if (!index.containsKey(stem))
								index.put(stem, new ArrayList<IndexWord>());
							IndexWord indexWord = new IndexWord(word, stem, idSpeech, j - word.length(), j);
							index.get(stem).add(indexWord);
						}
						word = "";
					} else {
						word += Character.toLowerCase(ch);
					}
				}
				if (ch == '<')
					isTag = true;

				if (ch == '>')
					isTag = false;
			}
			idSpeech++;
		}
	}
	//</editor-fold>
}
