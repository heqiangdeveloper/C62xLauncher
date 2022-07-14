package launcher.base.utils.collection;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;

public class ListKit {
    private static final String TAG = "ListKit";

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
    public static <T> void swipeElement(List<T> list, T t1, T t2) {
        if (list == null) {
            throw new NullPointerException("swipeElement error, param collection is null.");
        }
        if (t1 == null) {
            throw new NullPointerException("swipeElement error, param t1 is null.");
        }
        if (t2 == null) {
            throw new NullPointerException("swipeElement error, param t2 is null.");
        }
        if (t1 == t2) {
            Log.w(TAG, "Nothing to do because t1 == t2.");
            return;
        }
        int p1 = list.indexOf(t1);
        int p2 = list.indexOf(t2);
        if (IndexCheck.indexOutOfArray(list, p1) || IndexCheck.indexOutOfArray(list, p2)) {
            throw new ArrayIndexOutOfBoundsException("swipeElement error, p1: " + p1 + " , p2:" + p2);
        }
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

    /**
     * 比较两个列表是否相等.
     * @param list1 list1
     * @param list2 list2
     * @param <T> t
     * @return true: 满足下列条件: 1. 均非空 2. 长度相等 3. 每个列表位置的元素满足==关系
     */
    public static <T> boolean equal(@NonNull List<T> list1, @NonNull List<T> list2) {
        int size = list1.size();
        if (size != list2.size()) {
            return false;
        }
        if (size == 0) {
            return true;
        }
        for (int i = 0; i < size; i++) {
            T t1 = list1.get(i);
            T t2 = list2.get(i);
            if (t1 != t2) {
                return false;
            }
        }
        return true;
    }

    public static <T> T findNext(T item, List<T> list) {
        if (list == null) {
            throw new NullPointerException("findNext error, param list is null.");
        }
        if (item == null) {
            throw new NullPointerException("findNext error, param item is null.");
        }
        boolean foundNext = false;
        for (T t : list) {
            if (!foundNext) {
                if (t == item) {
                    foundNext = true;
                }
            } else {
                return t;
            }
        }
        return null;
    }

    public static <T> T findPrev(T item, List<T> list) {
        if (list == null) {
            throw new NullPointerException("findPrev error, param list is null.");
        }
        if (item == null) {
            throw new NullPointerException("findPrev error, param item is null.");
        }
        T prev = null;
        for (T t : list) {
            if (t == item) {
                return prev;
            }
            prev = t;
        }
        return null;
    }
}
