package ru.itmo.java;

public class HashTable {
    private static final float LOAD_FACTOR = 0.5f;
    private static final float CLEAN_FACTOR = 0.8f;
    private static final int INITIAL_SIZE = 1024;
    private static final int RESIZE_MULTIPLY = 2;
    private static final int STEP = 123457;
    private static final Entry DELETED = new Entry(null, null);

    private final float loadFactor;
    private Entry[] data;
    private int size = 0;
    private int realSize = 0;

    public HashTable() {
        this(INITIAL_SIZE, LOAD_FACTOR);
    }

    public HashTable(int initialSize) {
        this(initialSize, LOAD_FACTOR);
    }

    public HashTable(int initialSize, float loadFactor) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("Initial size should be 0 < initialSize.");
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Load factor should be 0 < loadFactor <= 1");
        }
        this.loadFactor = loadFactor;
        data = new Entry[initialSize];
    }

    public Object put(Object key, Object value) {
        int index = find(key);
        if (data[index] == null) {
            data[index] = new Entry(key, value);
            realSize++;
            size++;

            checkFullness();

            return null;
        }

        Object tmp = data[index].value;
        data[index].value = value;
        return tmp;
    }

    public Object get(Object key) {
        int index = find(key);
        if (data[index] == null) {
            return null;
        }

        return data[index].value;
    }

    public Object remove(Object key) {
        int index = find(key);
        if (data[index] == null) {
            return null;
        }

        Object tmp = data[index].value;
        data[index] = DELETED;
        size--;
        return tmp;
    }

    public int size() {
        return size;
    }

    private void checkFullness() {
        if (data.length * loadFactor < size) {
            resize(RESIZE_MULTIPLY);
        }
        if (data.length * CLEAN_FACTOR < realSize && CLEAN_FACTOR > loadFactor) {
            resize(1);
        }
    }

    private int find(Object key) {
        int index = getIndex(key);
        while (data[index] != null) {
            if (key.equals(data[index].key)) {
                return index;
            }

            index = nextHash(index);
        }

        return index;
    }


    private int getIndex(Object key) {
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
