package mollusc.linguasubtitle.subtitle.utility;

import mollusc.linguasubtitle.subtitle.Speech;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 11.09.13
 */
public class SubRipUtility {
	//<editor-fold desc="Public Methods">

	/**
	 * Get SubRip time stamp
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
		return String.format("%02d:%02d:%02d,%03d --> %02d:%02d:%02d,%03d",
				startH, startM, startS, startMS,
				endH, endM, endS, endMS);
	}

	/**
	 * Get speeches from the SubRip subtitles
	 *
	 * @param content content of the subtitle
	 * @return array with speeches
	 */
	public static ArrayList<Speech> getSpeeches(String content) {
		ArrayList<Speech> speeches = new ArrayList<Speech>();
		String newLine = "\\r?\\n";
		String space = "[ \\t]*";
		Pattern pattern = Pattern.compile("(?s)\\d+" + space + newLine
				+ "(\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d" + space + "-->" + space + "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d)" + space + "(X1:\\d.*?)??" + newLine
				+ "(.*?)" + newLine + newLine);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String timing = matcher.group(1);
			String text = matcher.group(3).replaceAll("\\r?\\n", "\n");
			speeches.add(new Speech(timing, text));
		}
		return speeches;
	}
	//</editor-fold>
}
