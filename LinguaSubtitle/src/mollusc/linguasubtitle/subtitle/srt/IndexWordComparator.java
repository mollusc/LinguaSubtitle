package mollusc.linguasubtitle.subtitle.srt;

import java.util.Comparator;

/**
 * Sort IndexWord
 * @author mollusc
 *
 */
public class IndexWordComparator implements Comparator<IndexWord> {
	@Override
	public int compare(IndexWord arg0, IndexWord arg1) {
		int compare = arg0.indexSpeech - arg1.indexSpeech;
		if (compare == 0)
			return arg1.start - arg0.start;
		return compare;
	}
}