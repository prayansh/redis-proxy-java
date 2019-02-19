package com.prayansh.redis.proxy.demo;

import com.prayansh.redis.proxy.demo.app.RedisCache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RedisCacheTests {

    RedisCache testCache1, testCache2;

    @BeforeEach
    public void runBefore() {
        testCache1 = new RedisCache();
        testCache2 = new RedisCache(1000, 10);
    }

    @AfterEach
    public void teardown() {
        testCache1.flush();
        testCache2.flush();
    }

    @Test
    public void testAdd() {
        testCache1.add("hello", "world");
        String hello = testCache1.retrieve("hello");
        assertEquals("world", hello);
    }

    @Test
    public void testAddExpired() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            testCache2.add("hello" + i, String.valueOf(i));
        }
        assertTrue(testCache2.hasKey("hello0"));
        assertTrue(testCache2.hasKey("hello4"));
        Thread.sleep(1001);
        assertNull(testCache2.retrieve("hello0"));
        assertNull(testCache2.retrieve("hello4"));
    }

    @Test
    public void testAddOverCapacityExpired() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            testCache2.add("hello" + i, String.valueOf(i));
        }
        Thread.sleep(800);
        assertTrue(testCache2.hasKey("hello0"));
        assertTrue(testCache2.hasKey("hello9")); // retrieve without resetting
        testCache2.add("foo", "bar");
        Thread.sleep(300);
        assertNull(testCache2.retrieve("hello0"));
        assertNull(testCache2.retrieve("hello9"));
        assertNotNull(testCache2.retrieve("foo"));
    }

    @Test
    public void testRefreshWhenAccessed() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            testCache2.add("hello" + i, String.valueOf(i));
        }
        Thread.sleep(800);
        assertNotNull(testCache2.retrieve("hello0"));
        assertNotNull(testCache2.retrieve("hello4"));
        testCache2.add("foo", "bar");
        Thread.sleep(300);
        assertNotNull(testCache2.retrieve("hello0"));
        assertNotNull(testCache2.retrieve("hello4"));
        assertNull(testCache2.retrieve("hello1"));
        assertNotNull(testCache2.retrieve("foo"));
    }

    @Test
    public void testAddOverCapacity() {
        for (int i = 0; i < 11; i++) {
            testCache2.add("hello" + i, String.valueOf(i));
        }
        assertNull(testCache2.retrieve("hello0"));
        assertNotNull(testCache2.retrieve("hello1"));
        assertNotNull(testCache2.retrieve("hello10"));
    }
}
