package mollusc.linguasubtitle.subtitle.utility;

/**
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 11.09.13
 */
public class CommonUtility {
	/**
	 * Check, the text is an integer?
	 *
	 * @param value - the text for check
	 * @return true, if the value is a integer, otherwise - false
	 */
	public static boolean tryParseInt(String value) {
		try
		{
			Integer.parseInt(value);
			return true;
		} catch(NumberFormatException nfe)
		{
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
}
