package mollusc.linguasubtitle.subtitle.srt;

public class Speech {
	public int sequenceNumber;
	public String timing;
	public String content;
	
	public Speech(int sequenceNumber, String timing, String content)
	{
		this.sequenceNumber = sequenceNumber;
		this.timing = timing;
		this.content = content;
	}
}
