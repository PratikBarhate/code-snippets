package javaprograms.sorting;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This interface is useful in defining the instances of [[java.util.Comparator]]
 * for custom classes.
 */
interface Sort {
    Comparator<Double> doubleComparator = (o1, o2) -> (int) ((o1 - o2) * -1);

    <T> void sort(ArrayList<T> in, Comparator<T> comp);
}
