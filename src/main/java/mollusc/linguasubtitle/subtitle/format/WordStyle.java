package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.Settings;
import mollusc.linguasubtitle.WordInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 * <p/>
 * Class that keeps information about words
 */
public class WordStyle {

	//<editor-fold desc="Private Fields">
	/**
	 * Information of translated words. Key is a stem of a word, Value is
	 * information about it.
	 */
	private final Map<String, WordInfo> _indexTranslatedWordInfo;
	/**
	 * Color of words. Key is a stem of a word, Value is color of a word.
	 */
	private final Map<String, String> _color;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class WordStyle
	 */
	private WordStyle() {
		_indexTranslatedWordInfo = new HashMap<String, WordInfo>();
		_color = new HashMap<String, String>();
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">

	/**
	 * Constructor of the WordStyle class
	 *
	 * @param wordInfos array of WordInfo
	 * @param hardWords array of hard words
	 */
	public WordStyle(ArrayList<WordInfo> wordInfos,
					 ArrayList<String> hardWords,
					 Settings settings) {
		this();
		initializeIndexTranslatedWordInfo(wordInfos);
		initializeColor(wordInfos, hardWords, settings);
	}

	/**
	 * Get color of the word
	 *
	 * @param stem stem of the word
	 * @return color of the word
	 */
	public String getColor(String stem) {
		if (_color.containsKey(stem))
			return _color.get(stem);
		return null;
	}

	/**
	 * Get WordInfo of the word
	 *
	 * @param stem stem of the word
	 * @return WordInfo of the word
	 */
	public WordInfo getTranslatedWordInfo(String stem) {
		if (_indexTranslatedWordInfo.containsKey(stem))
			return _indexTranslatedWordInfo.get(stem);
		return null;
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">

	/**
	 * Initialize field _indexTranslatedWordInfo
	 *
	 * @param wordInfos array of WordInfo
	 */
	private void initializeIndexTranslatedWordInfo(ArrayList<WordInfo> wordInfos) {
		for (WordInfo wordInfo : wordInfos) {
			if (!wordInfo.isName() && !wordInfo.isStudy() && !wordInfo.isKnown())
				_indexTranslatedWordInfo.put(wordInfo.getStem(), wordInfo);
		}
	}

	/**
	 * Initialize field _color
	 *
	 * @param wordInfos array of WordInfo
	 * @param hardWords array of hard words
	 * @param settings  settings of the program
	 */
	private void initializeColor(ArrayList<WordInfo> wordInfos,
								 ArrayList<String> hardWords,
								 Settings settings) {
		for (WordInfo wordInfo : wordInfos) {
			if (wordInfo.isKnown()) continue;

			if (wordInfo.isStudy()) {
				_color.put(wordInfo.getStem(), settings.getColorStudiedWords());
				continue;
			}

			if (wordInfo.isName()) {
				_color.put(wordInfo.getStem(), settings.getColorNameWords());
				continue;
			}

			if (hardWords != null && hardWords.contains(wordInfo.getStem()))
				_color.put(wordInfo.getStem(), settings.getColorHardWord());
			else
				_color.put(wordInfo.getStem(), settings.getColorUnknownWords());
		}
	}
	//</editor-fold>
}
