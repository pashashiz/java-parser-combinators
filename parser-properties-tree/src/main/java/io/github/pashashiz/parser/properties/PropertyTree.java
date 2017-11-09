package io.github.pashashiz.parser.properties;

import java.util.List;

public interface PropertyTree {

    PropertyTree merge(PropertyTree other);

    List<String> toStringLines();

    String joinSymbol();

}
