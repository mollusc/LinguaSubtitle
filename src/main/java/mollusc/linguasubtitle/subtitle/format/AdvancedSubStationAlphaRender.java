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

import static mollusc.linguasubtitle.Preferences.toHexString;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 20.09.13
 */
public class AdvancedSubStationAlphaRender extends Render {
	//<editor-fold desc="Private Fields">
	private String transparencyKnownWords;
	private int playResX;
	private int playResY;
	private String fontName;
	private int mainFontSize;
	private int translateFontSize;

	private double translateMariginV;
	private double translateMariginH;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Color of subtitle
	 * @param subtitle
	 * @param wordStyle
	 * @param indexer
	 * @param settings
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

		// Set playResX playResY
		VideoConfiguration f = new VideoConfiguration(settings);
		f.setVisible(true);
		f.pack();
		playResX = settings.getPlayResX();
		playResY = settings.getPlayResY();
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
				translateMariginV = 0;
				translateMariginH = 0;
				for (IndexWord indexWord : indexWords) {
					String word = indexWord.word;
					String stem = indexWord.stem;
					if (wordStyle.getColor(stem) != null) {
						String left = textSpeech.substring(0, indexWord.start);
						int start = CommonUtility.html2text(left).length();
						if (wordStyle.getTranslatedWordInfo(stem) != null) {
							String translate = wordStyle.getTranslatedWordInfo(stem).getTranslate();
							if (translate != null && !translate.equals("")) {
								InsertWordTranslation(translates, translate, start, CommonUtility.html2text(speech.content));
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

	private void InsertWordTranslation(ArrayList<String> translates,
									   String translate,
									   int start,
									   final String textSpeech) {
		String[] lines = textSpeech.split("\\n");
		int minPos = 0;
		int maxPos = 0;
		int marginV = lines.length * mainFontSize + (lines.length - 1) * mainFontSize / 2;
		for (String line : lines) {
			maxPos += line.length();
			if (start > minPos && start < maxPos) {
				double totalWidthPixel = getStringWidth(line, mainFontSize);
				double startPixel = getStringWidth(line.substring(0, start - minPos), mainFontSize);
				double from = startPixel - totalWidthPixel / 2;
				double translateWidthPixel = getStringWidth(translate + " ", translateFontSize);
				int scaleX = 100;
				if (marginV == translateMariginV && (from + translateWidthPixel) > translateMariginH) {
					// Try to fit the translation by scaling
					double newTranslateWidthPixel = translateMariginH - from;
					if (newTranslateWidthPixel / translateWidthPixel < 0.6)
						scaleX = 60;
					else
						scaleX = (int) (newTranslateWidthPixel / translateWidthPixel * 100.0);
					translateWidthPixel = translateWidthPixel * scaleX / 100.0;

					// Try to fit the translation by cutting
					while (marginV == translateMariginV && (from + translateWidthPixel) > translateMariginH) {
						translate = translate.substring(0, translate.length() - 2);
						translate += '…';
						translateWidthPixel = getStringWidth(translate + " ", translateFontSize) * scaleX / 100.0;
					}
				}
				// Create a line in the script
				double margin = from + translateWidthPixel / 2;
				translateMariginH = from;
				translateMariginV = marginV;
				int marginL = (int) margin > 0 ? (int) margin : 0;
				int marginR = (int) margin <= 0 ? (int) -margin : 0;
				translates.add(",Translate, NTP, " + marginL * 2 + ", " + marginR * 2 + ", " + marginV + ",!Effect,{\\fscx" + scaleX + "}" + translate);
				break;
			}
			minPos = maxPos + 1;
			marginV = marginV - mainFontSize - mainFontSize / 2;
		}
	}

	private String join(int layer, String timeStamp, String textSpeech) {
		return "Dialogue: " + layer + "," + timeStamp + textSpeech + "\n";
	}


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


	private String getScriptInfo() {
		return "[Script Info]\n" +
				"Original Script: LinguaSubtitle 2.3 http://sourceforge.net/projects/linguasubtitle/\n" +
				"ScriptType: v4.00+\n" +
				"Collisions: Normal\n" +
				"WrapStyle: 1\n" +
				"PlayResX: " + String.format("%d", (int) playResX) + "\n" +
				"PlayResY: " + String.format("%d", (int) playResY) + "\n" +
				"Timer: 100.0000\n\n";
	}

	private String getStyles() {
		int Outline = (int) Math.ceil(mainFontSize / 24.0);
		int Shadow = Outline;
		return "[V4+ Styles]\n" +
				"Format:Name,Fontname,Fontsize,PrimaryColour,SecondaryColour,OutlineColour,BackColour,Bold,Italic,Underline,StrikeOut,ScaleX,ScaleY,Spacing,Angle,BorderStyle,Outline,Shadow,Alignment,MarginL,MarginR,MarginV,Encoding\n" +
				"Style:Default," + fontName + "," + mainFontSize + ",&H" + transparencyKnownWords + RGBtoBGR(textColor) + ",&H" + transparencyKnownWords + "FFFFFF,&H" + transparencyKnownWords + "000000, &HFF000000,0,0,0,0,100,100,0,0,1,2," + Outline + "," + Shadow + ",0,0,10,1\n" +
				"Style:Translate," + fontName + "," + translateFontSize + ",&H00" + RGBtoBGR(translateColor) + ",&H00FFFFFF,&H00000000,&HFF000000,0,0,0,0,100,100,0,0,1,2," + Outline + "," + Shadow + ",0,0,10,1\n\n";
	}

	private String RGBtoBGR(String color) {
		int in = Integer.decode("#" + color);
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in >> 0) & 0xFF;
		int out = (blue << 16) | (green << 8) | (red << 0);
		Color c = new Color(out);
		return toHexString(c);
	}

	private double getStringWidth(String text, int fontSize) {
		Font font = new Font(fontName, Font.PLAIN, fontSize);
		TextLayout textLayout = new TextLayout(text, font, new FontRenderContext(null, true, true));
		double width = textLayout.getAdvance() * fontSize / (textLayout.getAscent() + textLayout.getDescent());
		return width;
	}
	//</editor-fold>

}
