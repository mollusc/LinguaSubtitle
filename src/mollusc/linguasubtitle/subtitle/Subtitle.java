package mollusc.linguasubtitle.subtitle;

import mollusc.linguasubtitle.subtitle.parser.Stem;

import javax.swing.text.Document;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author mollusc <MolluscLab@gmail.com>
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
            FileInputStream stream = new FileInputStream(new File(pathToSubtitle));
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                content = Charset.defaultCharset().decode(bb).toString();
            } finally {
                stream.close();
            }
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
    public abstract void markWord(String stem, Document document);

    /**
     * Hide headers of in the subtitle
     *
     * @return Text with hidden headers
     */
    public abstract void hideHeader(Document document);

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
     * @param pathToSave      - path for saving of the subtitle
     * @param stemsTranslate  - translation for stems
     * @param stemsColors     - colors for stems
     * @param knownColor      - color for known words
     * @param hideKnownSpeech - Will known speech hide?
     */
    public abstract void generateSubtitle(String pathToSave,
                                          Map<String, String> stemsTranslate,
                                          Map<String, String> stemsColors,
                                          Map<String, String> translateColors,
                                          String knownColor,
                                          boolean hideKnownSpeech);

    /**
     * Delete all html tags
     *
     * @param html string of html text
     * @return string without html tags
     */
    public static String html2text(String html) {
        return html.replaceAll("\\<.*?>", "");
    }

    /**
     * Check, the text is a numeric?
     *
     * @param text - the text for check
     * @return true, if the text is a numeric, otherwise - false
     */
    public static boolean isNumeric(String text) {
        try {
            Integer.parseInt(text);

        } catch (Exception nfe) {
            return false;
        }
        return true;
    }

    /**
     * Save content
     *
     * @param path    - path to save
     * @param content
     */
    protected static void saveSubtitle(String path, String content) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), "UTF8");
            writer.append(content);
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
