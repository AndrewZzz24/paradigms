package queue;

import java.util.function.Predicate;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

/*
    Model:
    a[1]..a[n]
    n -- size of queue

    Invariant: n >= 0 && forall i=1...n: a[i] != null

    Let immutable(n): forall i=1..n: a[i] == a[i]'

    Pred: element != null
    Post: n' == n + 1 && a'[n'] == element && immutable(n)
    enqueue(element)

    Pred: n > 0
    Post: R == a[1] && immutable(n)
    element

    Pred: n > 0
    Post: R == a[1] && n' == n - 1 && immutable(n')
    dequeue

    Pred: true
    Post: immutable(n) && R == n && n >= 0
    size

    Pred: true
    Post: immutable(n) && R == (n == 0)
    isEmpty

    Pred: true
    Post: n' == 0 && immutable(n)
    clear

    Pred: true
    Post: R == count of x in queue && immutable(n)
    count(x)

    Pred: true
    Post: R == count of (forall 1..n: predicate(a[x]) == true) && immutable(n);
    countIf
 */

public class ArrayQueue extends AbstractQueue {

    private Object[] elements = new Object[2];
    private int capacity = 2;

    private int begin;
    private int end;

    @Override
    protected void enqueueImpl(Object obj) {
        this.ensureCapacity();
        this.elements[this.end++] = obj;
        this.end = this.end % this.capacity;
    }

    @Override
    protected Object elementImpl() {
        return this.elements[this.begin];
    }

    @Override
    protected Object dequeueImpl() {

        var result = this.elements[this.begin];
        this.elements[this.begin] = null;
        this.begin++;
        this.begin = this.begin % this.capacity;
        //this.decreaseCapacity();

        return result;
    }

    @Override
    protected void clearImpl() {

        this.elements = new Object[2];
        this.begin = 0;
        this.end = 0;
        this.capacity = 2;

    }

    @Override
    public int count(Object value) {
        // :NOTE: copypaste
        return countIf((object) -> object.equals(value));
    }

    @Override
    public int countIf(Predicate<Object> predicate) {

        int iter = this.begin;
        int result = 0;

        while (iter != this.end) {
            if (predicate.test(this.elements[iter]))
                result++;
            iter++;
            iter = iter % this.capacity;
        }
        return result;
    }

    private void ensureCapacity() {

        if (this.size + 1 >= this.capacity) {
            this.elements = makeArray(this.capacity * 2);
            this.capacity = this.capacity * 2;
            this.begin = 0;
            this.end = this.size;
        }

    }

    private void decreaseCapacity() {

        if (this.size < this.capacity / 4) {
            this.elements = makeArray(this.capacity / 2);
            this.capacity = this.capacity / 2;
            this.begin = 0;
            this.end = this.size;
        }

    }

    private Object[] makeArray(int givenCapacity) {

        if (this.size == 0) {
            return new Object[givenCapacity];
        }

        Object[] newElements = new Object[givenCapacity];
        int i = this.begin;
        int newArrIter = 0;

        do {
            newElements[newArrIter] = this.elements[i];
            i++;
            i = i % this.capacity;
            newArrIter++;
        } while (i != this.end);

        return newElements;

    }
}
