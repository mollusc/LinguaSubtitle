package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.Settings;
import mollusc.linguasubtitle.VideoConfiguration;
import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.IndexWordComparator;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.AdvancedSubStationAlphaUtility;
import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static mollusc.linguasubtitle.subtitle.utility.CommonUtility.toHexString;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 20.09.13
 * <p/>
 * Class for generate an Advanced SubStation Alpha subtitle
 */
public class AdvancedSubStationAlphaRender extends Render {
	//<editor-fold desc="Private Fields">
	/**
	 * Transparency of known words
	 */
	private final String transparencyKnownWords;
	/**
	 * Horizontal video resolution
	 */
	private final int playResX;
	/**
	 * Vertical video resolution
	 */
	private final int playResY;
	/**
	 * Name of a font
	 */
	private final String fontName;
	/**
	 * Font size of speeches
	 */
	private final int mainFontSize;
	/**
	 * Font siz of translations
	 */
	private final int translateFontSize;
	/**
	 * Current horizontal margin for translations
	 */
	private double translateMarginH;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class AdvancedSubStationAlphaRender
	 *
	 * @param subtitle  container for speeches
	 * @param wordStyle style of words
	 * @param indexer   index of the text subtitle
	 * @param settings  settings of the program
	 */
	public AdvancedSubStationAlphaRender(Subtitle subtitle,
										 WordStyle wordStyle,
										 Indexer indexer,
										 Settings settings) {
		super(subtitle, wordStyle, indexer, settings);

		this.fontName = settings.getFontName();
		this.mainFontSize = settings.getMainFontSize();
		this.translateFontSize = settings.getTranslateFontSize();
		this.transparencyKnownWords = settings.getTransparencyKnownWords();
		this.playResX = settings.getPlayResX();
		this.playResY = settings.getPlayResY();
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public void save(String pathToSave) {
		String textSubtitle = getScriptInfo();
		textSubtitle += getStyles();
		textSubtitle += "[Events]\n" +
				"Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n";
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndicesByIndexSpeech();
		ArrayList<Integer> editedIndexSpeeches = getEditedIndexSpeeches();
		int indexSpeech = -1;
		for (Speech speech : subtitle) {
			indexSpeech++;
			if (hideKnownDialog && !editedIndexSpeeches.contains(indexSpeech))
				continue;
			String textSpeech = speech.content;
			ArrayList<IndexWord> indexWords = indices.get(indexSpeech);
			ArrayList<String> translates = new ArrayList<String>();
			if (indexWords != null) {
				Collections.sort(indexWords, new IndexWordComparator());
				int prevStart = -1;
				for (IndexWord indexWord : indexWords) {
					String word = indexWord.word;
					String stem = indexWord.stem;
					if (wordStyle.getColor(stem) != null) {
						String left = textSpeech.substring(0, indexWord.start);
						int start = CommonUtility.html2text(left).length();
						if (wordStyle.getTranslatedWordInfo(stem) != null) {
							String translate = wordStyle.getTranslatedWordInfo(stem).getTranslate();
							if (translate != null && !translate.equals("")) {
								translates.add(InsertWordTranslation(translate, start, prevStart, CommonUtility.html2text(speech.content)));
								prevStart = start;
							}
						}
						String middle = "{\\alpha&H00&, \\c&H" + RGBtoBGR(wordStyle.getColor(stem)) + "&}" + word + "{\\alpha,\\c }";
						String right = textSpeech.substring(indexWord.end);
						textSpeech = left + middle + right;
					}
				}
			}
			textSpeech = clearSpeech(textSpeech);

			// Get next speech index
			int nextIndexSpeech = indexSpeech + 1;
			while (hideKnownDialog && !editedIndexSpeeches.contains(nextIndexSpeech) && nextIndexSpeech < subtitle.size())
				nextIndexSpeech++;

			// Create a speech with translates
			String timeStamp = getTimeStamp(speech, subtitle.getSpeech(nextIndexSpeech));
			textSubtitle += join(0, timeStamp, ",Default, NTP, 0, 0, 0,!Effect," + textSpeech);
			int i = 1;
			for (String t : translates) {
				textSubtitle += join(i, timeStamp, t);
				i++;
			}
		}
		saveSubtitle(pathToSave, textSubtitle);
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">

	/**
	 * Replace html tags by ass tags
	 *
	 * @param textSpeech speech
	 * @return cleaned text
	 */
	private String clearSpeech(String textSpeech) {
		textSpeech = textSpeech.replaceAll("\n", "\\\\N\\\\N");
		textSpeech = textSpeech.replaceAll("<b>", "{\\\\b1}");
		textSpeech = textSpeech.replaceAll("</b>", "{\\\\b0}");
		textSpeech = textSpeech.replaceAll("<i>", "{\\\\i1}");
		textSpeech = textSpeech.replaceAll("</i>", "{\\\\i0}");
		textSpeech = textSpeech.replaceAll("<u>", "{\\\\u1}");
		textSpeech = textSpeech.replaceAll("</u>", "{\\\\u0}");
		textSpeech = textSpeech.replaceAll("<s>", "{\\\\s1}");
		textSpeech = textSpeech.replaceAll("</s>", "{\\\\s0}");
		return CommonUtility.html2text(textSpeech);
	}

	/**
	 * Insert translation in current position
	 *
	 * @param wordTranslate translation
	 * @param start         start position of the translation
	 * @param prevStart previous position of a translation
	 * @param textSpeech    speech
	 * @return ass script line for translation
	 */
	private String InsertWordTranslation(String wordTranslate,
										 int start,
										 int prevStart,
										 final String textSpeech) {
		String scriptLine = "";
		String[] lines = textSpeech.split("\\n");
		int minPos = 0;
		int maxPos = 0;
		int marginV = lines.length * mainFontSize + (lines.length - 1) * mainFontSize / 2;
		for (String line : lines) {
			maxPos += line.length();
			if (start >= minPos && start < maxPos) {
				if (!(prevStart >= minPos && prevStart < maxPos))
					translateMarginH = playResX / 2;
				double totalWidthPixel = getStringWidth(line, mainFontSize);
				double startPixel = getStringWidth(line.substring(0, start - minPos), mainFontSize);
				double from = startPixel - totalWidthPixel / 2;
				double translateWidthPixel = getStringWidth(wordTranslate, translateFontSize);
				double spaceWidthPixel = getStringWidth(" ", translateFontSize);
				int scaleX = 100;
				if ((from + translateWidthPixel + spaceWidthPixel) > translateMarginH) {
					// Try to fit the translation by scaling
					double newTranslateWidthPixel = translateMarginH - from;
					if (newTranslateWidthPixel / (translateWidthPixel + spaceWidthPixel) < 0.6)
						scaleX = 60;
					else
						scaleX = (int) (newTranslateWidthPixel / (translateWidthPixel + spaceWidthPixel) * 100.0);
					translateWidthPixel = translateWidthPixel * scaleX / 100.0;

					// Try to fit the translation by cutting
					while ((from + translateWidthPixel + spaceWidthPixel * scaleX / 100.0) > translateMarginH) {
						wordTranslate = wordTranslate.substring(0, wordTranslate.length() - 2);
						wordTranslate += '…';
						translateWidthPixel = getStringWidth(wordTranslate, translateFontSize) * scaleX / 100.0;
					}
				}
				// Create a line in the script
				double margin = from + translateWidthPixel / 2;
				translateMarginH = from;
				int marginL = (int) margin > 0 ? (int) margin : 0;
				int marginR = (int) margin <= 0 ? (int) -margin : 0;
				scriptLine = ",Translate, NTP, " + marginL * 2 + ", " + marginR * 2 + ", " + marginV + ",!Effect,{\\fscx" + scaleX + "}" + wordTranslate;
				break;
			}
			minPos = maxPos + 1;
			marginV = marginV - mainFontSize - mainFontSize / 2;
		}
		return scriptLine;
	}

	/**
	 * Join all components of the ass script line
	 *
	 * @param layer      layer of the dialogue
	 * @param timeStamp  time stamp of the dialogue
	 * @param textSpeech text of the dialogue
	 * @return ass script line
	 */
	private String join(int layer, String timeStamp, String textSpeech) {
		return "Dialogue: " + layer + "," + timeStamp + textSpeech + "\n";
	}

	/**
	 * Get time stamp of the speech
	 *
	 * @param currentSpeech current speech
	 * @param nextSpeech    next speech
	 * @return string with time stamp
	 */
	private String getTimeStamp(Speech currentSpeech, Speech nextSpeech) {
		if (automaticDuration) {
			int currentDuration = currentSpeech.endTimeInMilliseconds - currentSpeech.startTimeInMilliseconds;
			int newDuration = CommonUtility.html2text(currentSpeech.content).length() * millisecondsPerCharacter;

			if (newDuration > currentDuration) {
				int newEndTime = currentSpeech.startTimeInMilliseconds + newDuration;
				if (nextSpeech != null && newEndTime > nextSpeech.startTimeInMilliseconds)
					newEndTime = nextSpeech.startTimeInMilliseconds - 10; // 10 is space between subtitles
				return AdvancedSubStationAlphaUtility.getTimeStamp(currentSpeech.startTimeInMilliseconds, newEndTime);
			}
		}
		return AdvancedSubStationAlphaUtility.getTimeStamp(currentSpeech.startTimeInMilliseconds, currentSpeech.endTimeInMilliseconds);
	}

	/**
	 * Get header of ass subtitle
	 *
	 * @return ass script lines
	 */
	private String getScriptInfo() {
		return "[Script Info]\n" +
				"Original Script: LinguaSubtitle 2.4 http://sourceforge.net/projects/linguasubtitle/\n" +
				"ScriptType: v4.00+\n" +
				"Collisions: Normal\n" +
				"WrapStyle: 1\n" +
				"PlayResX: " + String.format("%d", (int) playResX) + "\n" +
				"PlayResY: " + String.format("%d", (int) playResY) + "\n" +
				"Timer: 100.0000\n\n";
	}

	/**
	 * Get styles information
	 *
	 * @return ass script lines
	 */
	private String getStyles() {
		int Outline = (int) Math.ceil(mainFontSize / 24.0);
		int Shadow = Outline;
		return "[V4+ Styles]\n" +
				"Format:Name,Fontname,Fontsize,PrimaryColour,SecondaryColour,OutlineColour,BackColour,Bold,Italic,Underline,StrikeOut,ScaleX,ScaleY,Spacing,Angle,BorderStyle,Outline,Shadow,Alignment,MarginL,MarginR,MarginV,Encoding\n" +
				"Style:Default," + fontName + "," + mainFontSize + ",&H" + transparencyKnownWords + RGBtoBGR(textColor) + ",&H" + transparencyKnownWords + "FFFFFF,&H" + transparencyKnownWords + "000000, &HFF000000,0,0,0,0,100,100,0,0,1,2," + Outline + "," + Shadow + ",0,0,10,1\n" +
				"Style:Translate," + fontName + "," + translateFontSize + ",&H00" + RGBtoBGR(translateColor) + ",&H00FFFFFF,&H00000000,&HFF000000,0,0,0,0,100,100,0,0,1,2," + Outline + "," + Shadow + ",0,0,10,1\n\n";
	}

	/**
	 * Convert color from a RGB format to a BGR format
	 *
	 * @param RGBColor color in RGB format
	 * @return color in BGR format
	 */
	private String RGBtoBGR(String RGBColor) {
		int in = Integer.decode("#" + RGBColor);
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in) & 0xFF;
		int out = (blue << 16) | (green << 8) | (red);
		Color c = new Color(out);
		return toHexString(c);
	}

	/**
	 * Get text width in pixels
	 *
	 * @param text     measured text
	 * @param fontSize font size of the text
	 * @return width in pixels
	 */
	private double getStringWidth(String text, int fontSize) {
		if (text.length() > 0) {
			Font font = new Font(fontName, Font.PLAIN, fontSize);
			TextLayout textLayout = new TextLayout(text, font, new FontRenderContext(null, true, true));
			return textLayout.getAdvance() * fontSize / (textLayout.getAscent() + textLayout.getDescent());
		}
		return 0;
	}
	//</editor-fold>

}
