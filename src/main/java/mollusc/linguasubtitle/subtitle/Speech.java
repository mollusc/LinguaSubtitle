package mollusc.linguasubtitle.subtitle;

import java.util.concurrent.TimeUnit;

/**
 * Container for data of a srt subtitle
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class Speech {

	//<editor-fold desc="Public Field">
	/**
	 * Content of the speech
	 */
	public String content;

	/**
	 * Start timestamps of the subtitle in milliseconds
	 */
	public int startTimeInMilliseconds;

	/**
	 * End timestamps of the subtitle in milliseconds
	 */
	public int endTimeInMilliseconds;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public Speech(String srtTiming, String content) {
		this.content = content;
		setTimesFromSrtTimeStamp(srtTiming);
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	/**
	 * Set startTimeInMilliseconds and endTimeInMilliseconds from SubRip time stamp
	 * @param srtTiming SubRip time stamp
	 */
	public void setTimesFromSrtTimeStamp(String srtTiming) {
		int indexOfArrowChars = srtTiming.indexOf("-->");
		String startString = srtTiming.substring(0, indexOfArrowChars).trim();
		String endString = srtTiming.substring(indexOfArrowChars + 4, srtTiming.length()).trim();

		int startH = Integer.parseInt(startString.substring(0, 2));
		int startM = Integer.parseInt(startString.substring(3, 5));
		int startS = Integer.parseInt(startString.substring(6, 8));
		int startMS = Integer.parseInt(startString.substring(9, 12));
		startTimeInMilliseconds = 60 * 60 * 1000 * startH + 60 * 1000 * startM + 1000 * startS + startMS;

		int endH = Integer.parseInt(endString.substring(0, 2));
		int endM = Integer.parseInt(endString.substring(3, 5));
		int endS = Integer.parseInt(endString.substring(6, 8));
		int endMS = Integer.parseInt(endString.substring(9, 12));
		endTimeInMilliseconds = 60 * 60 * 1000 * endH + 60 * 1000 * endM + 1000 * endS + endMS;
	}

	/**
	 * Get SubRip time stamp
	 */
	public static String getSubRipTimeStamp(int startTimeInMilliseconds, int endTimeInMilliseconds) {
		long startH = TimeUnit.MILLISECONDS.toHours(startTimeInMilliseconds);
		long startM = TimeUnit.MILLISECONDS.toMinutes(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toHours(startTimeInMilliseconds) * 60;
		long startS = TimeUnit.MILLISECONDS.toSeconds(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toMinutes(startTimeInMilliseconds) * 60;
		long startMS = TimeUnit.MILLISECONDS.toMillis(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toSeconds(startTimeInMilliseconds) * 1000;
		long endH = TimeUnit.MILLISECONDS.toHours(endTimeInMilliseconds);
		long endM = TimeUnit.MILLISECONDS.toMinutes(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toHours(endTimeInMilliseconds) * 60;
		long endS = TimeUnit.MILLISECONDS.toSeconds(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toMinutes(endTimeInMilliseconds) * 60;
		long endMS = TimeUnit.MILLISECONDS.toMillis(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toSeconds(endTimeInMilliseconds) * 1000;
		return String.format("%02d:%02d:%02d,%03d --> %02d:%02d:%02d,%03d",
				startH, startM, startS, startMS,
				endH, endM, endS, endMS);
	}
	//</editor-fold>
}
