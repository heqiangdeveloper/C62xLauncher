package launcher.base.utils.collection;

import java.util.Collection;
import java.util.Map;

public class ClearUtil {
    public static <T>  void clear(Collection<T> collection){
        if (collection != null) {
            collection.clear();
        }
    }
    public static  void clear(Map<?,?> map){
        if (map != null) {
            map.clear();
        }
    }
}
