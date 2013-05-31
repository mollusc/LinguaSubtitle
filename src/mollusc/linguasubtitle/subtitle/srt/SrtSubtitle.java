package mollusc.linguasubtitle.subtitle.srt;

import com.rits.cloning.Cloner;
import com.sun.deploy.util.ArrayUtil;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.parser.Stem;
import sun.security.ssl.Debug;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.Console;
import java.util.*;

/**
 * Class for srt subtitles
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class SrtSubtitle extends Subtitle {

    /**
     * Speech subtitles
     */
    private Map<Integer, Speech> speeches;

    /**
     * Word index
     */
    private Map<String, ArrayList<IndexWord>> index;

    public SrtSubtitle(String path, String language) {
        super(path, language);
        initialSpeeches();
        initialIndex();
    }

    /**
     * Speech initialization
     */
    private void initialSpeeches() {
        speeches = new TreeMap<Integer, Speech>();
		content +='\u00A0';
        String[] lines = content.split("\\r?\\n");
        boolean headerSpeech = true;
        int sequenceNumber = 0;
        String timing = new String();
        String text = new String();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (headerSpeech && isNumeric(line)) {
                sequenceNumber = Integer.parseInt(line);
                i++;
                timing = lines[i];
                headerSpeech = false;
                continue;
            }
            if (line.isEmpty() && !text.isEmpty()) {
                speeches.put(sequenceNumber, new Speech(sequenceNumber, timing, text));
                text = "";
                headerSpeech = true;
                continue;
            }
            text += line + "\n";
        }
    }

    /**
     * Index initialization
     */
    private void initialIndex() {
        index = new HashMap<String, ArrayList<IndexWord>>();
        for (Integer indexSpeech : speeches.keySet()) {
            boolean isTag = false;
            String text = new String();
            Speech speech = speeches.get(indexSpeech);
            for (int j = 0; j < speech.content.length(); j++) {
                char ch = speech.content.charAt(j);
                if (!isTag) {
                    if (!Character.isLetter(ch) && ch != '\'') {
                        if (text.length() > 2 && !isNumeric(text)) {
                            String stemString = Stem.stemmingWord(text, language);
                            if (!index.containsKey(stemString))
                                index.put(stemString, new ArrayList<IndexWord>());
                            IndexWord indexWord = new IndexWord(speech.sequenceNumber, j - text.length(), j);
                            index.get(stemString).add(indexWord);
                        }
                        text = new String();
                    } else {
                        text += Character.toLowerCase(ch);
                    }
                }
                if (ch == '<')
                    isTag = true;

                if (ch == '>')
                    isTag = false;
            }
        }

        // Sort index
        for (String stemString : index.keySet()) {
            Collections.sort(index.get(stemString), new IndexWordComparator());
        }
    }

    @Override
    public Map<Stem, Integer> getListStems() {
        Map<Stem, Integer> result = new HashMap<Stem, Integer>();
        for (String stemString : index.keySet()) {
            ArrayList<IndexWord> indexWords = index.get(stemString);
            String currentWord = new String();
            for (IndexWord indexWord : indexWords) {
                Speech speech = speeches.get(indexWord.indexSpeech);
				System.out.println(stemString);
				String word = speech.content.substring(indexWord.start,
                        indexWord.end);

                if (currentWord.isEmpty()) {
                    currentWord = word;
                    continue;
                }

                if (word.length() < currentWord.length())
                    currentWord = word;

                if (Character.isLowerCase(word.charAt(0)) || Character.isLowerCase(currentWord.charAt(0)))
                    currentWord = currentWord.toLowerCase();
            }
            if (!currentWord.isEmpty()) {
                Stem stem = new Stem(stemString, currentWord, language);
                result.put(stem, indexWords.size());
            }
        }
        return result;
    }


    @Override
    public void hideHeader(Document document) {
        SimpleAttributeSet attr = new SimpleAttributeSet();
        SimpleAttributeSet attrHide = new SimpleAttributeSet();
        StyleConstants.setForeground(attrHide, Color.LIGHT_GRAY);
        try {
            for (Speech speech : speeches.values()) {
                document.insertString(document.getLength(), speech.sequenceNumber + "\n" + speech.timing + "\n", attrHide);
                document.insertString(document.getLength(), speech.content + "\n", attr);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPositionStem(String stem) {
        ArrayList<IndexWord> indexWords = index.get(stem);
        if (indexWords != null && indexWords.size() > 0) {
            int indexSpeech = indexWords.get(0).indexSpeech;
            int lengthToWord = 0;

            for (Integer idSpeech : speeches.keySet()) {
                Speech speech = speeches.get(idSpeech);
                lengthToWord += Integer.toString(speech.sequenceNumber).length() + 1;
                lengthToWord += speech.timing.length() + 1;

                if (indexSpeech == idSpeech) {
                    lengthToWord += speech.content.substring(0, indexWords.get(0).end).length();
                    return lengthToWord;
                }
                lengthToWord += speech.content.length() + 1;
            }
        }
        return 0;
    }

    @Override
    public void markWord(String stemString, Document document) {
        ArrayList<IndexWord> indexWords = index.get(stemString);
        SimpleAttributeSet attr = new SimpleAttributeSet();

        SimpleAttributeSet attrHide = new SimpleAttributeSet();
        attrHide.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.lightGray);

        SimpleAttributeSet attrMark = new SimpleAttributeSet();
        attrMark.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.red);
        attrMark.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);

        try {
            for (Speech speech : speeches.values()) {
                document.insertString(document.getLength(), speech.sequenceNumber + "\n" + speech.timing + "\n", attrHide);
                int length = document.getLength();
                document.insertString(length, speech.content + "\n", attr);
                for (IndexWord indexWord : indexWords) {
                    if (indexWord.indexSpeech == speech.sequenceNumber) {
                        String word = speech.content.substring(indexWord.start, indexWord.end);

                        document.remove(length + indexWord.start, indexWord.end - indexWord.start);
                        document.insertString(length + indexWord.start, word, attrMark);
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateSubtitle(String pathToSave,
                                 Map<String, String> pairsStemTranslate,
                                 Map<String, String> pairStemColor,
                                 Map<String, String> pairStemTranslateColor,
                                 String knownColor,
                                 boolean hideKnownDialog) {

        String result = new String();
        Cloner cloner = new Cloner();
        Map<Integer, Speech> cloneSpeeches = cloner.deepClone(speeches);
        Map<Integer, String> mapTranslation = new HashMap<Integer, String>();
        ArrayList<IndexWord> indices = new ArrayList<IndexWord>();

        for (String stemString : pairStemColor.keySet())
            indices.addAll(index.get(stemString));

        Collections.sort(indices, new IndexWordComparator());

        HashSet<Integer> modifiedSpeech = new HashSet<Integer>();

        for (IndexWord indexWord : indices) {
            modifiedSpeech.add(indexWord.indexSpeech);
            Speech speech = cloneSpeeches.get(indexWord.indexSpeech);

            String word = speech.content.substring(indexWord.start, indexWord.end);
            String stemString = Stem.stemmingWord(word.toLowerCase(), language);
            String left = speech.content.substring(0, indexWord.start);

            StringBuilder strTranslate;
            if (mapTranslation.containsKey(indexWord.indexSpeech))
                strTranslate = new StringBuilder(mapTranslation.get(indexWord.indexSpeech));
            else
                strTranslate = new StringBuilder(blankTranslate(speech.content));

            int start = html2text(left).length();
            if (pairsStemTranslate.containsKey(stemString)) {
                String wordTranslationString = pairsStemTranslate.get(stemString);
                String colorTranslate = pairStemTranslateColor.get(stemString);
                InsertWordTranslation(strTranslate, wordTranslationString, start, colorTranslate);
            }

            mapTranslation.put(indexWord.indexSpeech, strTranslate.toString());

            String middle = "<font color=\"" + pairStemColor.get(stemString) + "\">" + word + "</font>";
            String right = speech.content.substring(indexWord.end);
            speech.content = left + middle + right;
        }

        // Generate text of the subtitle
        int indexSpeech = 1;
        for (Integer i : cloneSpeeches.keySet()) {
            if (hideKnownDialog && !modifiedSpeech.contains(i)) {
                continue;
            }
            Speech speech = cloneSpeeches.get(i);
            result += indexSpeech + "\n";
            result += speech.timing + "\n";

            String strTranslate = mapTranslation.get(i);
            String strContent = speech.content;

            String[] linesTranslate = new String[]{};
            if(strTranslate != null)
                linesTranslate = strTranslate.split("\n");

            String[] linesContant = strContent.split("\n");
            String content = "";
            for (int j = 0; j < linesContant.length; j++) {
                if(strTranslate != null)
                    content += linesTranslate[j];
				else
					content += '\u00A0';
                content += "\n" + linesContant[j] + "\n";
            }

            // Hack for vlc 
            content = content.replace("> <", ">\u00A0<");

            result += "<font color=\"" + knownColor + "\">" + content
                    + "</font>\n\n";
            indexSpeech++;
        }
        result += "\n";
        saveSubtitle(pathToSave, result);
    }

    private String blankTranslate(String content) {
        String strTranslate = new String();
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
}
