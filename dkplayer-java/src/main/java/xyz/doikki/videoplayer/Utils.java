package xyz.doikki.videoplayer;

import java.util.Collection;

public class Utils {

    /**
     * 判断集合是否为null或者空
     *
     * @param collection
     * @param <E>
     * @return
     */
    public static <E> boolean isNullOrEmpty(Collection<E> collection) {
        return collection == null || collection.size() == 0;
    }
}
