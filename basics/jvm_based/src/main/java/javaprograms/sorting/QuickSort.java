package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * One of the most efficient sorting algorithm with average and best
 * time complexity of O(n * log n) and wort complexity of O(square(n))
 *
 * Understanding analysis of Quick Sort:-
 * [https://www.khanacademy.org/computing/computer-science/algorithms/quick-sort/a/analysis-of-quicksort]
 */
public class QuickSort extends Sort {
    private Random randGen = new Random();

    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>(Arrays.asList(1, 23, 4, 100, 6, 8, 94, 2));
        System.out.println("Original order of elements: " + arr.toString());
        new QuickSort().sort(arr, Comparator.comparingInt(o -> o));
        System.out.println("Sorted order of elements: " + arr.toString());
    }

    private <T> int partition(ArrayList<T> arr, int left, int right, Comparator<T> comparator) {
        int separatingIndex;
        int pivotIndex = randGen.nextInt(right - left) + left;
        int i = left + 1, j = right;
        T pivotValue = arr.get(pivotIndex);
        swapArrayElements(arr, pivotIndex, left);
        while (i < j) {
            while (comparator.compare(arr.get(i), pivotValue) < 0 && i < right)
                i++;
            while (comparator.compare(arr.get(j), pivotValue) > 0 && j > left + 1)
                j--;
            if (i < j)
                swapArrayElements(arr, i, j);
        }
        // For sub-arrays which are already in order, there is no need to swap.
        if (comparator.compare(pivotValue, arr.get(j)) > 0) {
            swapArrayElements(arr, left, j);
            separatingIndex = j;
        } else {
            separatingIndex = left;
        }
        return separatingIndex;
    }

    private <T> void divide(ArrayList<T> arr, int left, int right, Comparator<T> comparator) {
        if (left < right) {
            int separator = partition(arr, left, right, comparator);
            divide(arr, left, separator - 1, comparator);
            divide(arr, separator + 1, right, comparator);
        }
    }

    public <T> void sort(ArrayList<T> in, Comparator<T> comparator) {
        Integer length = in.size();
        divide(in, 0, length - 1, comparator);
    }
}
