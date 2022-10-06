package no.nav.pensjon.selvbetjeningopptjening.util;

import java.util.ArrayList;
import java.util.List;

public final class ListUtil {

    public static <T> List<T> listOf(List<T> list, T item) {
        var aggregate = new ArrayList<>(list);
        aggregate.add(item);
        return aggregate;
    }

    private ListUtil() {
    }
}
