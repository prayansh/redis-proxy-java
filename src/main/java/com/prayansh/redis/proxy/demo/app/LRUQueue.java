package com.prayansh.redis.proxy.demo.app;

/**
 * A doubly linked list which uses the concept of least recently used for ordering elements
 * Doubly linked list so that we can access most recently used and least recently used in O(1)
 */
public class LRUQueue<T> {
    public class Node {
        T obj;
        Node next, prev;

        public Node(T obj) {
            this.obj = obj;
            next = prev = null;
        }
    }

    private Node front, back;

    public LRUQueue() {
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
            front.prev = back;
        } else {
            Node lFront = front;
            front = n;
            lFront.next = front;
            front.prev = lFront;
        }
        return n;
    }

    public Node moveToFront(Node item) {
        T obj = removeNode(item);
        if (obj != null) {
            return addNode(obj);
        }
        return null;
    }

    public T removeNode(Node node) {
        if (node != null) {
            T obj = node.obj;
            if (front == node && back == front) {
                node.next = null;
                node.prev = null;
                back = front = null;
            } else if (back == node) { // comparing references so using ==
                back.next.prev = null;
                back = back.next;
                node.next = null;
                node.prev = null;
            } else if (front == node) {
                front.prev.next = null;
                front = front.prev;
                node.next = null;
                node.prev = null;
            } else {
                Node prev = node.prev;
                Node next = node.next;
                // Remove
                prev.next = next;
                next.prev = prev;
                // Clear values
                node.next = null;
                node.prev = null;
            }
            return obj;
        }
        return null;
    }

    public void clear() {
        while (front != null && front.prev != null) {
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
