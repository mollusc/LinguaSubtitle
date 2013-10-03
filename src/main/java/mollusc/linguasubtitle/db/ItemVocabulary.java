/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle.db;

/**
 * @author mollusc <MolluscLab@gmail.com>
 *         Item of the table Stemmator
 */
public class ItemVocabulary {
	//<editor-fold desc="Public Fields">
	/**
	 * Word
	 */
	public final String word;
	/**
	 * Translation of the word
	 */
	public final String translate;
	/**
	 * Is word known?
	 */
	public final boolean known;
	/**
	 * Is word study?
	 */
	public final boolean study;
	/**
	 * Number of meeting in the subtitle
	 */
	public final int meeting;
	//</editor-fold>

	//<editor-fold desc="Constructor">

	/**
	 * Constructor of the class ItemVocabulary
	 *
	 * @param word      word
	 * @param translate translation of the word
	 * @param known     Is word known?
	 * @param meeting   number of meeting in the subtitle
	 * @param study     Is word study?
	 */
	public ItemVocabulary(String word, String translate, boolean known, int meeting, boolean study) {
		this.word = word;
		this.translate = translate;
		this.known = known;
		this.meeting = meeting;
		this.study = study;
	}
	//</editor-fold>
}
