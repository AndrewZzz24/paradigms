package queue;

import java.util.Objects;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
/*
  Model:
    a[1]..a[n]
    n -- size of queue

    Invariant: queue in arg != null && n >= 0 && forall i=1...n: a[i] != null

    Let immutable(n): forall i=1..n: a[i] == a[i]'

    Pred: element != null
    Post: n' == n + 1 && a'[n'] == element && immutable(n)
    enqueue(queue, element)

    Pred: n > 0
    Post: R == a[1] && immutable(n)
    element

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
    Post: n' == 0 && immutable(n)
    clear

    Pred: true
    Post: R == count of x in queue && immutable(n)
    count(x)
 */

public class ArrayQueueADT {

    private int size;
    private Object[] elements = new Object[2];

    private int begin;
    private int end;
    private int capacity = 2;


    public static ArrayQueueADT create() {

        var queue = new ArrayQueueADT();
        queue.elements = new Object[2];
        queue.capacity = 2;
        return queue;

    }

    public static void enqueue(ArrayQueueADT queue, Object obj) {

        Objects.requireNonNull(obj);
        ensureCapacity(queue);

        queue.elements[queue.end++] = obj;
        queue.end = queue.end % queue.capacity;
        queue.size++;

    }


    public static Object element(ArrayQueueADT queue) {

        assert queue.size > 0;
        return queue.elements[queue.begin];

    }

    public static Object dequeue(ArrayQueueADT queue) {

        assert queue.size > 0;
        queue.size--;
        var result = queue.elements[queue.begin];
        queue.elements[queue.begin] = null;
        queue.begin++;
        queue.begin = queue.begin % queue.capacity;
        //decreaseCapacity(queue);
        return result;

    }

    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    public static void clear(ArrayQueueADT queue) {

        queue.elements = new Object[2];
        queue.begin = 0;
        queue.end = 0;
        queue.capacity = 2;
        queue.size = 0;

    }

    public static int count(ArrayQueueADT queue, Object value) {

        int iter = queue.begin;
        int result = 0;

        while (iter != queue.end) {
            if (queue.elements[iter].equals(value))
                result++;
            iter++;
            iter = iter % queue.capacity;
        }

        return result;
    }

    private static void decreaseCapacity(ArrayQueueADT queue) {

        if (queue.size < queue.capacity / 4) {
            queue.elements = MakeArray(queue, queue.capacity / 2);
            queue.capacity = queue.capacity / 2;
            queue.begin = 0;
            queue.end = queue.size;
        }

    }

    private static void ensureCapacity(ArrayQueueADT queue) {

        if (queue.size + 1 >= queue.capacity) {
            queue.elements = MakeArray(queue, queue.capacity * 2);
            queue.capacity = queue.capacity * 2;
            queue.begin = 0;
            queue.end = queue.size;
        }
    }

    private static Object[] MakeArray(ArrayQueueADT queue, int given_capacity) {

        if (queue.size == 0) {
            return new Object[queue.capacity];
        }

        Object[] newElements = new Object[given_capacity];

        int i = queue.begin;
        int newArrIter = 0;

        do {
            newElements[newArrIter] = queue.elements[i];
            i++;
            i = i % queue.capacity;
            newArrIter++;
        } while (i != queue.end);

        return newElements;

    }

}
