package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This sorting technique is useful when we can make sure that the elements are in reverse order.
 *
 * NOTE:
 * Alternative way to deal with the reverse order of element is
 * inverting the list of elements {O(n/2)} and apply insertion sort.
 */
public class ShellSort implements Sort {

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 23, 4, 100, 6, 8, 94, 2));
        new ShellSort().sort(arr, Comparator.comparingInt(o -> o));
        arr.forEach(System.out::println);
    }

    public <T> void sort(ArrayList<T> in, Comparator<T> comparator) {
        int length = in.size();
<<<<<<< HEAD
        // NOTE: value of the first divisor needs to be adjusted efficiently.
=======
>>>>>>> 6d038ec815f3f8a3c9ad82fabd4c72c3d1880cf4
        int gap = (int) Math.ceil((double) length / 2.0);
        T key;
        int j;
        while (gap >= 1) {
            for (int i = gap; i < length; i++) {
                key = in.get(i);
                for (j = i - gap; j >= 0; j = j - gap) {
                    if (comparator.compare(in.get(j), key) >= 0) {
                        in.set(j + gap, in.get(j));
                    } else {
                        break;
                    }
                }
                in.set(j + gap, key);
            }
            gap = gap / 2;
        }
    }
}
