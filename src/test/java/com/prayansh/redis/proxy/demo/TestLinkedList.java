package com.prayansh.redis.proxy.demo;

import com.prayansh.redis.proxy.demo.app.LRUQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SuppressWarnings({"Duplicates"})
public class TestLinkedList {

    private class TestStruct {
        int a, b;

        public TestStruct(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    LRUQueue<TestStruct> testList;
    TestStruct t1, t2, t3;

    @BeforeEach
    public void runBefore() {
        testList = new LRUQueue<>();
        t1 = new TestStruct(1, 2);
        t2 = new TestStruct(11, 12);
        t3 = new TestStruct(21, 22);
    }

    @Test
    public void testAddOne() {
        testList.addNode(t1);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertEquals(1, testStruct.a);
        assertEquals(2, testStruct.b);
    }

    @Test
    public void testAddMultiple() {
        testList.addNode(t1);
        TestStruct testStruct1 = testList.leastRecentlyUsed();
        testList.addNode(t2);
        TestStruct testStruct2 = testList.leastRecentlyUsed();
        testList.addNode(t3);
        TestStruct testStruct3 = testList.leastRecentlyUsed();
        assertSame(testStruct1, testStruct2);
        assertSame(testStruct2, testStruct3);
        assertSame(testStruct1, testStruct3);
    }

    @Test
    public void testRemoveFirst() {
        LRUQueue<TestStruct>.Node node1 = testList.addNode(t1);
        LRUQueue<TestStruct>.Node node2 = testList.addNode(t2);
        LRUQueue<TestStruct>.Node node3 = testList.addNode(t3);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertEquals(1, testStruct.a);
        assertEquals(2, testStruct.b);
        testStruct = testList.mostRecentlyUsed();
        assertEquals(21, testStruct.a);
        assertEquals(22, testStruct.b);
        testList.removeNode(node3);
        testStruct = testList.mostRecentlyUsed();
        assertEquals(11, testStruct.a);
        assertEquals(12, testStruct.b);
    }

    @Test
    public void testRemoveLast() {
        LRUQueue<TestStruct>.Node node1 = testList.addNode(t1);
        LRUQueue<TestStruct>.Node node2 = testList.addNode(t2);
        LRUQueue<TestStruct>.Node node3 = testList.addNode(t3);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertEquals(1, testStruct.a);
        assertEquals(2, testStruct.b);
        testList.removeNode(node1);
        testStruct = testList.leastRecentlyUsed();
        assertEquals(11, testStruct.a);
        assertEquals(12, testStruct.b);
    }

    @Test
    public void testRemoveMiddle() {
        LRUQueue<TestStruct>.Node node1 = testList.addNode(t1);
        LRUQueue<TestStruct>.Node node2 = testList.addNode(t2);
        LRUQueue<TestStruct>.Node node3 = testList.addNode(t3);
        testList.removeNode(node2);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertEquals(1, testStruct.a);
        assertEquals(2, testStruct.b);
    }

    @Test
    public void testRemoveMultiple() {
        LRUQueue<TestStruct>.Node node1 = testList.addNode(t1);
        LRUQueue<TestStruct>.Node node2 = testList.addNode(t2);
        LRUQueue<TestStruct>.Node node3 = testList.addNode(t3);
        testList.removeNode(node2);
        testList.removeNode(node1);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertEquals(21, testStruct.a);
        assertEquals(22, testStruct.b);
    }

    @Test
    public void testMoveToFront() {
        LRUQueue<TestStruct>.Node node1 = testList.addNode(t1);
        LRUQueue<TestStruct>.Node node2 = testList.addNode(t2);
        LRUQueue<TestStruct>.Node node3 = testList.addNode(t3);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertSame(t1, testStruct);
        testList.moveToFront(node1);
        testStruct = testList.leastRecentlyUsed();
        assertSame(t2, testStruct);
    }

    @Test
    public void testMoveToFront2() {
        LRUQueue<TestStruct>.Node node = testList.addNode(t1);
        TestStruct testStruct = testList.leastRecentlyUsed();
        assertSame(t1, testStruct);
        testList.moveToFront(node);
        testStruct = testList.leastRecentlyUsed();
        assertSame(t1, testStruct);
    }
}
