package io.github.pashashiz.parser.properties;

import java.util.List;

public interface PropertyTree {

    List<String> toStringLines();

    String joinSymbol();

}
