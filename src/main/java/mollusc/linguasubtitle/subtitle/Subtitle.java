package mollusc.linguasubtitle.subtitle;

import mollusc.linguasubtitle.Filename;
import mollusc.linguasubtitle.subtitle.utility.SubRipUtility;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 05.09.13
 */
public class Subtitle implements Iterable<Speech> {

	//<editor-fold desc="Private Field">
	/**
	 * Subtitle speeches
	 */
	private ArrayList<Speech> speeches;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public Subtitle(String path)
	{
		initializeFromFile(path);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	@Override
	public Iterator<Speech> iterator() {
		return speeches == null ? Collections.EMPTY_LIST.iterator() : speeches.iterator();
	}

	/**
	 * Get speech by index
	 * @param index index of speech
	 * @return Speech
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
	private void initializeFromFile(String path) {
		try {
			Filename fileName = new Filename(path);
			String extension = fileName.extension().toLowerCase();
			FileInputStream stream = new FileInputStream(new File(path));
			try {
				FileChannel fc = stream.getChannel();
				MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
				String content = Charset.defaultCharset().decode(bb).toString();
				if (extension.toLowerCase().equals("srt"))
					speeches = SubRipUtility.getSpeeches(content);
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	//</editor-fold>
}
