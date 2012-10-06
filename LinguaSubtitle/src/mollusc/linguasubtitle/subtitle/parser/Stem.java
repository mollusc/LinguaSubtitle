package mollusc.linguasubtitle.subtitle.parser;

import org.tartarus.snowball.ext.englishStemmer;

/**
 * User: Valentin Fedosseyenko
 * Date: 27.08.12
 * Time: 9:57
 */
public class Stem implements Comparable<Stem>  {

    private String content;
    private String word;

    /**
     * Get stem
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * Get word of the stem
     * @return
     */
    public String getWord() {
        return word;
    }

    /**
     * Set word of the stem
     * @param word
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Initialize stem
     * @param word
     */
    public Stem(String word) {
        this.setWord(word);
        content = stemmingWord(this.getWord().toLowerCase());
    }

    /**
     * Get stem from the word
     * @param word
     * @return
     */
    public static String stemmingWord(String word) {
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    @Override
    public boolean equals(Object obj)
    {
        if ( this == obj ) return true;
        if ( obj instanceof Stem )
            return this.getContent().equals(((Stem) obj).getContent());
        if(obj instanceof String )
            return this.getContent().equals((String)obj);
        return false;
    }

    @Override
    public String toString()
    {
        return getWord();
    }

    @Override
    public int compareTo(Stem stem)
    {
        return stem.getContent().compareTo(this.getContent());
    }
}
