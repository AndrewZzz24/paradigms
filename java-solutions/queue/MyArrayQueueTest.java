package queue;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
public class MyArrayQueueTest {

    public static void main(String[] args) {

        testModule();
        testADT();
        testClass();
        testLinkedQueue();

    }

    private static void testLinkedQueue() {
        LinkedQueue linkedQueue = new LinkedQueue();
        for (int i = 0; i < 20; i++) {
            linkedQueue.enqueue(i);
        }
        assert linkedQueue.size() == 20;
        for (int i = 0; i < 10; i++) {
            linkedQueue.dequeue();
        }
        assert linkedQueue.size() == 10;
        for (int i = 20; i < 30; i++) {
            linkedQueue.enqueue(i);
        }
        assert linkedQueue.size() == 20;
        linkedQueue.clear();
        assert linkedQueue.isEmpty();
        for (int i = 20; i < 30; i++) {
            linkedQueue.enqueue(i);
        }
        assert linkedQueue.size() == 10;
    }

    private static void testClass() {

        ArrayQueue queue1 = new ArrayQueue();
        assert queue1.isEmpty();

        for (int i = 0; i < 10; i++) {
            queue1.enqueue("Class_" + i);
        }

        assert !queue1.isEmpty();
        assert queue1.size() == 10;
        assert queue1.element().equals("Class_0");

        while (!queue1.isEmpty()) {
            System.out.println(queue1.dequeue());
        }

        assert queue1.isEmpty();
        queue1.enqueue("qweqwe");
        queue1.enqueue("qweqwe");
        queue1.enqueue("qweqwe");
        assert queue1.count("qweqwe") == 3;
        queue1.clear();
        assert queue1.count("qweqwe") == 0;

    }

    private static void testADT() {

        ArrayQueueADT queue1 = ArrayQueueADT.create();
        ArrayQueueADT queue2 = ArrayQueueADT.create();

        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(queue1, "ADT_" + i);
        }

        assert !ArrayQueueADT.isEmpty(queue1);
        assert ArrayQueueADT.isEmpty(queue2);

        assert ArrayQueueADT.size(queue1) == 10;
        assert ArrayQueueADT.element(queue1).equals("ADT_0");
        assert ArrayQueueADT.count(queue1, "ADT_2") == 1;
        assert ArrayQueueADT.count(queue1, "ADT_10") == 0;

        while (!ArrayQueueADT.isEmpty(queue1)) {
            System.out.println(ArrayQueueADT.dequeue(queue1));
        }

        assert ArrayQueueADT.isEmpty(queue1);
        ArrayQueueADT.enqueue(queue1, "qwe");
        ArrayQueueADT.enqueue(queue1, "qwe");
        ArrayQueueADT.enqueue(queue1, "qwe");
        assert ArrayQueueADT.count(queue1, "qwe") == 3;
        ArrayQueueADT.clear(queue1);
        assert ArrayQueueADT.count(queue1, "qwe") == 0;

    }

    private static void testModule() {

        assert ArrayQueueModule.isEmpty();

        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue("module_" + i);
        }

        assert ArrayQueueModule.size() == 10;
        assert ArrayQueueModule.element().equals("module_0");

        ArrayQueueModule.clear();
        assert ArrayQueueModule.size() == 0;

        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue("module_" + i);
        }

        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.dequeue());
        }

        var ob = "qweqweqwe";

        for (int i = 0; i < 5; i++)
            ArrayQueueModule.enqueue(ob);

        assert ArrayQueueModule.count(ob) == 5;
        ArrayQueueModule.clear();
        assert ArrayQueueModule.count(ob) == 0;
    }

}
