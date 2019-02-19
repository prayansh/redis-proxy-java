package com.prayansh.redis.proxy.demo;

import com.prayansh.redis.proxy.demo.app.RedisProxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRedisProxy {
    private RedisProxy proxy;

    @BeforeEach
    public void setup() {
        proxy = new RedisProxy("localhost", 6379);
    }

    @Test
    public void testPing() {
        assertEquals("PONG", proxy.ping());
    }

    @Test
    public void testSetAndGet() {
        proxy.set("foo", "bar");
        assertEquals("bar", proxy.get("foo"));
        proxy.flushDB();
    }

    @Test
    public void testSetAndGetMultiple() {
        proxy.set("foo", "bar");
        proxy.set("bar", "foo");
        assertEquals("bar", proxy.get("foo"));
        assertEquals("foo", proxy.get("bar"));
        proxy.flushDB();
    }

    @Test
    public void testKeyUpdate() {
        proxy.set("foo", "bar");
        assertEquals("bar", proxy.get("foo"));
        proxy.set("foo", "bar2");
        assertEquals("bar2", proxy.get("foo"));
        proxy.flushDB();
    }

    @Test
    public void testConcurrentSet() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            proxy.set("foo", "bar");
            proxy.set("foobar", "barfoo");
        });
        Thread t2 = new Thread(() -> {
            proxy.set("bar", "bar");
            proxy.set("foobar", "foobar");
        });
        Thread t3 = new Thread(() -> {
            proxy.set("foo", "foo");
            proxy.set("bar", "foo");
        });
        final ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(t1);
        service.execute(t2);
        service.execute(t3);
        Thread.sleep(200);
        assertEquals("foo", proxy.get("foo"));
        assertEquals("foobar", proxy.get("foobar"));
        assertEquals("foo", proxy.get("bar"));
        Thread.sleep(200);
        service.execute(t2);
        service.execute(t3);
        service.execute(t1);
        Thread.sleep(200);
        assertEquals("bar", proxy.get("foo"));
        assertEquals("barfoo", proxy.get("foobar"));
        assertEquals("foo", proxy.get("bar"));
        service.shutdown();
        proxy.flushDB();
    }
}
