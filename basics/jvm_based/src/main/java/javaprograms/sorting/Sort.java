package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This interface is useful in defining the instances of [[java.util.Comparator]]
 * for custom classes.
 */
interface Sort {
    /**
     * Sorting method, sorts the given array in place.
     *
     * @param in         The unsorted array of objects.
     * @param comparator The comparator object to get the required ordering.
     * @param <T>        The type of the objects in the input sequence.
     */
    <T> void sort(ArrayList<T> in, Comparator<T> comparator);
}
