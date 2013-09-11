package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Subtitle;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

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

}
