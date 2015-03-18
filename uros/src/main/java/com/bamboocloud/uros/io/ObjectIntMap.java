package com.bamboocloud.uros.io;

public final class ObjectIntMap {

	static class Entry {

		final Object key;
		int value;
		Entry next;
		final int hash;

		Entry(int h, Object k, int v, Entry n) {
			value = v;
			next = n;
			key = k;
			hash = h;
		}
	}

	static final int DEFAULT_INITIAL_CAPACITY = 16;
	static final int MAXIMUM_CAPACITY = 1 << 30;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	Entry[] table;
	int size;
	int threshold;
	final float loadFactor;

	public ObjectIntMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		this.table = new Entry[DEFAULT_INITIAL_CAPACITY];
	}

	void transfer(Entry[] newTable) {
		Entry[] src = this.table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			Entry e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					Entry next = e.next;
					int i = e.hash & (newCapacity - 1);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	void resize(int newCapacity) {
		Entry[] oldTable = this.table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			this.threshold = Integer.MAX_VALUE;
			return;
		}

		Entry[] newTable = new Entry[newCapacity];
		transfer(newTable);
		this.table = newTable;
		this.threshold = (int) (newCapacity * this.loadFactor);
	}

	void addEntry(int hash, Object key, int value, int bucketIndex) {
		Entry e = this.table[bucketIndex];
		this.table[bucketIndex] = new Entry(hash, key, value, e);
		if (this.size++ >= this.threshold) {
			resize(2 * this.table.length);
		}
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public void clear() {
		Entry[] tab = this.table;
		for (int i = 0; i < tab.length; ++i) {
			tab[i] = null;
		}
		this.size = 0;
	}

	public int get(Object key) {
		if (key == null) {
			for (Entry e = this.table[0]; e != null; e = e.next) {
				if (e.key == null) {
					return e.value;
				}
			}
		} else {
			int hash = System.identityHashCode(key);
			int i = hash & (this.table.length - 1);
			for (Entry e = this.table[i]; e != null; e = e.next) {
				if (e.hash == hash && e.key == key) {
					return e.value;
				}
			}
		}
		return -1;
	}

	public boolean containsKey(Object key) {
		if (key == null) {
			for (Entry e = this.table[0]; e != null; e = e.next) {
				if (e.key == null) {
					return true;
				}
			}
		} else {
			int hash = System.identityHashCode(key);
			int i = hash & (this.table.length - 1);
			for (Entry e = this.table[i]; e != null; e = e.next) {
				if (e.hash == hash && e.key == key) {
					return true;
				}
			}
		}
		return false;
	}

	public int put(Object key, int value) {
		if (key == null) {
			for (Entry e = this.table[0]; e != null; e = e.next) {
				if (e.key == null) {
					int oldValue = e.value;
					e.value = value;
					return oldValue;
				}
			}
			this.addEntry(0, key, value, 0);
		} else {
			int hash = System.identityHashCode(key);
			int i = hash & (this.table.length - 1);
			for (Entry e = this.table[i]; e != null; e = e.next) {
				if (e.hash == hash && e.key == key) {
					int oldValue = e.value;
					e.value = value;
					return oldValue;
				}
			}
			this.addEntry(hash, key, value, i);
		}
		return -1;
	}
}