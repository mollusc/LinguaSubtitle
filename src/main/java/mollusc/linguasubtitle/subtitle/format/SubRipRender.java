package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.Settings;
import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.IndexWordComparator;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.CommonUtility;
import mollusc.linguasubtitle.subtitle.utility.SubRipUtility;

import java.util.*;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 06.09.13
 * <p/>
 * Class for generate an SubRip subtitle
 */
public class SubRipRender extends Render {
	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class SubRipRender
	 *
	 * @param subtitle  container for speeches
	 * @param wordStyle style of words
	 * @param indexer   index of the text subtitle
	 * @param settings  settings of the program
	 */
	public SubRipRender(Subtitle subtitle,
						WordStyle wordStyle,
						Indexer indexer,
						Settings settings) {
		super(subtitle, wordStyle, indexer, settings);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public void save(String pathToSave) {
		String textSubtitle = "";
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndicesByIndexSpeech();
		ArrayList<Integer> editedIndexSpeeches = getEditedIndexSpeeches();
		int indexSpeech = -1;
		int index = 1;
		for (Speech speech : subtitle) {
			indexSpeech++;
			if (hideKnownDialog && !editedIndexSpeeches.contains(indexSpeech))
				continue;
			String textSpeech = speech.content;
			StringBuilder textTranslate = new StringBuilder(blankTranslate(textSpeech));
			ArrayList<IndexWord> indexWords = indices.get(indexSpeech);
			if (indexWords != null) {
				Collections.sort(indexWords, new IndexWordComparator());
				for (IndexWord indexWord : indexWords) {
					String word = indexWord.word;
					String stem = indexWord.stem;
					if (wordStyle.getColor(stem) != null) {
						String left = textSpeech.substring(0, indexWord.start);
						int start = CommonUtility.html2text(left).length();
						if (wordStyle.getTranslatedWordInfo(stem) != null) {
							String translate = wordStyle.getTranslatedWordInfo(stem).getTranslate();
							if (translate != null && !translate.equals(""))
								InsertWordTranslation(textTranslate, translate, start, translateColor);
						}
						String middle = "<font color=\"" + wordStyle.getColor(stem) + "\">" + word + "</font>";
						String right = textSpeech.substring(indexWord.end);
						textSpeech = left + middle + right;
					}
				}
			}

			// Get next speech index
			int nextIndexSpeech = indexSpeech + 1;
			while (hideKnownDialog && !editedIndexSpeeches.contains(nextIndexSpeech) && nextIndexSpeech < subtitle.size())
				nextIndexSpeech++;

			String timeStamp = getTimeStamp(speech, subtitle.getSpeech(nextIndexSpeech));
			textSubtitle += join(textSpeech, textTranslate.toString(), index, timeStamp);
			index++;

		}
		saveSubtitle(pathToSave, textSubtitle);
	}

	//</editor-fold>

	//<editor-fold desc="Private Methods">

	/**
	 * Join all components of the speech
	 *
	 * @param textSpeech    text of the speech
	 * @param textTranslate translated text
	 * @param indexSpeech   speech id
	 * @param timeStamp     time stamp of the speech
	 * @return joined speech
	 */
	private String join(String textSpeech, String textTranslate, int indexSpeech, String timeStamp) {
		String result = "";
		result += (indexSpeech) + "\n";
		result += timeStamp + "\n";
		String[] linesTranslate = textTranslate.split("\n");
		String[] linesSpeech = textSpeech.split("\n");

		String content = "";
		for (int j = 0; j < linesSpeech.length; j++) {
			content += linesTranslate[j];
			content += "\n" + linesSpeech[j] + "\n";
		}

		// Hack for vlc
		content = content.replace("> <", ">\u00A0<");

		result += "<font color=\"" + textColor + "\">" + content
				+ "</font>\n\n";

		return result;
	}


	/**
	 * Create string filling by nonbreakable space.
	 *
	 * @param pattern pattern for filling
	 * @return string filled by nonbreakable space
	 */
	private String blankTranslate(String pattern) {
		String textTranslation = "";
		pattern = CommonUtility.html2text(pattern);
		for (int i = 0; i < pattern.length(); i++) {
			if (pattern.charAt(i) == '\n')
				textTranslation += '\n';
			else
				textTranslation += '\u00A0';
		}
		return textTranslation;
	}

	/**
	 * Insert translation in current position
	 *
	 * @param textTranslation text with translations
	 * @param wordTranslate   translation
	 * @param start           start position of the translation
	 * @param colorTranslate  color of the translation
	 */
	private void InsertWordTranslation(StringBuilder textTranslation, String wordTranslate, int start, String colorTranslate) {
		if (!wordTranslate.isEmpty()) {
			int i = start;
			while (i < textTranslation.length() &&
					wordTranslate.length() > i - start &&
					textTranslation.charAt(i) == '\u00A0') {
				char ch = wordTranslate.charAt(i - start);
				textTranslation.setCharAt(i, ch);
				i++;
			}
			if (wordTranslate.length() > i - start ||
					(i < textTranslation.length() && wordTranslate.length() == i - start && textTranslation.charAt(i) != '\u00A0'))
				// Horizontal ellipsis
				textTranslation.setCharAt(i - 1, '\u2026');

			// Add tag
			textTranslation.insert(i, "</font>");
			textTranslation.insert(start, "<font color=\"" + colorTranslate + "\">");
		}
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
					newEndTime = nextSpeech.startTimeInMilliseconds - 1; // 1 is space between subtitles
				return SubRipUtility.getTimeStamp(currentSpeech.startTimeInMilliseconds, newEndTime);
			}
		}
		return SubRipUtility.getTimeStamp(currentSpeech.startTimeInMilliseconds, currentSpeech.endTimeInMilliseconds);
	}
	//</editor-fold>
}
