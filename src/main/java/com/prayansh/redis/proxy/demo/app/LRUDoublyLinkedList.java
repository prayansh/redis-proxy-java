package com.prayansh.redis.proxy.demo.app;

/**
 * A doubly linked list which uses the concept of least recently used for ordering elements
 * Doubly linked list so that we can access most recently used and least recently used in O(1)
 */
public class LRUDoublyLinkedList<T> {
    private class Node {
        T obj;
        Node next, prev;

        public Node(T obj) {
            this.obj = obj;
            next = prev = null;
        }
    }

    private Node front, back;

    public LRUDoublyLinkedList() {
        this.front = null;
        this.back = null;
    }

    public Node addNode(T item) {
        Node n = new Node(item);
        if (front == back && front == null) { // empty
            front = n;
            back = front;
        } else if (front == back) { // one item
            front = n;
            back.next = front;
            n.prev = back;
        } else {
            Node lFront = front;
            front = n;
            lFront.next = front;
            front.prev = lFront;
        }
        return n;
    }

    public boolean moveToFront(T item) {
        T obj = removeNode(item);
        if (obj != null) {
            addNode(obj);
            return true;
        }
        return false;
    }

    public T removeNode(T item) {
        T ret = null;
        if (front.obj == item && back.obj == front.obj) {
            back.next = null;
            back.prev = null;
            ret = back.obj;
            back = front = null;
        } else if (back.obj == item) { // comparing references so using ==
            Node remove = back;
            ret = remove.obj;
            back.next.prev = null;
            back = back.next;
            remove.next = null;
        } else if (front.obj == item) {
            Node remove = front;
            ret = remove.obj;
            front.prev.next = null;
            front = front.prev;
            remove.prev = null;
        } else {
            // Since we remove least recently used most of the time we iterate from the back
            Node iterator = back.next;
            while (iterator != null) {
                if (iterator.obj == item) {
                    Node prev = iterator.prev;
                    Node next = iterator.next;
                    ret = iterator.obj;
                    // Remove
                    prev.next = next;
                    next.prev = prev;
                    // Clear values
                    iterator.next = null;
                    iterator.prev = null;
                }
                iterator = iterator.next;
            }
        }
        return ret;
    }

    public void clear() {
        while (front != null && front.prev != null){
            front.next = null;
            front = front.prev;
        }
        front = back = null;
    }

    public T leastRecentlyUsed() {
        return back.obj;
    }

    public T mostRecentlyUsed() {
        return front.obj;
    }
}
