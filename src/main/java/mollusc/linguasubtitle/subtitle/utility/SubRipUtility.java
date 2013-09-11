package mollusc.linguasubtitle.subtitle.utility;

import mollusc.linguasubtitle.subtitle.Speech;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
		content += '\u00A0';
		String[] lines = content.split("\\r?\\n");
		boolean headerSpeech = true;
		String timing = "";
		String text = "";
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (headerSpeech && CommonUtility.tryParseInt(line)) {
				i++;
				timing = lines[i];
				headerSpeech = false;
				continue;
			}
			if (line.isEmpty() && !text.isEmpty()) {
				speeches.add(new Speech(timing, text));

				text = "";
				headerSpeech = true;
				continue;
			}
			if (!headerSpeech)
				text += line + "\n";
		}
		return speeches;
	}
}
