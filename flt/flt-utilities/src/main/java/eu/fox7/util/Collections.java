package eu.fox7.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class Collections {

	public static<T> T extractSingleton(Set<T> set) {
		if (set.size() == 1) {
            Iterator<T> it = set.iterator();
            return it.next();
		} else {
			return null;
		}
	}

	public static<T> T takeOne(Set<T> set) {
		if (set.size() > 0) {
			Iterator<T> it = set.iterator();
			T o = it.next();
			set.remove(o);
			return o;
		} else {
			return null;
		}
	}

	public static<T> T getOne(Set<T> set) {
        if (set.size() > 0) {
            Iterator<T> it = set.iterator();
            T o = it.next();
            return o;
        } else {
            return null;
        }
	}

	public static<T> Set<T> intersect(Set<T> set1, Set<T> set2) {
        Set<T> intersection = new HashSet<T>();
        for (T element : set1) {
            if (set2.contains(element))
                intersection.add(element);
        }
        return intersection;
    }

}
