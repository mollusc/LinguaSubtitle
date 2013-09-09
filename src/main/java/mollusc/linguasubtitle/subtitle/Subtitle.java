package mollusc.linguasubtitle.subtitle;

import mollusc.linguasubtitle.Filename;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 05.09.13
 */
public class Subtitle implements Iterable<Speech>{

	//<editor-fold desc="Private Field">
	/**
	 * Subtitle speeches
	 */
	private ArrayList<Speech> speeches;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public Subtitle(String path)
	{
		initialiseFromFile(path);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public Iterator<Speech> iterator() {
		return speeches.iterator();
	}

	/**
	 * Get speech by index
	 * @param index index of speech
	 * @return
	 */
	public Speech getSpeech(int index)
	{
		if(index >= 0 && index< speeches.size())
			return speeches.get(index);
		return null;
	}
	//</editor-fold>

	//<editor-fold desc="Private Methods">
	/**
	 * Initialise file from the file subtitles
	 * @param path path to the file subtitles
	 */
	private void initialiseFromFile(String path) {
		try {
			Filename fileName = new Filename(path);
			String extension = fileName.extension().toLowerCase();
			FileInputStream stream = new FileInputStream(new File(path));
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				String content = Charset.defaultCharset().decode(bb).toString();
				if (extension.toLowerCase() == "srt")
					setSpeechesFromSrt (content);
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}


	/**
	 * Set speeches from the SubRip (srt) subtitles
	 */
	private void setSpeechesFromSrt(String content) {
		speeches = new ArrayList<Speech>();
		content += '\u00A0';
		String[] lines = content.split("\\r?\\n");
		boolean headerSpeech = true;
		String timing = "";
		String text = "";
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (headerSpeech && tryParseInt(line)) {
				i++;
				timing = lines[i];
				headerSpeech = false;
				continue;
			}
			if (line.isEmpty() && !text.isEmpty()) {
				speeches.add(new Speech(timing, text));

				text = "";
				headerSpeech = true;
				continue;
			}
			if (!headerSpeech)
				text += line + "\n";
		}
	}

	/**
	 * Check, the text is an integer?
	 *
	 * @param value - the text for check
	 * @return true, if the value is a integer, otherwise - false
	 */
	private static boolean tryParseInt(String value) {
		try
		{
			Integer.parseInt(value);
			return true;
		} catch(NumberFormatException nfe)
		{
			return false;
		}
	}
	//</editor-fold>
}
