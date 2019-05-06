package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This interface is useful in defining the instances of [[java.util.Comparator]]
 * for custom classes.
 */
abstract class Sort {
    /**
     * Sorting method, sorts the given array in place.
     *
     * @param in         The unsorted array of objects.
     * @param comparator The comparator object to get the required ordering.
     * @param <T>        The type of the objects in the input sequence.
     */
    abstract <T> void sort(ArrayList<T> in, Comparator<T> comparator);

    /**
     * Swaps the the elements of the array at the given two indices.
     *
     * @param arr Array whose elements are under observation.
     * @param i   first index to be swapped.
     * @param j   second index to be swapped with the first one.
     * @param <T> The type of the objects within the given array.
     */
    <T> void swapArrayElements(ArrayList<T> arr, int i, int j) {
        T temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
}
