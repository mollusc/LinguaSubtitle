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
    public boolean known;
    public int meeting;
    public boolean study;

    public ItemVocabulary(String stem, String word, String translate, boolean known, int meeting, boolean study) {
	this.stem = stem;
	this.word = word;
	this.translate = translate;
	this.known = known;
	this.meeting = meeting;
	this.study = study;
    }
}
