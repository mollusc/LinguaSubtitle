package mollusc.linguasubtitle;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 */
public class WordInfo {
	//<editor-fold desc="Private Field">
	private String _word;
	private String _stem;
	private String _translate;
	private boolean _known;
	private boolean _study;
	private boolean _name;
	//</editor-fold>

	//<editor-fold desc="Constructor">
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
	public boolean isKnown(){
		return _known;
	}

	public boolean isStudy(){
		return _study;
	}

	public boolean isName(){
		return _name;
	}

	public String getTranslate(){
		return _translate;
	}

	public String getStem(){
		return _stem;
	}

	public String getWord(){
		return _word;
	}
	//</editor-fold>
}
