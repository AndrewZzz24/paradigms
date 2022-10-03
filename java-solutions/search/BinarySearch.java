package search;

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
public class BinarySearch {

    // Pred: right > left && array.length >= 0 && for all i=0..array.length-1: a[i]>a[i+1]
    // Func inv : forall i = 0..n : a[i] = a[i]' && n == n' && x' == x && array != null && array.length >= 0 && for all i=0..array.length-1: a[i]>a[i+1]
    // Post: R == res && right' - left' <= 1 && res == right' &&  array.length > 0 : array[res]<=x ? 0 && Func inv
    private static int MakeRecurse(int[] array, int x, int left, int right) {

        // res > left && res <= right
        if (right - left > 1) {
            // res > left && res <= right && right - left > 1
            int mid = (right + left) / 2;
            // res > left && res <= right && right - left > 1 && mid < right && mid > left
            if (array[mid] <= x) {
                // res > left && res <= right && right - left > 1 && mid < right && mid > left && array[mid] <= x
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && res <= mid
                right = mid;
                // res > left && res <= right && right' > left && right' < right && res <= right'
                // res > left && res <= right && res <= right'
            } else {
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && array[mid] > x
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && res > mid
                left = mid;
                // res > left && res <= right && left' > left && left' < right && res > left'
                // res > left && res <= right && res > left'
            }
            // res > left' && res <= right'
            return MakeRecurse(array, x, left, right);

        } else {
            // res > left && res <= right && right - left <= 1
        }

        // res > left && res <= right && right - left <= 1
        // res == right;

        return right;
    }


    // Pred: array != null && for all i=0..array.length-1: a[i]>a[i+1]
    // Func inv : forall i = 0..n : a[i] = a[i]' && n == n' && x' == x && array != null && array.length >= 0 && for all i=0..array.length-1: a[i]>a[i+1]
    // Post: R == res && array.length > 0 : array[res]<=x ? 0 && Func inv
    public static int RecursiveBinarySearch(int[] array, int x) {
        return MakeRecurse(array, x, -1, array.length);
    }


    // Pred: array != null && for all i=0..array.length-1: a[i]>a[i+1]
    // Func inv : forall i = 0..n : a[i] = a[i]' && n == n' && x' == x
    // Post: R == res && array.length > 0 : array[res]<=x ? 0 && Func inv
    public static int IterativeBinarySearch(int[] array, int x) {

        // res > -1 && res <= array.length;
        int left = -1;

        // res > left && res <= array.length;
        int right = array.length;

        // res > left && res <= right
        while (right - left > 1) {

            // res > left && res <= right && right - left > 1
            int mid = (right + left) / 2;
            // res > left && res <= right && right - left > 1 && mid > left && mid < right

            if (array[mid] <= x) {
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && array[mid] <= x
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && res <= mid
                right = mid;
                // res > left && res <= right && right' > left && right' < right && res <= right'
                // res > left && res <= right && res <= right'
            } else {
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && array[mid] > x
                // res > left && res <= right && right - left > 1 && mid > left && mid < right && res > mid
                left = mid;
                // res > left && res <= right && left' > left && left' < right && res > left'
                // res > left && res <= right && res > left'
            }

            // res > left' && res <= right'

        }
        // res > left && res <= right && right - left <= 1
        // res == right;
        return right;

    }


    // Pred: args.length >= 1
    // && for all i=1..args.length : args[i] --> integer
    // && for all i=1..args.length-1: a[i]>a[i+1]

    // Post: res --> integer
    // && res --> STDOUT && array[res] <= x
    // && res -- min possible;
    public static void main(String[] args) {

        int x = Integer.parseInt(args[0]);

        int[] arr = new int[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            arr[i - 1] = Integer.parseInt(args[i]);
        }

        //System.out.println(IterativeBinarySearch(arr, x));
        System.out.println(RecursiveBinarySearch(arr, x));

    }

}
