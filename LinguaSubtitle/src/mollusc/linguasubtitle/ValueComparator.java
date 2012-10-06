package mollusc.linguasubtitle;

import java.util.Comparator;
import java.util.Map;

class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        int compare = base.get(b).compareTo(base.get(a));
        if(compare == 0)
        	return a.compareTo(b);
        return compare;
    }
}

