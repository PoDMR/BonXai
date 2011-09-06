package eu.fox7.util;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class CarthesianProduct {

	protected Collection[] vectors;

	public CarthesianProduct(List vectorList) {
		vectors = (Collection[]) vectorList.toArray(new Collection[vectorList.size()]);
	}

	public List all() {
		List result = new LinkedList();
		for (Iterator it = iterator(); it.hasNext(); ) {
			result.add(it.next());
		}
		return result;
	}

	public Iterator iterator() {
		return new Iterator(vectors);
	}

	protected static class Iterator implements java.util.Iterator {

		protected Collection[] vectors;
		protected java.util.Iterator[] iterators;
		protected Object[] current;
		protected long[] modulo;
		protected long size;
		protected int iteratorIndex;

		Iterator(Collection[] vectorArray) {
			this.vectors = vectorArray;
			iterators = new java.util.Iterator[vectors.length];
			current = new Object[vectors.length];
			modulo = new long[vectors.length];
			size = 1;
			for (int i = 0; i < vectors.length; i++) {
				modulo[i] = size;
				size *= vectors[i].size();
			}
			reset();
		}

		protected void reset() {
			if (size() > 0) {
				for (int i = 0; i < vectors.length; i++) {
					iterators[i] = vectors[i].iterator();
					current[i] = iterators[i].next();
				}
			}
			iteratorIndex = 1;
		}

		public long size() {
			return size;
		}

		public boolean hasNext() {
			return iteratorIndex <= size();
		}

		public Object next() {
 			List list = new ArrayList();
			for (int i = 0; i < current.length; i++) {
				list.add(current[i]);
			}
			if (iteratorIndex < size()) {
				for (int i = modulo.length - 1; i >= 0; i--) {
					if (iteratorIndex % modulo[i] == 0) {
						current[i] = iterators[i].next();
						for (int j = 0; j < i; j++) {
							iterators[j] = vectors[j].iterator();
							current[j] = iterators[j].next();
						}
						break;
					}
				}
			} else if (iteratorIndex > size()) {
				throw new java.util.NoSuchElementException();
			}
			iteratorIndex++;
 			return list;
		}

 		public void remove() {}

	}

}
