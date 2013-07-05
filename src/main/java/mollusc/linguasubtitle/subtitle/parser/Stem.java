package mollusc.linguasubtitle.subtitle.parser;

import org.tartarus.snowball.SnowballStemmer;

/**
 * Class for getting stem from a word
 *
 * @author mollusc <MolluscLab@gmail.com>
 */
public class Stem implements Comparable<Stem> {

	private final String stem;
	private final String word;

	/**
	 * Get stem
	 */
	public String getStem() {
		return stem;
	}

	/**
	 * Get word of the stem
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Initialize stem
	 */
	public Stem(String word, String language) {
		this.word = word;
		stem = stemmingWord(this.word.toLowerCase(), language);
	}

	/**
	 * Initialize stem
	 */
	/*public Stem(String stem, String word) {
		this.word = word;
		this.stem = stem;
	}*/

	/**
	 * Get stem from the word
	 *
	 * @param word is word for stemming
	 * @param language - Language of the word. (danish, dutch,
	 *                 swedish, finnish, hungarian,
	 *                 norwegian, romanian, english,
	 *                 french, german, italian,
	 *                 portuguese, russian, spanish, turkish)
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
		return obj instanceof String && this.getStem().equals(obj);
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
