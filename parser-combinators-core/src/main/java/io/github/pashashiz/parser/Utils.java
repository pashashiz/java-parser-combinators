package io.github.pashashiz.parser;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <A> List<A> addHeadToList(A head, List<A> list) {
        List<A> merged = new ArrayList<>();
        merged.add(head);
        merged.addAll(list);
        return merged;
    }

}
