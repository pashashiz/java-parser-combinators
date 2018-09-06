package io.github.pashashiz.parser.properties;

import io.github.pashashiz.parser.Result;

import java.util.List;

public interface PropertyTree {

    static Result<PropertyTree> parse(String value) {
        return PropertyParsers.tree().parse(value);
    }

    PropertyTree merge(PropertyTree other);

    List<String> toStringLines();

    String joinSymbol();

}
