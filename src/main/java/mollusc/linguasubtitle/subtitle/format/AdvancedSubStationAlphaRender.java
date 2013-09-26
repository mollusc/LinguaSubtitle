package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.IndexWordComparator;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.AdvancedSubStationAlphaUtility;
import mollusc.linguasubtitle.subtitle.utility.CommonUtility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static mollusc.linguasubtitle.Preferences.toHexString;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 20.09.13
 */
public class AdvancedSubStationAlphaRender extends Render {
	private final double playResX = 1280;
	private final double playResY = 720;
	private final String fontName = "Arial";
	private final int fontSize = 48;
	private final double translateFacotr = 0.75;
	private int translateMariginV;
	private int translateMariginH;
	//<editor-fold desc="Constructor">
	public AdvancedSubStationAlphaRender(Subtitle subtitle,
						WordStyle wordStyle,
						Indexer indexer,
						String textColor,
						int millisecondsPerCharacter,
						boolean hideKnownDialog,
						boolean automaticDuration) {
		super(subtitle, wordStyle, millisecondsPerCharacter, automaticDuration, indexer, textColor, hideKnownDialog);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public void save(String pathToSave) {
		String textSubtitle = getScriptInfo();
		textSubtitle += getStyles();
		textSubtitle += "[Events]\n" +
						"Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n";
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndexByIndexSpeech();
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

			String timeStamp = getTimeStamp(speech, subtitle.getSpeech(nextIndexSpeech));
			textSubtitle += join(0, timeStamp, ",Default, NTP, 0, 0, 0,!Effect," + textSpeech);
			int i = 1;
			for( String t : translates)
			{
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
		int marginV = lines.length * fontSize + (lines.length - 1) * fontSize/2;
		for (String line : lines)
		{
			maxPos+= line.length();
			if(start > minPos && start < maxPos)
			{
				double totalWidthPixel = getStringWidth(line,fontSize);
				double startPixel = getStringWidth(line.substring(0, start - minPos), fontSize);
				int margin = 0;

				int from = (int)startPixel - (int)(totalWidthPixel/2) ;
				double translatePixel = getStringWidth(translate, (int)(fontSize * translateFacotr));

				while (marginV == translateMariginV && (from + (int)translatePixel) > translateMariginH )
				{
					translate = translate.substring(0,translate.length()-2);
					translate += '…';
					translatePixel = getStringWidth(translate, (int)(fontSize * translateFacotr));
				}
				margin = from + (int)(translatePixel/2);
				translateMariginH = from;
				translateMariginV = marginV;
				int marginL = margin > 0 ? margin : 0;
				int marginR = margin <= 0 ? -margin : 0;
				translates.add(",Translate, NTP, " + marginL*2 +", " + marginR*2 + ", " + marginV + ",!Effect," + translate);
				break;
			}
			minPos = maxPos+1;
			marginV = marginV - fontSize - fontSize/2;
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


	private String getScriptInfo(){
		return "[Script Info]\n" +
				"Original Script: LinguaSubtitle 2.3 http://sourceforge.net/projects/linguasubtitle/\n"+
				"ScriptType: v4.00+\n" +
				"Collisions: Normal\n" +
				"WrapStyle: 1\n" +
				"PlayResX: " + String.format("%d", (int)playResX) + "\n" +
				"PlayResY: " + String.format("%d", (int)playResY) + "\n" +
				"Timer: 100.0000\n\n";
	}

	private String getStyles(){
		return  "[V4+ Styles]\n" +
				"Format:Name,Fontname,Fontsize,PrimaryColour,SecondaryColour,OutlineColour,BackColour,Bold,Italic,Underline,StrikeOut,ScaleX,ScaleY,Spacing,Angle,BorderStyle,Outline,Shadow,Alignment,MarginL,MarginR,MarginV,Encoding\n"+
				"Style:Default," + fontName +","+ fontSize +",&H99" + RGBtoBGR(textColor) + ",&H99FFFFFF,&H99000000,&HFF000000,0,0,0,0,100,100,0,0,1,2,2,2,5,5,10,1\n" +
				"Style:Translate," + fontName +","+ fontSize*2/3 +",&H0000FF00,&H00FFFFFF,&H00000000,&HFF000000,0,0,0,0,100,100,0,0,1,2,2,2,0,0,10,204\n\n";
	}

	private String RGBtoBGR(String color)
	{
		int in = Integer.decode("#" + color);
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in >> 0) & 0xFF;
		int out = (blue << 16) | (green << 8) | (red << 0);
		Color c = new Color(out);
		return  toHexString(c);
	}

	private int getStringWidth(String text, int fontSize)
	{
		Font font = new Font(fontName, Font.PLAIN, (int)(fontSize));
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		FontMetrics fontMetrics = img.getGraphics().getFontMetrics(font);
		return fontMetrics.stringWidth(text);
	}
	//</editor-fold>

}
