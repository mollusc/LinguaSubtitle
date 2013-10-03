package mollusc.linguasubtitle.subtitle.utility;

import java.awt.*;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 11.09.13
 */
public class CommonUtility {
	//<editor-fold desc="Public Methods">

	/**
	 * Check, the text is an integer?
	 *
	 * @param value - the text for check
	 * @return true, if the value is a integer, otherwise - false
	 */
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * Delete all html tags
	 *
	 * @param html string of html text
	 * @return string without html tags
	 */
	public static String html2text(String html) {
		return html.replaceAll("<.*?>", "");
	}

	/**
	 * Convert a color to a hex string
	 * @param c input color
	 * @return hex string of the color
	 */
	public static String toHexString(Color c) {
		StringBuilder sb = new StringBuilder();

		if (c.getRed() < 16) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(c.getRed()));

		if (c.getGreen() < 16) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(c.getGreen()));

		if (c.getBlue() < 16) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(c.getBlue()));

		return sb.toString();
	}
	//</editor-fold>
}
