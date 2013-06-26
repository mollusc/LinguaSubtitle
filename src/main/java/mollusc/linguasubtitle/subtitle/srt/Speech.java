package mollusc.linguasubtitle.subtitle.srt;

import java.util.concurrent.TimeUnit;

/**
 * Container for data of a srt subtitle
 *
 * @author mollusc <MolluscLab@gmail.com>
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

	/**
	 * Start timestamps of the subtitle in milliseconds
	 */
	int startTimeInMilliseconds;

	/**
	 * End timestamps of the subtitle in milliseconds
	 */
	int endTimeInMilliseconds;

    public Speech(int sequenceNumber, String timing, String content) {
        this.sequenceNumber = sequenceNumber;
        this.timing = timing;
        this.content = content;
		UpdateTimestamps();
    }

	private void UpdateTimestamps() {
		int indexOfArrowChars = timing.indexOf("-->");
		String startString = timing.substring (0, indexOfArrowChars).trim();
		String endString   = timing.substring (indexOfArrowChars+4, timing.length()).trim();

		int startH = Integer.parseInt( startString.substring(0,2));
		int startM = Integer.parseInt( startString.substring(3,5));
		int startS = Integer.parseInt( startString.substring(6,8));
		int startMS = Integer.parseInt( startString.substring(9,12));
		startTimeInMilliseconds = 60*60*1000*startH + 60*1000*startM + 1000*startS + startMS;

		int endH = Integer.parseInt( endString.substring(0,2));
		int endM = Integer.parseInt( endString.substring(3,5));
		int endS = Integer.parseInt( endString.substring(6,8));
		int endMS = Integer.parseInt( endString.substring(9,12));
		endTimeInMilliseconds = 60*60*1000*endH + 60*1000*endM + 1000*endS + endMS;
	}

	public void SetTimestamp(int startTime, int endTime)
	{
		long startH = TimeUnit.MILLISECONDS.toHours(startTime);
		long startM = TimeUnit.MILLISECONDS.toMinutes(startTime) - TimeUnit.MILLISECONDS.toHours(startTime) * 60;
		long startS = TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MILLISECONDS.toMinutes(startTime) * 60;
		long startMS = TimeUnit.MILLISECONDS.toMillis(startTime) - TimeUnit.MILLISECONDS.toSeconds(startTime) * 1000;
		long endH = TimeUnit.MILLISECONDS.toHours(endTime);
		long endM = TimeUnit.MILLISECONDS.toMinutes(endTime) - TimeUnit.MILLISECONDS.toHours(endTime) * 60;
		long endS = TimeUnit.MILLISECONDS.toSeconds(endTime) - TimeUnit.MILLISECONDS.toMinutes(endTime) * 60;
		long endMS = TimeUnit.MILLISECONDS.toMillis(endTime) - TimeUnit.MILLISECONDS.toSeconds(endTime) * 1000;
		timing = String.format("%02d:%02d:%02d,%03d --> %02d:%02d:%02d,%03d",
				startH,	startM,	startS,	startMS,
				endH, endM, endS, endMS);
		startTimeInMilliseconds = startTime;
		endTimeInMilliseconds = endTime;
	}
}
