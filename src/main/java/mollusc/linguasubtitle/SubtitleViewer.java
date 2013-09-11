package mollusc.linguasubtitle;

import mollusc.linguasubtitle.index.IndexWord;
import mollusc.linguasubtitle.index.Indexer;
import mollusc.linguasubtitle.subtitle.Speech;
import mollusc.linguasubtitle.subtitle.Subtitle;
import mollusc.linguasubtitle.subtitle.utility.SubRipUtility;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.*;

/**
 * @author mollusc <MolluscLab@gmail.com>
 */
public class SubtitleViewer {

	//<editor-fold desc="Private Field">
	/**
	 * Subtitle
	 */
	private final Subtitle subtitle;

	/**
	 * Index words in the subtitle
	 */
	private final Indexer index;
	//</editor-fold>

	//<editor-fold desc="Constructor">
	public SubtitleViewer(Subtitle subtitle, Indexer index) {
		this.subtitle = subtitle;
		this.index = index;
	}
	//</editor-fold>

	//<editor-fold desc="Public Methods">
	/**
	 * Print subtitle to document, and highlight word
	 * @param stemString stem of highlighted word
	 * @param document output document
	 */
	public void print(String stemString, Document document) {
		ArrayList<IndexWord> indexWords = index.get(stemString);
		SimpleAttributeSet attr = new SimpleAttributeSet();

		SimpleAttributeSet attrHide = new SimpleAttributeSet();
		attrHide.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.lightGray);

		SimpleAttributeSet attrMark = new SimpleAttributeSet();
		attrMark.addAttribute(StyleConstants.CharacterConstants.Foreground, Color.red);
		attrMark.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);

		try {
			int idSpeech = 0;
			for (Speech speech : subtitle) {
				document.insertString(document.getLength(), SubRipUtility.getSubRipTimeStamp(speech.startTimeInMilliseconds, speech.endTimeInMilliseconds) + "\n", attrHide);
				int length = document.getLength();
				document.insertString(length, speech.content + "\n", attr);
				for (IndexWord indexWord : indexWords) {
					if (indexWord.indexSpeech == idSpeech) {
						String word = speech.content.substring(indexWord.start, indexWord.end);
						document.remove(length + indexWord.start, indexWord.end - indexWord.start);
						document.insertString(length + indexWord.start, word, attrMark);
					}
				}
				idSpeech++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print subtitle to document
	 * @param document output document
	 */
	public void print(Document document) {
		SimpleAttributeSet attr = new SimpleAttributeSet();
		SimpleAttributeSet attrHide = new SimpleAttributeSet();
		StyleConstants.setForeground(attrHide, Color.LIGHT_GRAY);
		try {
			for (Speech speech : subtitle) {
				document.insertString(document.getLength(), SubRipUtility.getSubRipTimeStamp(speech.startTimeInMilliseconds, speech.endTimeInMilliseconds) + "\n", attrHide);
				document.insertString(document.getLength(), speech.content + "\n", attr);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get position of the stems in the subtitle.
	 */
	public int getPositionStem(String stem) {
		ArrayList<IndexWord> indexWords = index.get(stem);
		if (indexWords != null && indexWords.size() > 0) {
			int indexSpeech = indexWords.get(0).indexSpeech;
			int lengthToWord = 0;
			int idSpeech = 0;
			for (Speech speech : subtitle) {
				lengthToWord += SubRipUtility.getSubRipTimeStamp(speech.startTimeInMilliseconds, speech.endTimeInMilliseconds).length() + 1;

				if (indexSpeech == idSpeech) {
					lengthToWord += speech.content.substring(0, indexWords.get(0).end).length();
					return lengthToWord;
				}
				lengthToWord += speech.content.length() + 1;
				idSpeech++;
			}
		}
		return 0;
	}
	//</editor-fold>
}
