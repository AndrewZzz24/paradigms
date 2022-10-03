package queue;

import java.util.function.Predicate;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
public class LinkedQueue extends AbstractQueue {

    private Node begin;
    private Node end;

    private static class Node {

        private final Object _data;
        private Node _next;

        private Node(Object data, Node next) {
            this._data = data;
            this._next = next;
        }

    }

    @Override
    protected void enqueueImpl(Object obj) {
        Node node = new Node(obj, null);
        if (this.begin == null) {
            this.begin = node;
            this.end = node;
        } else {
            this.end._next = node;
            this.end = this.end._next;
        }
    }

    @Override
    protected Object elementImpl() {
        return this.begin._data;
    }

    @Override
    protected Object dequeueImpl() {
        var result = this.begin._data;
        if (this.begin == this.end) {
            this.begin = null;
            this.end = null;
        } else this.begin = this.begin._next;
        return result;
    }

    @Override
    protected void clearImpl() {
        this.begin = null;
        this.end = null;
    }

    @Override
    public int count(Object value) {
        return countIf((object) -> object.equals(value));
    }

    @Override
    public int countIf(Predicate<Object> predicate) {

        int result = 0;

        Node iter = this.begin;

        while (iter != null) {
            if (predicate.test(iter._data))
                result++;
            iter = iter._next;
        }
        return result;
    }

}
