package mollusc.linguasubtitle;

import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 30.09.13
 *
 * Class that keeps settings of the program
 */
public class Settings {

	//<editor-fold desc="Private Fields">
	/**
	 * Map of parameter - values
	 */
	private Map<String, String> values;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class Settings
	 * @param values settings of the program
	 */
	public Settings(Map<String, String> values)
	{
		this.values = values;
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	public String getFontName(){
		return values.containsKey("fontName") ?
				values.get("fontName"):
				"Arial";
	}

	public void setFontName(String value){
		values.put("fontName",value);
	}

	public Integer getMainFontSize(){
		return values.containsKey("mainFontSize") && CommonUtility.tryParseInt(values.get("mainFontSize")) ?
				Integer.parseInt(values.get("mainFontSize")):
				48;
	}

	public void setMainFontSize(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("mainFontSize", value);
	}

	public Integer getTranslateFontSize(){
		return values.containsKey("translateFontSize") && CommonUtility.tryParseInt(values.get("translateFontSize")) ?
				Integer.parseInt(values.get("translateFontSize")):
				36;
	}

	public void setTranslateFontSize(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("translateFontSize", value);
	}

	public String getTransparencyKnownWords(){
		return values.containsKey("transparencyKnownWords") ?
				values.get("transparencyKnownWords").toUpperCase():
				"B4";
	}

	public void setTransparencyKnownWords(String value){
		values.put("transparencyKnownWords", value.toUpperCase());
	}

	public boolean getExportUnknownWords(){
		return values.containsKey("exportUnknownWords") ?
				values.get("exportUnknownWords").equals("1"):
				true;
	}

	public void setExportUnknownWords(boolean value){
		values.put("exportUnknownWords", value ? "1" : "0");
	}

	public boolean getExportStudyWords(){
		return values.containsKey("exportStudyWords") ?
				values.get("exportStudyWords").equals("1"):
				false;
	}

	public void setExportStudyWords(boolean value){
		values.put("exportStudyWords", value ? "1" : "0");
	}

	public boolean getExportKnownWords(){
		return values.containsKey("exportKnownWords") ?
				values.get("exportKnownWords").equals("1"):
				false;
	}

	public void setExportKnownWords(boolean value){
		values.put("exportKnownWords", value ? "1" : "0");
	}

	public boolean getNoBlankTranslation(){
		return values.containsKey("noBlankTranslation") ?
				values.get("noBlankTranslation").equals("1"):
				false;
	}

	public void setNoBlankTranslation(boolean value){
		values.put("noBlankTranslation", value ? "1" : "0");
	}

	public Integer getExportMoreThan(){
		return values.containsKey("exportMoreThan") && CommonUtility.tryParseInt(values.get("exportMoreThan")) ?
				Integer.parseInt(values.get("exportMoreThan")):
				10;
	}

	public void setExportMoreThan(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("exportMoreThan", value);
	}

	public String getExportLanguage(){
		return values.containsKey("exportLanguage") ?
				values.get("exportLanguage"):
				"English";
	}

	public void setExportLanguage(String value){
		values.put("exportLanguage",value);
	}

	public String getColorKnownWords(){
		return values.containsKey("colorKnownWords") ?
				values.get("colorKnownWords").toUpperCase():
				"999999";
	}

	public void setColorKnownWords(String value){
		values.put("colorKnownWords",value.toUpperCase());
	}

	public String getColorUnknownWords(){
		return values.containsKey("colorUnknownWords") ?
				values.get("colorUnknownWords").toUpperCase():
				"FFFFFF";
	}

	public void setColorUnknownWords(String value){
		values.put("colorUnknownWords",value.toUpperCase());
	}

	public String getColorTranslateWords(){
		return values.containsKey("colorTranslateWords") ?
				values.get("colorTranslateWords").toUpperCase():
				"CCFFCC";
	}

	public void setColorTranslateWords(String value){
		values.put("colorTranslateWords",value.toUpperCase());
	}

	public String getColorHardWord(){
		return values.containsKey("colorHardWord") ?
				values.get("colorHardWord").toUpperCase():
				"FFCCCC";
	}

	public void setColorHardWord(String value){
		values.put("colorHardWord",value.toUpperCase());
	}

	public String getColorNameWords(){
		return values.containsKey("colorNameWords") ?
				values.get("colorNameWords").toUpperCase():
				"CCCCFF";
	}

	public void setColorNameWords(String value){
		values.put("colorNameWords",value.toUpperCase());
	}

	public String getColorStudiedWords(){
		return values.containsKey("colorStudiedWords") ?
				values.get("colorStudiedWords").toUpperCase():
				"FFFF33";
	}

	public void setColorStudiedWords(String value){
		values.put("colorStudiedWords",value.toUpperCase());
	}

	public Integer getMillisecondsPerCharacter(){
		return values.containsKey("millisecondsPerCharacter") && CommonUtility.tryParseInt(values.get("millisecondsPerCharacter")) ?
				Integer.parseInt(values.get("millisecondsPerCharacter")):
				100;
	}

	public void setMillisecondsPerCharacter(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("millisecondsPerCharacter", value);
	}

	public boolean getAutomaticDurations(){
		return values.containsKey("automaticDurations") ?
				values.get("automaticDurations").equals("1"):
				true;
	}

	public void setAutomaticDurations(boolean value){
		values.put("automaticDurations", value ? "1" : "0");
	}

	public boolean getHideKnownDialog(){
		return values.containsKey("hideKnownDialog") ?
				values.get("hideKnownDialog").equals("1"):
				true;
	}

	public void setHideKnownDialog(boolean value){
		values.put("hideKnownDialog", value ? "1" : "0");
	}


	public String getLanguage(){
		return values.containsKey("language") ?
				values.get("language"):
				"English";
	}

	public void setLanguage(String value){
		values.put("language",value);
	}

	public Integer getPlayResX(){
		return values.containsKey("playResX") && CommonUtility.tryParseInt(values.get("playResX")) ?
				Integer.parseInt(values.get("playResX")):
				1280;
	}

	public void setPlayResX(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("playResX", value);
	}

	public Integer getPlayResY(){
		return values.containsKey("playResY") && CommonUtility.tryParseInt(values.get("playResY")) ?
				Integer.parseInt(values.get("playResY")):
				720;
	}

	public void setPlayResY(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("playResY", value);
	}

	public Integer getIndexVideoResolution(){
		return values.containsKey("indexVideoResolution") && CommonUtility.tryParseInt(values.get("indexVideoResolution")) ?
				Integer.parseInt(values.get("indexVideoResolution")):
				7;
	}

	public void setIndexVideoResolution(String value){
		if (CommonUtility.tryParseInt(value))
			values.put("indexVideoResolution", value);
	}

	public String getDefaultFileFilter(){
		return values.containsKey("defaultFileFilter") ?
				values.get("defaultFileFilter"):
				"ass";
	}

	public void setDefaultFileFilter(String value){
		values.put("defaultFileFilter",value);
	}

	/**
	 * Get settings in the map
	 * @return map of parameter - values
	 */
	public Map<String, String> getMap(){
		setAutomaticDurations(getAutomaticDurations());
		setColorHardWord(getColorHardWord());
		setColorKnownWords(getColorKnownWords());
		setColorNameWords(getColorNameWords());
		setColorStudiedWords(getColorStudiedWords());
		setColorTranslateWords(getColorTranslateWords());
		setColorUnknownWords(getColorUnknownWords());
		setExportKnownWords(getExportKnownWords());
		setExportLanguage(getExportLanguage());
		setExportMoreThan(getExportMoreThan().toString());
		setExportStudyWords(getExportStudyWords());
		setExportUnknownWords(getExportUnknownWords());
		setFontName(getFontName());
		setHideKnownDialog(getHideKnownDialog());
		setMainFontSize(getMainFontSize().toString());
		setMillisecondsPerCharacter(getMillisecondsPerCharacter().toString());
		setNoBlankTranslation(getNoBlankTranslation());
		setTranslateFontSize(getTranslateFontSize().toString());
		setTransparencyKnownWords(getTransparencyKnownWords());
		setLanguage(getLanguage());
		setPlayResY(getPlayResY().toString());
		setPlayResX(getPlayResX().toString());
		setIndexVideoResolution(getIndexVideoResolution().toString());
		setDefaultFileFilter(getDefaultFileFilter());
		return values;
	}
	//</editor-fold>
}
