/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mollusc.linguasubtitle.db;

/**
 * Item of the table Stem
 * @author mollusc
 */
public class ItemVocabulary {
    public String stem;
    public String word;
    public String translate;
    public boolean remember;
    public int meeting;
    public boolean learning;

    public ItemVocabulary(String stem, String word, String translate, boolean remember, int meeting, boolean learning) {
	this.stem = stem;
	this.word = word;
	this.translate = translate;
	this.remember = remember;
	this.meeting = meeting;
	this.learning = learning;
    }
}
