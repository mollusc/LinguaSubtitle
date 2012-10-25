package mollusc.linguasubtitle.subtitle.srt;

public class IndexWord {
	public int numberSpeech;
	public int start;
	public int end;

	public IndexWord(int numberSpeach, int startWord, int lengthWord) {
		this.numberSpeech = numberSpeach;
		this.start = startWord;
		this.end = lengthWord;
	}
}
