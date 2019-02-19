package com.prayansh.redis.proxy.demo.app;

import java.util.HashMap;
import java.util.Map;

public class RedisCache {

    private class CacheItem {
        long timeLastUsed;
        String key, val;

        public CacheItem(String key, String val) {
            reset();
            this.key = key;
            this.val = val;
        }

        public boolean isValid(long timeToExpire) {
            return timeLastUsed + timeToExpire > System.currentTimeMillis();
        }

        public synchronized void reset() {
            timeLastUsed = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "(" + key + ", " + val + ")";
        }
    }

    private final long timeToExpire;
    private final int capacity;
    private Map<String, CacheItem> cache;
    private LRUDoublyLinkedList<CacheItem> lruList;

    public RedisCache(long timeToExpireInMilliseconds, int capacity) {
        cache = new HashMap<>();
        this.timeToExpire = timeToExpireInMilliseconds;
        this.capacity = capacity;
        lruList = new LRUDoublyLinkedList<>();
    }

    public RedisCache() {
        this(30000, 30);
    }

    public String retrieve(String key) {
        CacheItem item = cache.get(key);
        String returnVal = null;
        if (item != null) { // Is in cache
            if (item.isValid(timeToExpire)) { // value has not expired
                returnVal = item.val;
                item.reset();
                lruList.moveToFront(item);
            } else { // value has expired, remove from cache
                cache.remove(key);
                lruList.removeNode(item);
            }
        }
        return returnVal; // values not in cache, or has expired
    }

    public void add(String key, String value) {
        CacheItem cacheItem = cache.get(key);
        if (cacheItem != null) { // already in cache, update value
            cacheItem.val = value;
            cacheItem.reset();
            lruList.moveToFront(cacheItem);
        } else { // not in cache
            if (cache.size() == capacity) { // cache is full, remove expired LRU entries
                removeExpiredEntries();
            }
            if (cache.size() == capacity) { // No expired entries found, explicitly remove LRU entry
                removeLRU();
            }
            CacheItem newItem = new CacheItem(key, value);
            cache.put(key, newItem);
            lruList.addNode(newItem);
        }
    }

    public void updateKeyIfPresent(String key, String value) {
        CacheItem item = cache.get(key);
        if (item != null) {
            item.val = value;
        }
    }

    // Return true if key is in cache and not expired, otherwise return false
    public boolean hasKey(String key) {
        CacheItem cacheItem = cache.get(key);
        return cacheItem != null && cacheItem.isValid(timeToExpire);
    }

    public int size() {
        return cache.size();
    }

    public void flush() {
        cache.clear();
        lruList.clear();
    }

    private void remove(CacheItem i) {
        lruList.removeNode(i);
        cache.remove(i.key);
    }

    private void removeExpiredEntries() {
        while (!lruList.leastRecentlyUsed().isValid(timeToExpire)) {
            remove(lruList.leastRecentlyUsed());
        }
    }

    private void removeLRU() {
        remove(lruList.leastRecentlyUsed());
    }
}
