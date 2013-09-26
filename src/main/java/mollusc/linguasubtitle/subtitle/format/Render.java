package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 */
public abstract class Render {

    protected final Subtitle subtitle;
	//<editor-fold desc="Private Fields">
	protected String textColor;
	protected boolean hideKnownDialog;
	protected boolean automaticDuration;
	protected int millisecondsPerCharacter;
	protected WordStyle wordStyle;
	protected Indexer indexer;

	protected Render(Subtitle subtitle, WordStyle wordStyle, int millisecondsPerCharacter, boolean automaticDuration, Indexer indexer, String textColor, boolean hideKnownDialog) {
		this.subtitle = subtitle;
		this.wordStyle = wordStyle;
		this.millisecondsPerCharacter = millisecondsPerCharacter;
		this.automaticDuration = automaticDuration;
		this.indexer = indexer;
		this.textColor = textColor;
		this.hideKnownDialog = hideKnownDialog;
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

	public abstract void save(String pathToSave);

	protected Map<Integer, ArrayList<IndexWord>> getAllIndexByIndexSpeech() {
		Map<Integer, ArrayList<IndexWord>> indices = new HashMap<Integer, ArrayList<IndexWord>>();
		for (String stem : indexer) {
			for (IndexWord indexWord : indexer.get(stem)) {
				if (!indices.containsKey(indexWord.indexSpeech))
					indices.put(indexWord.indexSpeech, new ArrayList<IndexWord>());

				indices.get(indexWord.indexSpeech).add(indexWord);
			}
		}
		return indices;
	}

	//<editor-fold desc="Private Methods">
	protected ArrayList<Integer> getEditedIndexSpeeches() {
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
}
