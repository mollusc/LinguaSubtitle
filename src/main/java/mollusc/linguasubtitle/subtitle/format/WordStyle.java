package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.WordInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 */
public class WordStyle {

	//<editor-fold desc="Private Fields">
	private Map<String, WordInfo> _idexTranslatedWordInfo;
	private Map<String, String> _color;
	private Map<String, String> _translateColor;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public WordStyle() {
		_idexTranslatedWordInfo = new HashMap<String, WordInfo>();
		_color = new HashMap<String, String>();
		_translateColor = new HashMap<String, String>();
	}

	/**
	 * Initialize WordStyle
	 * @param wordInfos array of WordInfo
	 * @param hardWords array of hard words
	 * @param colorStudiedWords color of a studied words
	 * @param colorNameWords color of a name
	 * @param colorHardWord color of a hard words
	 * @param colorUnknownWords color of an unknown words
	 * @param colorTranslateWords color of a translate
	 */
	public WordStyle(ArrayList<WordInfo> wordInfos,
					 ArrayList<String> hardWords,
					 String colorStudiedWords,
					 String colorNameWords,
					 String colorHardWord,
					 String colorUnknownWords,
					 String colorTranslateWords)
	{
		this();
		initializeIdexTranslatedWordInfo(wordInfos);
		initializeColor(wordInfos, hardWords,colorStudiedWords, colorNameWords, colorHardWord, colorUnknownWords);
		initializeColorsTranslate(wordInfos,colorTranslateWords);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">

	/**
	 * Get color of the word
	 * @param stem stem of the word
	 * @return
	 */
	public String getColor(String stem)
	{
		if (_color.containsKey(stem))
			return _color.get(stem);
		return null;
	}

	/**
	 * Get WordInfo of the word
	 * @param stem stem of the word
	 * @return
	 */
	public WordInfo getTranslatedWordInfo(String stem)
	{
		if (_idexTranslatedWordInfo.containsKey(stem))
			return _idexTranslatedWordInfo.get(stem);
		return null;
	}


	/**
	 * Get color of the translate
	 * @param stem stem of the word
	 * @return
	 */
	public String getTranslateColor(String stem)
	{
		if (_translateColor.containsKey(stem))
			return _translateColor.get(stem);
		return null;
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">
	private void initializeIdexTranslatedWordInfo(ArrayList<WordInfo> wordInfos) {
		for (WordInfo wordInfo : wordInfos) {
			if (!wordInfo.isName() && !wordInfo.isStudy() && !wordInfo.isKnown())
				_idexTranslatedWordInfo.put(wordInfo.getStem(), wordInfo);
		}
	}

	private void initializeColor(ArrayList<WordInfo> wordInfos,
								 ArrayList<String> hardWords,
	                             String colorStudiedWords,
								 String colorNameWords,
								 String colorHardWord,
								 String colorUnknownWords) {
		for (WordInfo wordInfo : wordInfos) {
			if (wordInfo.isKnown()) continue;

			if (wordInfo.isStudy()) {
				_color.put(wordInfo.getStem(), colorStudiedWords);
				continue;
			}

			if (wordInfo.isName()) {
				_color.put(wordInfo.getStem(), colorNameWords);
				continue;
			}

			if (hardWords != null && hardWords.contains(wordInfo.getStem()))
				_color.put(wordInfo.getStem(), colorHardWord);
			else
				_color.put(wordInfo.getStem(), colorUnknownWords);
		}
	}

	private void initializeColorsTranslate(ArrayList<WordInfo> wordInfos, String colorTranslateWords ) {
		for (WordInfo wordInfo : wordInfos) {
			if (!wordInfo.isName() && !wordInfo.isStudy() && !wordInfo.isKnown()) {
				_translateColor.put(wordInfo.getStem(), colorTranslateWords);
			}
		}
	}
	//</editor-fold>
}
