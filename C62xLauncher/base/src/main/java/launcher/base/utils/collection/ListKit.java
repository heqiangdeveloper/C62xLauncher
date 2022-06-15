package launcher.base.utils.collection;

import java.util.Collection;
import java.util.List;

public class ListKit {
    public static <T> void swipeElement(List<T> list, int p1, int p2) {
        if (list == null) {
            throw new NullPointerException("swipeElement error, param collection is null.");
        }
        if (IndexCheck.indexOutOfArray(list, p1) || IndexCheck.indexOutOfArray(list, p2)) {
            throw new ArrayIndexOutOfBoundsException("swipeElement error, p1: " + p1 + " , p2:" + p2);
        }
        T t1 = list.get(p1);
        T t2 = list.get(p2);
        list.set(p1, t2);
        list.set(p2, t1);
    }

    public static <T> void swipeElement(List<T> list1, List<T> list2, int p1, int p2) {
        if (list1 == null) {
            throw new NullPointerException("swipeElement error, param list1 is null.");
        }
        if (list2 == null) {
            throw new NullPointerException("swipeElement error, param list2 is null.");
        }
        if (IndexCheck.indexOutOfArray(list1, p1)) {
            throw new ArrayIndexOutOfBoundsException("swipeElement error, p1: " + p1);
        }
        if (IndexCheck.indexOutOfArray(list2, p2)) {
            throw new ArrayIndexOutOfBoundsException("swipeElement error, p2: " + p2);
        }
        T t1 = list1.get(p1);
        T t2 = list2.get(p2);
        list1.set(p1, t2);
        list2.set(p2, t1);
    }
}