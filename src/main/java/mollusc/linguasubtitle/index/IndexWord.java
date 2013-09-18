package mollusc.linguasubtitle.index;

/**
 * Container for index data
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class IndexWord {
	//<editor-fold desc="Public Field">
	/**
	 * word
	 */
	public final String word;
	/**
	 * Stem of the word
	 */
	public final String stem;

	/**
	 * Index of speech in the subtitle
	 */
	public final int indexSpeech;

	/**
	 * Index of first character of the word
	 */
	public final int start;

	/**
	 * Index of last character of the word
	 */
	public final int end;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public IndexWord(String word, String stem, int indexSpeech, int startWord, int endWord) {
		this.word = word;
		this.stem = stem;
		this.indexSpeech = indexSpeech;
		this.start = startWord;
		this.end = endWord;
	}
	//</editor-fold>
}
