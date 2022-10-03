package queue;

import java.util.Objects;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
public abstract class AbstractQueue implements Queue {

    protected int size;

    public void enqueue(Object obj) {
        Objects.requireNonNull(obj);
        enqueueImpl(obj);
        this.size++;
    }

    protected abstract void enqueueImpl(Object obj);

    public Object element() {
        assert this.size > 0;
        return elementImpl();
    }

    protected abstract Object elementImpl();

    public Object dequeue() {

        assert this.size > 0;

        this.size--;
        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {

        clearImpl();
        this.size = 0;

    }

    protected abstract void clearImpl();
}
