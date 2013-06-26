package mollusc.linguasubtitle.subtitle.parser;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;

/**
 * Class for getting stem from a word
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class Stem implements Comparable<Stem> {

    private String stem;
    private String word;
    private String language;

    /**
     * Get stem
     *
     * @return
     */
    public String getStem() {
        return stem;
    }

    /**
     * Get word of the stem
     *
     * @return
     */
    public String getWord() {
        return word;
    }

    /**
     * Set word of the stem
     *
     * @param word
     */
    public void setWord(String word) {
        this.word = word;
    }

    /**
     * Get language
     * @return
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Initialize stem
     *
     * @param word
     */
    public Stem(String word, String language) {
        this.word = word;
        this.language = language;
        stem = stemmingWord(this.word.toLowerCase(), language);
    }

    /**
     * Initialize stem
     *
     * @param word
     */
    public Stem(String stem, String word, String language) {
        this.word = word;
        this.stem = stem;
        this.language = language;
    }

    /**
     * Get stem from the word
     *
     * @param word
     * @param language - Language of the word. (danish, dutch,
     *                 swedish, finnish, hungarian,
     *                 norwegian, romanian, english,
     *                 french, german, italian,
     *                 portuguese, russian, spanish, turkish)
     * @return
     */
    public static String stemmingWord(String word, String language) {
        try {
            Class stemClass = Class.forName("org.tartarus.snowball.ext." + language + "Stemmer");
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            stemmer.setCurrent(word);
            stemmer.stem();
            return stemmer.getCurrent();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Stem)
            return this.getStem().equals(((Stem) obj).getStem());
        if (obj instanceof String)
            return this.getStem().equals((String) obj);
        return false;
    }

    @Override
    public String toString() {
        return getWord();
    }

    @Override
    public int compareTo(Stem stem) {
        return stem.getStem().compareTo(this.getStem());
    }
}
