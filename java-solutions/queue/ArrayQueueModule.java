package queue;

import java.util.Objects;

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

// :NOTE: incorrect postcondition; also for 'clear'
    Pred: n > 0
    Post: R == a[1] && n' == n - 1 && immutable(2..n)
    dequeue

    Pred: true
    Post: immutable(n) && R == n && n >= 0
    size

    Pred: true
    Post: immutable(n) && R == (n == 0)
    isEmpty

    Pred: true
    Post: n' == 0
    clear

    Pred: true
    Post: R == count of x in queue && immutable(n)
    count(x)
 */

public class ArrayQueueModule {

    private static int size;
    private static Object[] elements = new Object[2];

    private static int capacity = 2;
    private static int begin;
    private static int end;


    public static void enqueue(Object obj) {

        Objects.requireNonNull(obj);
        ensureCapacity();

        elements[end++] = obj;
        end = end % capacity;
        size++;

    }


    public static Object element() {

        assert size > 0;
        return elements[begin];

    }

    public static Object dequeue() {

        assert size > 0;
        size--;
        var result = elements[begin];
        elements[begin] = null;
        begin++;
        begin = begin % capacity;
        //decreaseCapacity();

        return result;

    }

    public static int size() {
        return size;
    }

    public static boolean isEmpty() {
        return size == 0;
    }

    public static void clear() {

        elements = new Object[2];
        begin = 0;
        end = 0;
        capacity = 2;
        size = 0;

    }

    public static int count(Object value) {

        int iter = begin;
        int result = 0;

        while (iter != end) {
            if (elements[iter].equals(value))
                result++;
            iter++;
            iter = iter % capacity;
        }

        return result;
    }

    private static void decreaseCapacity() {

        if (size < capacity / 4) {
            elements = makeArray(capacity / 2);
            capacity = capacity / 2;
            begin = 0;
            end = size;
        }

    }

    private static void ensureCapacity() {

        if (size + 1 >= capacity) {

            elements = makeArray(capacity * 2);
            capacity = capacity * 2;
            begin = 0;
            end = size;
        }

    }

    private static Object[] makeArray(int givenCapacity) {

        if (size == 0) {
            return new Object[capacity];
        }

        Object[] newElements = new Object[givenCapacity];

        int i = begin;
        int newArrIter = 0;

        do {
            newElements[newArrIter] = elements[i];
            i++;
            i = i % capacity;
            newArrIter++;
        } while (i != end);

        return newElements;

    }

}
