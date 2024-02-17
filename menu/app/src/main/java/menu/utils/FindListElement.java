package menu.utils;

import java.util.List;
import java.util.function.Predicate;

public class FindListElement {

    public static <E> E getElement(List<E> list, Predicate<E> predicate) {
        for (E item : list) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null;
    }
}