package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.Settings;
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
	//private Map<String, String> _translateColor;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public WordStyle() {
		_idexTranslatedWordInfo = new HashMap<String, WordInfo>();
		_color = new HashMap<String, String>();
		//_translateColor = new HashMap<String, String>();
	}

	/**
	 * Initialize WordStyle
	 * @param wordInfos array of WordInfo
	 * @param hardWords array of hard words
	 */
	public WordStyle(ArrayList<WordInfo> wordInfos,
					 ArrayList<String> hardWords,
					 Settings settings)
	{
		this();
		initializeIdexTranslatedWordInfo(wordInfos);
		initializeColor(wordInfos, hardWords, settings);
		//initializeColorsTranslate(wordInfos, settings);
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
	/*public String getTranslateColor(String stem)
	{
		if (_translateColor.containsKey(stem))
			return _translateColor.get(stem);
		return null;
	}*/
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

	/*private void initializeColorsTranslate(ArrayList<WordInfo> wordInfos, Settings settings) {
		for (WordInfo wordInfo : wordInfos) {
			if (!wordInfo.isName() && !wordInfo.isStudy() && !wordInfo.isKnown()) {
				_translateColor.put(wordInfo.getStem(), settings.getColorTranslateWords());
			}
		}
	}*/
	//</editor-fold>
}
