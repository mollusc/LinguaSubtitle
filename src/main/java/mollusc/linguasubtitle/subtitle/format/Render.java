package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.Settings;
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
	//<editor-fold desc="Protected Fields">
	/**
	 * Container for speeches
	 */
	protected final Subtitle subtitle;
	/**
	 * Color of speeches
	 */
	protected String textColor;
	/**
	 * Color of translated words
	 */
	protected String translateColor;
	/**
	 * Are speeches hiding?
	 */
	protected boolean hideKnownDialog;
	/**
	 * Is automatic durations?
	 */
	protected boolean automaticDuration;
	/**
	 * Milliseconds per character
	 */
	protected int millisecondsPerCharacter;
	/**
	 * Style of words
	 */
	protected WordStyle wordStyle;
	/**
	 * Index of the text subtitle
	 */
	protected Indexer indexer;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class Render
	 *
	 * @param subtitle  container for speeches
	 * @param wordStyle style of words
	 * @param indexer   index of the text subtitle
	 * @param settings  settings of the program
	 */
	protected Render(Subtitle subtitle, WordStyle wordStyle, Indexer indexer, Settings settings) {
		this.subtitle = subtitle;
		this.wordStyle = wordStyle;
		this.indexer = indexer;
		this.millisecondsPerCharacter = settings.getMillisecondsPerCharacter();
		this.automaticDuration = settings.getAutomaticDurations();
		this.textColor = settings.getColorKnownWords();
		this.hideKnownDialog = settings.getHideKnownDialog();
		this.translateColor = settings.getColorTranslateWords();
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">

	/**
	 * Generate and save the subtitle to the file
	 * @param pathToSave path to save the file
	 */
	public abstract void save(String pathToSave);
	//</editor-fold>

	/**
	 * Save content in the file
	 *
	 * @param path path to save a file
	 * @param content content of a file
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

	/**
	 *
	 * @return
	 */
	protected Map<Integer, ArrayList<IndexWord>> getAllIndicesByIndexSpeech() {
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

	protected ArrayList<Integer> getEditedIndexSpeeches() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Map<Integer, ArrayList<IndexWord>> indices = getAllIndicesByIndexSpeech();
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
}
