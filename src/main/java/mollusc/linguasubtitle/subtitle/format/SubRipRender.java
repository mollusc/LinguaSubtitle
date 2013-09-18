package mollusc.linguasubtitle.subtitle.format;

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
 */
public class SubRipRender extends Render {
	//<editor-fold desc="Constructor">
	public SubRipRender(Subtitle subtitle,
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
		String textSubtitle = "";
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndexByIndexSpeech();
		ArrayList<Integer> editedIndexSpeeches = getEditedIndexSpeeches();
		int indexSpeech = -1;
		int index = 1;
		for (Speech speech : subtitle) {
			indexSpeech++;
			boolean isEdited = false;
			String textSpeech = speech.content;
			StringBuilder textTranslate = new StringBuilder(blankTranslate(textSpeech));
			ArrayList<IndexWord> indexWords = indices.get(indexSpeech);
			if (indexWords != null) {
				Collections.sort(indexWords, new IndexWordComparator());
				for (IndexWord indexWord : indexWords) {
					String word = indexWord.word;
					String stem = indexWord.stem;
					if (wordStyle.getColor(stem) != null) {
						isEdited = true;
						String left = textSpeech.substring(0, indexWord.start);
						int start = CommonUtility.html2text(left).length();
						if (wordStyle.getTranslatedWordInfo(stem) != null) {
							String translate = wordStyle.getTranslatedWordInfo(stem).getTranslate();
							if (translate != null && !translate.equals("")) {
								String translateColor = wordStyle.getTranslateColor(stem);
								InsertWordTranslation(textTranslate, translate, start, translateColor);
							}
						}
						String middle = "<font color=\"" + wordStyle.getColor(stem) + "\">" + word + "</font>";
						String right = textSpeech.substring(indexWord.end);
						textSpeech = left + middle + right;
					}
				}
			}


			if (hideKnownDialog && !isEdited)
				continue;

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

	private ArrayList<Integer> getEditedIndexSpeeches() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndexByIndexSpeech();
		int indexSpeech = 0;
		for (Speech ignored : subtitle) {
			ArrayList<IndexWord> indexWords = indices.get(indexSpeech);
			if (indexWords != null) {
				for (IndexWord indexWord : indexWords) {
					if (wordStyle.getColor(indexWord.stem) != null) {
						result.add(indexSpeech);
						break;
					}
				}
			}
			indexSpeech++;
		}
		return result;
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">
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


	private Map<Integer, ArrayList<IndexWord>> getAllIndexByIndexSpeech() {
		Map<Integer, ArrayList<IndexWord>> indices = new HashMap<Integer, java.util.ArrayList<IndexWord>>();
		for (String stem : indexer) {
			for (IndexWord indexWord : indexer.get(stem)) {
				if (!indices.containsKey(indexWord.indexSpeech))
					indices.put(indexWord.indexSpeech, new ArrayList<IndexWord>());

				indices.get(indexWord.indexSpeech).add(indexWord);
			}
		}
		return indices;
	}

	/**
	 * Create string filling by nonbreaking space.
	 *
	 * @return
	 */
	private String blankTranslate(String content) {
		String strTranslate = "";
		content = CommonUtility.html2text(content);
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) == '\n')
				strTranslate += '\n';
			else
				strTranslate += '\u00A0';
		}
		return strTranslate;
	}

	private void InsertWordTranslation(StringBuilder strTranslate, String wordTranslate, int start, String colorTranslate) {
		if (!wordTranslate.isEmpty()) {
			int i = start;
			while (i < strTranslate.length() &&
					wordTranslate.length() > i - start &&
					strTranslate.charAt(i) == '\u00A0') {
				char ch = wordTranslate.charAt(i - start);
				strTranslate.setCharAt(i, ch);
				i++;
			}
			if (wordTranslate.length() > i - start ||
					(i < strTranslate.length() && wordTranslate.length() == i - start && strTranslate.charAt(i) != '\u00A0'))
				// Horizontal ellipsis
				strTranslate.setCharAt(i - 1, '\u2026');

			// Add tag
			strTranslate.insert(i, "</font>");
			strTranslate.insert(start, "<font color=\"" + colorTranslate + "\">");
		}
	}

	private String getTimeStamp(Speech currentSpeech, Speech nextSpeech) {
		if (automaticDuration) {
			int currentDuration = currentSpeech.endTimeInMilliseconds - currentSpeech.startTimeInMilliseconds;
			int newDuration = CommonUtility.html2text(currentSpeech.content).length() * millisecondsPerCharacter;

			if (newDuration > currentDuration) {
				int newEndTime = currentSpeech.startTimeInMilliseconds + newDuration;
				if (nextSpeech != null && newEndTime > nextSpeech.startTimeInMilliseconds)
					newEndTime = nextSpeech.startTimeInMilliseconds - 1; // 1 is space between subtitles
				return SubRipUtility.getSubRipTimeStamp(currentSpeech.startTimeInMilliseconds, newEndTime);
			}
		}
		return SubRipUtility.getSubRipTimeStamp(currentSpeech.startTimeInMilliseconds, currentSpeech.endTimeInMilliseconds);
	}
	//</editor-fold>
}
