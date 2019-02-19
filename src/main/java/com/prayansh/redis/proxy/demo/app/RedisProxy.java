package com.prayansh.redis.proxy.demo.app;

import redis.clients.jedis.Jedis;

public class RedisProxy {
    private RedisCache cache;
    private Jedis jedis;

    public RedisProxy(String hostname, int port,
                      long timeToExpireInMilliseconds, int cacheCapacity) {
        System.out.printf("Starting RedisProxy with config hostname:%s, port:%d, ttlMillis:%d, cacheCap:%d\n",
                hostname, port, timeToExpireInMilliseconds, cacheCapacity);
        jedis = new Jedis(hostname, port);
        cache = new RedisCache(timeToExpireInMilliseconds, cacheCapacity);
    }

    public RedisProxy(String hostname, int port) {
        jedis = new Jedis(hostname, port);
        cache = new RedisCache();
    }

    public RedisProxy() {
        jedis = new Jedis();
        cache = new RedisCache();
    }

    public synchronized String get(String key) {
        String value = cache.retrieve(key);
        if (value == null) {
            String valueJ = jedis.get(key);
            if (valueJ != null) {
                value = valueJ;
                cache.add(key, valueJ);
            }
        }
        return value;
    }

    public synchronized void set(String key, String value) {
        jedis.set(key, value);
        cache.updateKeyIfPresent(key, value);
    }

    public int getCacheSize() {
        return cache.size();
    }

    /*
     * Jedis Functions
     */

    /**
     * Ping function for testing if server is up
     */
    public String ping() {
        return jedis.ping();
    }

    /**
     * Clear values in Redis instance
     */
    public String flushDB() {
        cache.flush();
        return jedis.flushDB();
    }
}
