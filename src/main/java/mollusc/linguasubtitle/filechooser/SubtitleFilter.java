package mollusc.linguasubtitle.filechooser;

import java.io.File;

/**
 * Filter subtitles
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class SubtitleFilter extends javax.swing.filechooser.FileFilter {

	final String ext;
	private final String description;

	public String getDescription() {
		return description;
	}

	public SubtitleFilter() {
		this.ext = ".srt";
		description = "Subtitles SRT";
	}

	public boolean accept(File f) {
		return f != null && (f.isDirectory() || f.toString().endsWith(ext));
	}
}
