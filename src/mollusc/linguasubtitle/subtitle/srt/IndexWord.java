package mollusc.linguasubtitle.subtitle.srt;

/**
 * Container for index data
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class IndexWord {
    /**
     * Index of speech in the subtitle
     */
    public int indexSpeech;

    /**
     * Index of first character of the word
     */
    public int start;

    /**
     * Index of last character of the word
     */
    public int end;

    public IndexWord(int indexSpeech, int startWord, int lengthWord) {
        this.indexSpeech = indexSpeech;
        this.start = startWord;
        this.end = lengthWord;
    }
}
