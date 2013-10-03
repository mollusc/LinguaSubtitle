package mollusc.linguasubtitle.index;

import java.util.Comparator;

/**
 * @author mollusc
 * Sort IndexWord
 */
 public class IndexWordComparator implements Comparator<IndexWord> {
	//<editor-fold desc="Public Methods">
	@Override
	public int compare(IndexWord arg0, IndexWord arg1) {
		int compare = arg0.indexSpeech - arg1.indexSpeech;
		if (compare == 0)
			return arg1.start - arg0.start;
		return compare;
	}
	//</editor-fold>
}