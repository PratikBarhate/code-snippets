package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * This sorting technique is useful when we can make sure that the elements are nearly sorted.
 */
public class InsertionSort implements Sort {

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 23, 4, 100, 6, 8, 94, 2));
        new InsertionSort().sort(arr, Comparator.comparingInt(o -> o));
        arr.forEach(System.out::println);
    }

    public <T> void sort(ArrayList<T> in, Comparator<T> comparator) {
        Integer length = in.size();
        T key;
        int j;
        for (int i = 1; i < length; i++) {
            key = in.get(i);
            for (j = i - 1; j >= 0; j--) {
                if (comparator.compare(in.get(j), key) >= 0) {
                    in.set(j + 1, in.get(j));
                } else {
                    break;
                }
            }
            in.set(j + 1, key);
        }
    }
}

