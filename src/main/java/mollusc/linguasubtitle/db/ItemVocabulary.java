/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle.db;

/**
 * Item of the table Stem
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class ItemVocabulary {
	public final String word;
	public final String translate;
	public final boolean known;
	public final int meeting;
	public final boolean study;

	public ItemVocabulary(String word, String translate, boolean known, int meeting, boolean study) {
		this.word = word;
		this.translate = translate;
		this.known = known;
		this.meeting = meeting;
		this.study = study;
	}
}
