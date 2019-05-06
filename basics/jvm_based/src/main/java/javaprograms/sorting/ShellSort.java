package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This sorting technique is useful when we can make sure that the elements are in reverse order.
 *
 * If the elements are far from their position in random order,
 * then its better to use merge or quick sort.
 *
 * NOTE:
 * Alternative way to deal with the reverse order of elements is
 * inverting the list of elements {O(n/2)} and apply insertion sort {O(n)}.
 */
public class ShellSort extends Sort {

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 23, 4, 100, 6, 8, 94, 2));
        System.out.println("Original order of elements: " + arr.toString());
        new ShellSort().sort(arr, Comparator.comparingInt(o -> o));
        System.out.println("Sorted order of elements: " + arr.toString());
    }

    public <T> void sort(ArrayList<T> in, Comparator<T> comparator) {
        int length = in.size();
        // NOTE: value of the first divisor needs to be adjusted efficiently.
        int gap = (int) Math.ceil((double) length / 2.0);
        T key;
        int j;
        while (gap >= 1) {
            for (int i = gap; i < length; i = i + gap) {
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
