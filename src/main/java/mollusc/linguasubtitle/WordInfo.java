package mollusc.linguasubtitle;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 */
public class WordInfo {
	//<editor-fold desc="Private Field">
	/**
	 * Word
	 */
	private final String _word;
	/**
	 * Stem of the word
	 */
	private final String _stem;
	/**
	 * Translation of the word
	 */
	private final String _translate;
	/**
	 * Is word known?
	 */
	private final boolean _known;
	/**
	 * Is word study?
	 */
	private final boolean _study;
	/**
	 * Is word name?
	 */
	private final boolean _name;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class WordInfo
	 *
	 * @param word      word
	 * @param stem      stem of the word
	 * @param translate translation of the word
	 * @param known     Is word known?
	 * @param study     Is word study?
	 * @param name      Is word name?
	 */
	public WordInfo(String word, String stem, String translate, boolean known, boolean study, boolean name) {
		_word = word;
		_stem = stem;
		_translate = translate;
		_known = known;
		_study = study;
		_name = name;
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	public boolean isKnown() {
		return _known;
	}

	public boolean isStudy() {
		return _study;
	}

	public boolean isName() {
		return _name;
	}

	public String getTranslate() {
		return _translate;
	}

	public String getStem() {
		return _stem;
	}

	public String getWord() {
		return _word;
	}
	//</editor-fold>
}
