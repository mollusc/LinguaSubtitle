package mollusc.linguasubtitle.subtitle;

import mollusc.linguasubtitle.subtitle.parser.Stem;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /*public Subtitle(String path) {
        pathToSubtitle = path;
        try {
            FileInputStream firearm = new FileInputStream(pathToSubtitle);
            DataInputStream in = new DataInputStream(firearm);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine = "";
            content = "";
            while ((strLine = br.readLine()) != null)
                content += strLine + "\n";
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }*/

    // 1
    public Subtitle(String path){
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
    /*public Subtitle(String path){
        pathToSubtitle = path;
        Path p = Paths.get(pathToSubtitle);
        try {
            content = String.valueOf(Files.readAllLines(p, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }*/


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
     * Hide headers of in the subtitle
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
     * Check, the text is a word?
     *
     * @param text the text for check
     * @return true, if the text is a word, otherwise - false
     */
    public static boolean isWord(String text) {
        // return str.matches("-?\\w+(\\w|')*?");
        return text.matches("-?(\\w|')+?");
    }

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
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path, true), "windows-1251");
            writer.append(content);
            writer.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
