package mollusc.linguasubtitle.subtitle.srt;

import com.rits.cloning.Cloner;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.*;
import javax.print.DocFlavor;
import mollusc.linguasubtitle.VocabularyDialog;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.parser.Stem;

/**
 * Class for srt subtitles
 *
 * @author mollusc
 */
public class SrtSubtitle extends Subtitle {

    private Map<Integer, Speech> speeches;
    private Map<String, ArrayList<IndexWord>> index;

    public SrtSubtitle(String path) {
        super(path);
        initialSpeechs();
        initialIndex();
    }

    /**
     * Speech initialization
     */
    private void initialSpeechs() {
        speeches = new TreeMap<Integer, Speech>();
        String[] lines = content.split("\n");
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
                text = text.substring(0, text.length() - 1);
                speeches.put(sequenceNumber, new Speech(sequenceNumber, timing,
                        text));
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
                        if (text.length() > 2 && isWord(text)) {
                            String stemString = Stem.stemmingWord(text);
                            if (!index.containsKey(stemString)) {
                                index.put(stemString,
                                        new ArrayList<IndexWord>());
                            }
                            IndexWord indexWord = new IndexWord(
                                    speech.sequenceNumber, j - text.length(), j);
                            index.get(stemString).add(indexWord);
                        }
                        text = new String();
                    } else {
                        text += Character.toLowerCase(ch);
                    }
                }
                if (ch == '<') {
                    isTag = true;
                }

                if (ch == '>') {
                    isTag = false;
                }
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
                String word = speech.content.substring(indexWord.start,
                        indexWord.end);

                if (currentWord.isEmpty()) {
                    currentWord = word;
                    continue;
                }

                if (word.length() < currentWord.length()) {
                    if (Character.isUpperCase(word.charAt(0))
                            && Character.isLowerCase(currentWord.charAt(0))) {
                        word = word.toLowerCase();
                    }
                    currentWord = word;
                }

                if (Character.isUpperCase(currentWord.charAt(0))
                        && Character.isLowerCase(word.charAt(0))) {
                    currentWord = currentWord.toLowerCase();
                }
            }
            if (!currentWord.isEmpty()) {
                Stem stem = new Stem(currentWord);
                result.put(stem, index.get(stemString).size());
            }

        }
        return result;
    }

    @Override
    public String hideHeader() {
        return hideHeader(speeches);
    }

    private static String hideHeader(Map<Integer, Speech> speeches) {
        String result = new String();
        for (Integer indexSpeech : speeches.keySet()) {
            Speech speech = speeches.get(indexSpeech);
            result += "<font color=\"#cccccc\">" + speech.sequenceNumber
                    + "</font><br>";
            result += "<font color=\"#cccccc\">" + speech.timing
                    + "</font><br>";
            String text = speech.content + "<br><br>";
            text = text.replace("\n", "<br>");
            result += text;
        }
        return result;
    }

    @Override
    public int getPositionStem(String stem) {
        ArrayList<IndexWord> indexWords = index.get(stem);
        if (indexWords != null && indexWords.size() > 0) {
            int indexSpeech = indexWords.get(0).indexSpeech;
            int lengthToWord = indexWords.get(0).end;

            for (Integer idSpeech : speeches.keySet()) {
                Speech speech = speeches.get(idSpeech);
                lengthToWord += Integer.toString(speech.sequenceNumber)
                        .length() + 1;
                lengthToWord += speech.timing.length() + 1;

                if (indexSpeech == idSpeech) {
                    return lengthToWord;
                }
                lengthToWord += html2text(speech.content).length() + 2;
            }
        }
        return 0;
    }

    @Override
    public int numberDialogWithStems(List<String> stems, int maxNumber) {
        int result = 0;
        Map<Integer, Integer> ListSequenceNumber = new HashMap<Integer, Integer>();

        for (String string : stems) {
            ArrayList<IndexWord> list = index.get(string);
            for (IndexWord indexWord : list) {
                if (!ListSequenceNumber.containsKey(indexWord.indexSpeech)) {
                    ListSequenceNumber.put(indexWord.indexSpeech, 0);
                }
                Integer count = ListSequenceNumber.get(indexWord.indexSpeech);
                count++;
                ListSequenceNumber.put(indexWord.indexSpeech, count);
            }
        }
        for (Integer i : ListSequenceNumber.keySet()) {
            if (ListSequenceNumber.get(i) >= maxNumber) {
                result++;
            }
        }
        return result;
    }

    @Override
    public String markWord(String stemString) {
        Cloner cloner = new Cloner();
        Map<Integer, Speech> cloneSpeeches = cloner.deepClone(speeches);
        ArrayList<IndexWord> indexWords = index.get(stemString);
        for (IndexWord indexWord : indexWords) {
            Speech speech = cloneSpeeches.get(indexWord.indexSpeech);
            String left = speech.content.substring(0, indexWord.start);
            String middle = "<b><font color=\"#ff0000\">"
                    + speech.content.substring(indexWord.start, indexWord.end)
                    + "</font></b>";
            String right = speech.content.substring(indexWord.end);
            speech.content = left + middle + right;
        }
        return hideHeader(cloneSpeeches);
    }

    @Override
    public void generateSubtitle(String pathToSave,
            Map<String, String> stemsTranslate,
            Map<String, String> stemsColors,
            Map<String, String> translateColors,
            String knownColor,
            boolean hideKnownDialog) {
        
        String result = new String();
        Cloner cloner = new Cloner();
        Map<Integer, Speech> cloneSpeeches = cloner.deepClone(speeches);
        Map<Integer, String> mapTranslation = new HashMap<Integer, String>();
        ArrayList<IndexWord> indices = new ArrayList<IndexWord>();       

        for (String stemString : stemsTranslate.keySet())
            indices.addAll(index.get(stemString));

        Collections.sort(indices, new IndexWordComparator());
        
        HashSet<Integer> modifiedSpeech = new HashSet<Integer>();          
        
        for (IndexWord indexWord : indices) {
            modifiedSpeech.add(indexWord.indexSpeech);
            Speech speech = cloneSpeeches.get(indexWord.indexSpeech);   
            
            String word = speech.content.substring(indexWord.start, indexWord.end);
            String stemString = Stem.stemmingWord(word.toLowerCase());
            String left = speech.content.substring(0, indexWord.start);
            
            StringBuilder strTranslate;
            if(mapTranslation.containsKey(indexWord.indexSpeech))
                strTranslate = new StringBuilder(mapTranslation.get(indexWord.indexSpeech));
            else
                strTranslate =  new StringBuilder(blankTranslate(speech.content));  

            int start = html2text(left).length();
            String wordTranslationString = stemsTranslate.get(stemString);
            String colorTranslate =  translateColors.get(stemString);
            InsertWordTranslation(strTranslate, wordTranslationString, start, colorTranslate);            
            mapTranslation.put(indexWord.indexSpeech, strTranslate.toString());
            
            String middle = "<font color=\"" + stemsColors.get(stemString) + "\">" + word + "</font>";
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
            
            String strTranslate =  mapTranslation.get(i);
            String strContent = speech.content;
            
            String[] linesTranslate = strTranslate.split("\n");
            String[] linesContant = strContent.split("\n");
            String content ="";
            for (int j = 0; j < linesTranslate.length; j++) {
                content += linesTranslate[j] + "\n" + linesContant[j] + "\n";
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
    
    private String blankTranslate(String content)
    {
        String strTranslate = new String();
        content = html2text(content);
        for(int i=0; i < content.length(); i++)
        {
            if(content.charAt(i) == '\n')   
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
            if(wordTranslate.length() > i - start ||
                    (i < strTranslate.length() && wordTranslate.length() == i - start && strTranslate.charAt(i) != '\u00A0'))
                // Horizontal ellipsis
                strTranslate.setCharAt(i-1, '\u2026');
            
            // Add tag
            strTranslate.insert(i, "</font>");
            strTranslate.insert(start, "<font color=\"" + colorTranslate + "\">");
        }
    }
}
