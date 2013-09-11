package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.IndexWordComparator;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 06.09.13
 */
public class SubRipRender extends Render {
	//<editor-fold desc="Private Fields">
	private String textColor;
	private boolean hideKnownDialog;
	private boolean automaticDuration;
	private int millisecondsPerCharacter;
	private WordStyle wordStyle;
	private Indexer indexer;
	//</editor-fold>

	//<editor-fold desc="Description">
	public SubRipRender(Subtitle subtitle,
						WordStyle wordStyle,
						Indexer indexer,
						String textColor,
						int millisecondsPerCharacter,
						boolean hideKnownDialog,
						boolean automaticDuration) {
		super(subtitle);
		this.indexer = indexer;
		this.textColor = textColor;
		this.hideKnownDialog = hideKnownDialog;
		this.automaticDuration = automaticDuration;
		this.millisecondsPerCharacter = millisecondsPerCharacter;
		this.wordStyle = wordStyle;
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public void save(String pathToSave) {
		String textSubtitle = "";
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndexByIndexSpeech();
		int indexSpeech = 0;
		for (Speech speech : subtitle) {
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
						int start = html2text(left).length();
						if (wordStyle.getTranslatedWordInfo(stem) != null) {
							String translate = wordStyle.getTranslatedWordInfo(stem).getTranslate();
							if (translate != null && translate != "") {
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
			indexSpeech++;

			if (hideKnownDialog && !isEdited)
				continue;

			String timeStamp = getTimeStamp(speech, subtitle.getSpeech(indexSpeech));
			textSubtitle += join(textSpeech, textTranslate.toString(), indexSpeech, timeStamp);

		}
		saveSubtitle(pathToSave, textSubtitle);
	}
	//</editor-fold>

	//<editor-fold desc="Protected Methods">

	/**
	 * Delete all html tags
	 *
	 * @param html string of html text
	 * @return string without html tags
	 */
	protected static String html2text(String html) {
		return html.replaceAll("<.*?>", "");
	}

	/**
	 * Save content
	 *
	 * @param path - path to save
	 */
	protected static void saveSubtitle(String path, String content) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, false), "UTF8");
			writer.append(content);
			writer.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
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
	 * @param content
	 * @return
	 */
	private String blankTranslate(String content) {
		String strTranslate = "";
		content = html2text(content);
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
			int newDuration = html2text(currentSpeech.content).length() * millisecondsPerCharacter;

			if (newDuration > currentDuration) {
				int newEndTime = currentSpeech.startTimeInMilliseconds + newDuration;
				if (nextSpeech != null && newEndTime > nextSpeech.startTimeInMilliseconds)
					newEndTime = nextSpeech.startTimeInMilliseconds - 1; // 1 is space between subtitles
				return Speech.getSubRipTimeStamp(currentSpeech.startTimeInMilliseconds, newEndTime);
			}
		}
		return Speech.getSubRipTimeStamp(currentSpeech.startTimeInMilliseconds, currentSpeech.endTimeInMilliseconds);
	}
	//</editor-fold>
}
