package mollusc.linguasubtitle.subtitle;

import java.io.*;
import java.util.List;
import java.util.Map;
import mollusc.linguasubtitle.subtitle.parser.Stem;

/**
 * 
 * @author mollusc
 */
public abstract class Subtitle {

	/**
	 * Content of the subtitle file
	 */
	protected String content;

	/**
	 * Path to the subtitle file
	 */
	protected String pathToSubtitle;

	/**
	 * Get content of the subtitle file
	 * 
	 * @return
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Get path to the subtitle file
	 * 
	 * @return
	 */
	public String getPathToSubtitle() {
		return pathToSubtitle;
	}

	public Subtitle(String path) {
		pathToSubtitle = path;
		try {
			FileInputStream fstream = new FileInputStream(pathToSubtitle);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine = "";
			content = "";
			while ((strLine = br.readLine()) != null)
				content += strLine + "\n";
			in.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Get the list of stems
	 * 
	 * @return Keys is a stem object, Value is quantity of the stem in the
	 *         subtitle
	 */
	public abstract Map<Stem, Integer> getListStems();

	/**
	 * Mark the stem in the subtitle
	 * 
	 * @param stem
	 * @return text with selected stem
	 */
	public abstract String markWord(String stem);

	/**
	 * Hide headers of dialogs
	 * 
	 * @return Text with hidden headers
	 */
	public abstract String hideHeader();

	/**
	 * Get position of the stems in the subtitle.
	 * 
	 * @param stem
	 * @return
	 */
	public abstract int getPositionStem(String stem);

	/**
	 * Generate subtitle
	 * 
	 * @param pathToSave
	 *            - path for saving of the subtitle
	 * @param stemsTranslate
	 *            - translation for stems
	 * @param stemsColors
	 *            - colors for stems
	 * @param knownColor
	 *            - color for known words
	 * @param hideKnownSpeech
	 *            - Will known speech hide?
	 */
	public abstract void generateSubtitle(String pathToSave,
			Map<String, String> stemsTranslate,
			Map<String, String> stemsColors,
			Map<String, String> translateColors,
			String knownColor,
			boolean hideKnownSpeech);

	/**
	 * Quantity of dialogs with unknown stems more than maxNumber
	 * 
	 * @param stems
	 *            List of unknown stems
	 * @param maxNumber
	 *            Maximum number of words in a dialog
	 * @return
	 */
	public abstract int numberDialogWithStems(List<String> stems, int maxNumber);

	/**
	 * Check, the text is a word?
	 * 
	 * @param text
	 *            the text for check
	 * @return true, if the text is a word, otherwise - false
	 */
	public static boolean isWord(String text) {
		// return str.matches("-?\\w+(\\w|')*?");
		return text.matches("-?(\\w|')+?");
	}

	/**
	 * Delete all html tags
	 * 
	 * @param html
	 *            string of html text
	 * @return string without html tags
	 */
	public static String html2text(String html) {
		return html.replaceAll("\\<.*?>", "");
	}

	/**
	 * Check, the text is a numeric?
	 * 
	 * @param text
	 *            - the text for check
	 * @return true, if the text is a numeric, otherwise - false
	 */
	public static boolean isNumeric(String text) {
		try {
                    byte[] b = text.getBytes("UTF-8");
                    String s = new String(b, "US-ASCII");
                    Integer.parseInt(s);

		} catch (Exception nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Save content
	 * 
	 * @param path
	 *            - path to save
	 * @param content
	 */
	protected static void saveSubtitle(String path, String content) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), "windows-1251");
			writer.append(content);
                        writer.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
