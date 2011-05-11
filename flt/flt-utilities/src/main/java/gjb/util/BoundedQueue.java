package gjb.util;

import java.util.LinkedList;
import java.util.Iterator;

public class BoundedQueue<T> {

	protected LinkedList<T> queue;
	protected int maxSize;

	public BoundedQueue(int maxSize) {
		setMaxSize(maxSize);
		queue = new LinkedList<T>();
	}

	public BoundedQueue(BoundedQueue<T> q) {
		queue = new LinkedList<T>(q.queue);
		setMaxSize(q.maxSize());
	}

	public int maxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public int size() {
		return queue.size();
	}

	public boolean isEmpty() {
		return queue.size() == 0;
	}

	public T push(T in) {
		if (queue.size() < maxSize) {
			queue.addLast(in);
			return null;
		} else {
			T out = queue.removeFirst();
			queue.addLast(in);
			return out;
		}
	}

	public T pop() {
		if (queue.isEmpty()) {
			return null;
		} else {
			return queue.removeFirst();
		}
	}

	public Iterator<T> iterator() {
		return queue.iterator();
	}

	public String signature() {
		StringBuffer str = new StringBuffer();
		if (!isEmpty()) {
			str.append(queue.get(0).toString());
			for (int i = 1; i < size(); i++) {
				str.append("\n");
				str.append(String.valueOf(queue.get(i).toString()));
			}
		}
		return str.toString();
	}

}
