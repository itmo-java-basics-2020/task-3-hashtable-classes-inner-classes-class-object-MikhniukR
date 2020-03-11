package ru.itmo.java;

public class HashTable {
    private final float LOAD_FACTOR = 0.5f;
    private final float CLEAN_FACTOR = 0.8f;
    private final int INITIAL_SIZE = 1024;
    private final int RESIZE_MULTIPLY = 2;

    private final float loadFactor;
    private Entry[] data;
    private int size = 0;
    private int realSize = 0;

    HashTable(int initialSize, float loadFactor) {
        this.loadFactor = loadFactor;
        data = new Entry[initialSize];
    }

    HashTable(int initialSize) {
        loadFactor = LOAD_FACTOR;
        data = new Entry[initialSize];
    }

    HashTable() {
        loadFactor = LOAD_FACTOR;
        data = new Entry[INITIAL_SIZE];
    }

    Object put(Object key, Object value) {
        int hash = getHash(key);
        while(data[hash] != null) {
            if(key.equals(data[hash].key)) {
                Object tmp = data[hash].value;
                data[hash].value = value;
                return tmp;
            }

            hash++;
            if(hash == data.length) {
                hash = 0;
            }
        }

        data[hash] = new Entry(key, value);
        realSize++;
        size++;

        if(data.length * loadFactor < size) {
            resize(RESIZE_MULTIPLY);
        }
        if(data.length * CLEAN_FACTOR < realSize && CLEAN_FACTOR > loadFactor) {
            resize(1);
        }
        return null;
    }

    Object get(Object key) {
        int hash = getHash(key);
        while(data[hash] != null) {
            if(key.equals(data[hash].key)) {
                return data[hash].value;
            }

            hash++;
            if(hash == data.length) {
                hash = 0;
            }
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

            hash++;
            if(hash == data.length) {
                hash = 0;
            }
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

    private class Entry {
        private Object key, value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
