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

	/**
	 * Get speeches from the SubRip (srt) subtitles
	 */
	public static ArrayList<Speech> getSpeeches(String content) {
		ArrayList<Speech> speeches = new ArrayList<Speech>();
		String newLine = "\\r?\\n";
		String space = "[ \\t]*";
		Pattern pattern = Pattern.compile("(?s)\\d+"+space+newLine
				+"(\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d"+space+"-->"+ space +"\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d)"+space+"(X1:\\d.*?)??"+newLine
				+"(.*?)"+newLine+newLine);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			speeches.add(new Speech(matcher.group(1),matcher.group(3)));
		}
		return speeches;
	}
}
