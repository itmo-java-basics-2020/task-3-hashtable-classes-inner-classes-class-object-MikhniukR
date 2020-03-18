package ru.itmo.java;

public class HashTable {
    private static final float LOAD_FACTOR = 0.5f;
    private static final float CLEAN_FACTOR = 0.8f;
    private static final int INITIAL_SIZE = 1024;
    private static final int RESIZE_MULTIPLY = 2;
    private static final int MAX_INITIAL_SIZE = 1_000_000;
    private static final int STEP = 1009;

    private final float loadFactor;
    private Entry[] data;
    private int size = 0;
    private int realSize = 0;

    HashTable(int initialSize, float loadFactor) {
        if(initialSize <= 0 || initialSize > MAX_INITIAL_SIZE) {
            throw new IllegalArgumentException("Initial size should be 0 < initialSize < " + MAX_INITIAL_SIZE);
        }
        if(loadFactor <= 0 || loadFactor > 1) {
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
        if(data.length * loadFactor < size) {
            resize(RESIZE_MULTIPLY);
        }
        if(data.length * CLEAN_FACTOR < realSize && CLEAN_FACTOR > loadFactor) {
            resize(1);
        }
    }

    Object put(Object key, Object value) {
        int hash = getHash(key);
        while(data[hash] != null) {
            if(key.equals(data[hash].key)) {
                Object tmp = data[hash].value;
                data[hash].value = value;
                return tmp;
            }

            hash = nextHash(hash);
        }

        data[hash] = new Entry(key, value);
        realSize++;
        size++;

        checkFullness();

        return null;
    }

    Object get(Object key) {
        int hash = getHash(key);
        while(data[hash] != null) {
            if(key.equals(data[hash].key)) {
                return data[hash].value;
            }

            hash = nextHash(hash);
        }

        return null;
    }

    Object remove(Object key) {
        int hash = getHash(key);
        while(data[hash] != null) {
            if(key.equals(data[hash].key)) {
                Object tmp = data[hash].value;
                data[hash].key = null;
                data[hash].value = null;
                size--;
                return tmp;
            }

            hash = nextHash(hash);
        }

        return null;
    }

    int size() {
        return size;
    }

    private int getHash(Object key) {
        int hash = key.hashCode() % data.length;
        if(hash < 0) {
            hash += data.length;
        }
        return hash;
    }

    private void resize(int factor) {
        Entry[] oldData = data;
        data = new Entry[oldData.length * factor];
        size = 0;
        realSize = 0;

        for(Entry entry : oldData) {
            if(entry != null  && entry.key != null && entry.value != null) {
                put(entry.key, entry.value);
            }
        }
    }

    private int nextHash(int hash) {
        return (hash + STEP) % data.length;
    }

    private class Entry {
        private Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
