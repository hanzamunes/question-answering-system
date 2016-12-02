package core;

import java.util.Comparator;
import java.util.Map.Entry;

public class ScoreComparator<T> implements Comparator<Entry<T, Double>> {

	@Override
	public int compare(Entry<T, Double> a, Entry<T, Double> b) {
		return a.getValue().compareTo(b.getValue());
	}
}