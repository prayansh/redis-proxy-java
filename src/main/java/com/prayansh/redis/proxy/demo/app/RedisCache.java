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
    private Map<String, LRUQueue<CacheItem>.Node> cache;
    private LRUQueue<CacheItem> lruList;

    public RedisCache(long timeToExpireInMilliseconds, int capacity) {
        cache = new HashMap<>();
        this.timeToExpire = timeToExpireInMilliseconds;
        this.capacity = capacity;
        lruList = new LRUQueue<>();
    }

    public RedisCache() {
        this(30000, 30);
    }

    public String retrieve(String key) {
        LRUQueue<CacheItem>.Node item = cache.get(key);
        String returnVal = null;
        if (item != null) { // Is in cache
            if (item.obj.isValid(timeToExpire)) { // value has not expired
                returnVal = item.obj.val;
                item.obj.reset();
                LRUQueue<CacheItem>.Node updatedNode = lruList.moveToFront(item);
                cache.put(key, updatedNode);
            } else { // value has expired, remove from cache
                lruList.removeNode(item);
                cache.remove(key);
            }
        }
        return returnVal; // values not in cache, or has expired
    }

    public void add(String key, String value) {
        LRUQueue<CacheItem>.Node cacheItem = cache.get(key);
        if (cacheItem != null) { // already in cache, update value
            cacheItem.obj.val = value;
            cacheItem.obj.reset();
            LRUQueue<CacheItem>.Node updatedNode = lruList.moveToFront(cacheItem);
            cache.put(key, updatedNode);
        } else { // not in cache
            if (cache.size() == capacity) { // cache is full, remove expired LRU entries
                removeExpiredEntries();
            }
            if (cache.size() == capacity) { // No expired entries found, explicitly remove LRU entry
                removeLRU();
            }
            CacheItem newItem = new CacheItem(key, value);
            LRUQueue<CacheItem>.Node node = lruList.addNode(newItem);
            cache.put(key, node);
        }
    }

    public void updateKeyIfPresent(String key, String value) {
        LRUQueue<CacheItem>.Node item = cache.get(key);
        if (item != null) {
            item.obj.val = value;
        }
    }

    // Return true if key is in cache and not expired, otherwise return false
    public boolean hasKey(String key) {
        LRUQueue<CacheItem>.Node cacheItem = cache.get(key);
        return cacheItem != null && cacheItem.obj != null && cacheItem.obj.isValid(timeToExpire);
    }

    public int size() {
        return cache.size();
    }

    public void flush() {
        cache.clear();
        lruList.clear();
    }

    private void remove(CacheItem i) {
        lruList.removeNode(cache.get(i.key));
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
