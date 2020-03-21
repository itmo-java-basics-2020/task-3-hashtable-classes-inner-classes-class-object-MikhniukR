package ru.itmo.java;

public class HashTable {
    private static final float LOAD_FACTOR = 0.5f;
    private static final float CLEAN_FACTOR = 0.8f;
    private static final int INITIAL_SIZE = 1024;
    private static final int RESIZE_MULTIPLY = 2;
    private static final int STEP = 1;
    private static final Entry DELETED = new Entry(null, null);

    private final float loadFactor;
    private Entry[] data;
    private int size = 0;
    private int realSize = 0;

    HashTable(int initialSize, float loadFactor) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("Initial size should be 0 < initialSize.");
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Load factor should be 0 < loadFactor <= 1");
        }
        this.loadFactor = loadFactor;
        data = new Entry[initialSize];
    }

    HashTable(int initialSize) {
        this(initialSize, LOAD_FACTOR);
    }

    HashTable() {
        this(INITIAL_SIZE, LOAD_FACTOR);
    }

    private void checkFullness() {
        if (data.length * loadFactor < size) {
            resize(RESIZE_MULTIPLY);
        }
    }

    private int find(Object key) {
        int hash = getHash(key);
        while (data[hash] != null) {
            if (key.equals(data[hash].key)) {
                return hash;
            }

            hash = nextHash(hash);
        }

        return hash;
    }

    Object put(Object key, Object value) {
        int hash = find(key);
        if (data[hash] == null) {
            data[hash] = new Entry(key, value);
            realSize++;
            size++;

            checkFullness();

            return null;
        }

        Object tmp = data[hash].value;
        data[hash].value = value;
        return tmp;
    }

    Object get(Object key) {
        int hash = find(key);
        if (data[hash] == null) {
            return null;
        }

        return data[hash].value;
    }

    Object remove(Object key) {
        int hash = find(key);
        if (data[hash] == null) {
            return null;
        }

        Object tmp = data[hash].value;
        data[hash] = DELETED;
        size--;
        return tmp;
    }

    int size() {
        return size;
    }

    private int getHash(Object key) {
        int hash = key.hashCode() % data.length;
        if (hash < 0) {
            hash += data.length;
        }
        return hash;
    }

    private void resize(int factor) {
        Entry[] oldData = data;
        data = new Entry[oldData.length * factor];
        size = 0;
        realSize = 0;

        for (Entry entry : oldData) {
            if (entry != null && entry.key != null && entry.value != null) {
                put(entry.key, entry.value);
            }
        }
    }

    private int nextHash(int hash) {
        return (hash + STEP) % data.length;
    }

    private static class Entry {
        private Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
