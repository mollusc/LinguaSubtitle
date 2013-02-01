package mollusc.linguasubtitle.subtitle.srt;

/**
 * Container for data of a srt subtitle
 * @author mollusc <MolluscLab@gmail.com>
 *
 */
public class Speech {
	/**
	 * Sequence number of the speech
	 */
	public int sequenceNumber;
	
	/**
	 * Timing of the speech
	 */
	public String timing;
	
	/**
	 * Content of the speech
	 */
	public String content;

	public Speech(int sequenceNumber, String timing, String content) {
		this.sequenceNumber = sequenceNumber;
		this.timing = timing;
		this.content = content;
	}
}
