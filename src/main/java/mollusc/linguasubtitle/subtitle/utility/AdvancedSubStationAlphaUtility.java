package mollusc.linguasubtitle.subtitle.utility;

import java.util.concurrent.TimeUnit;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 23.09.13
 */
public class AdvancedSubStationAlphaUtility {
	//<editor-fold desc="Public Methods">

	/**
	 * Get Advanced SubStation Alpha time stamp
	 *
	 * @param startTime start time in milliseconds
	 * @param endTime   end time in milliseconds
	 * @return string with time stamp
	 */
	public static String getTimeStamp(int startTime, int endTime) {
		long startH = TimeUnit.MILLISECONDS.toHours(startTime);
		long startM = TimeUnit.MILLISECONDS.toMinutes(startTime) - TimeUnit.MILLISECONDS.toHours(startTime) * 60;
		long startS = TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MILLISECONDS.toMinutes(startTime) * 60;
		long startMS = TimeUnit.MILLISECONDS.toMillis(startTime) - TimeUnit.MILLISECONDS.toSeconds(startTime) * 1000;
		long endH = TimeUnit.MILLISECONDS.toHours(endTime);
		long endM = TimeUnit.MILLISECONDS.toMinutes(endTime) - TimeUnit.MILLISECONDS.toHours(endTime) * 60;
		long endS = TimeUnit.MILLISECONDS.toSeconds(endTime) - TimeUnit.MILLISECONDS.toMinutes(endTime) * 60;
		long endMS = TimeUnit.MILLISECONDS.toMillis(endTime) - TimeUnit.MILLISECONDS.toSeconds(endTime) * 1000;
		return String.format("%02d:%02d:%02d.%02d, %02d:%02d:%02d.%02d",
				startH, startM, startS, startMS / 10,
				endH, endM, endS, endMS / 10);
	}
	//</editor-fold>
}
