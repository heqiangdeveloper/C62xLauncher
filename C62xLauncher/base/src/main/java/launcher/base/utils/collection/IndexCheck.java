package launcher.base.utils.collection;

import java.util.Collection;

public class IndexCheck {
    public static boolean indexOutOfArray(Collection collection, int index) {
        if (collection == null) {
            throw new NullPointerException("IndexCheck error, param collection is null.");
        }
        int size = collection.size();
        return index < 0 || index >= size;
    }
}
