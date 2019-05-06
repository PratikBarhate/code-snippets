package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 1. This sorting technique has a average time complexity of O(n * log n).
 * 2. Stable sort.
 *
 * One disadvantage of this sorting technique is that
 * it creates two array objects for each merge operation.
 * Line number 28 ad 29.
 */
public class MergeSort extends Sort {

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 23, 4, 100, 6, 8, 94, 2));
        System.out.println("Original order of elements: " + arr.toString());
        // Comparator for the sorting to be in descending order.
        new MergeSort().sort(arr, Comparator.comparingInt(o -> -o));
        System.out.println("Sorted order of elements: " + arr.toString());
    }

    private <T> void merge(ArrayList<T> arr, int left, int mid, int right, Comparator<T> comparator) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        // creating two temporary arrays
        ArrayList<T> l_arr = new ArrayList<>();
        ArrayList<T> r_arr = new ArrayList<>();
        for (int i = 0; i < n1; ++i)
            l_arr.add(i, arr.get(left + i));
        for (int j = 0; j < n2; ++j)
            r_arr.add(j, arr.get(mid + 1 + j));
        // merge the two sub arrays in order.
        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (comparator.compare(l_arr.get(i), r_arr.get(j)) < 0) {
                arr.set(k, l_arr.get(i));
                i++;
            } else {
                arr.set(k, r_arr.get(j));
                j++;
            }
            k++;
        }
        // copy the left over
        while (i < n1) {
            arr.set(k, l_arr.get(i));
            i++;
            k++;
        }
        while (j < n2) {
            arr.set(k, r_arr.get(j));
            j++;
            k++;
        }
    }

    private <T> void divide(ArrayList<T> arr, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int mid = (left + right) / 2;
            divide(arr, left, mid, comparator);
            divide(arr, mid + 1, right, comparator);
            merge(arr, left, mid, right, comparator);
        }
    }

    public <T> void sort(ArrayList<T> in, Comparator<T> comparator) {
        int length = in.size();
        divide(in, 0, length - 1, comparator);
    }
}
