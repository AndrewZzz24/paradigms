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

*/
public interface Queue {

    /*
        Pred: element != null
        Post: n' == n + 1 && a'[n'] == element && immutable(n)
        enqueue(element)
    */
    void enqueue(Object obj);

    /*
        Pred: n > 0
        Post: R == a[1] && immutable(n)
        element
    */
    Object element();

    /*
        Pred: n > 0
        Post: R == a[1] && n' == n - 1 && immutable(2..n)
        dequeue
    */
    Object dequeue();

    /*
        Pred: true
        Post: immutable(1..n) && R == n && n >= 0
        size
         */
    int size();

    /*
        Pred: true
        Post: immutable(n) && R == (n == 0) && immutable(n) && n'==n
        isEmpty
    */
    boolean isEmpty();

    /*
        Pred: true
        Post: n' == 0
        clear
     */
    void clear();

    /*
        Pred: true
        Post: R == count of x in queue && immutable(n)
        count(x)
    */
    int count(Object value);

    /*
        Pred: true
        Post: R == count of (forall 1..n: predicate(a[x]) == true) && immutable(n);
        countIf
     */
    int countIf(Predicate<Object> predicate);
}
