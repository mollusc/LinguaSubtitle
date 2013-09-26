package mollusc.linguasubtitle.subtitle.utility;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 23.09.13
 */
public class AdvancedSubStationAlphaUtility {
	/**
	 * Get Advanced SubStation Alpha time stamp
	 */
	public static String getTimeStamp(int startTimeInMilliseconds, int endTimeInMilliseconds) {
		long startH = TimeUnit.MILLISECONDS.toHours(startTimeInMilliseconds);
		long startM = TimeUnit.MILLISECONDS.toMinutes(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toHours(startTimeInMilliseconds) * 60;
		long startS = TimeUnit.MILLISECONDS.toSeconds(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toMinutes(startTimeInMilliseconds) * 60;
		long startMS = TimeUnit.MILLISECONDS.toMillis(startTimeInMilliseconds) - TimeUnit.MILLISECONDS.toSeconds(startTimeInMilliseconds) * 1000;
		long endH = TimeUnit.MILLISECONDS.toHours(endTimeInMilliseconds);
		long endM = TimeUnit.MILLISECONDS.toMinutes(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toHours(endTimeInMilliseconds) * 60;
		long endS = TimeUnit.MILLISECONDS.toSeconds(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toMinutes(endTimeInMilliseconds) * 60;
		long endMS = TimeUnit.MILLISECONDS.toMillis(endTimeInMilliseconds) - TimeUnit.MILLISECONDS.toSeconds(endTimeInMilliseconds) * 1000;
		return String.format("%02d:%02d:%02d.%02d, %02d:%02d:%02d.%02d",
				startH, startM, startS, startMS / 10,
				endH, endM, endS, endMS / 10);
	}
}
