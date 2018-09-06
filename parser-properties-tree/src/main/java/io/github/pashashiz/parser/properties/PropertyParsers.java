package io.github.pashashiz.parser.properties;

import io.github.pashashiz.parser.Parser;
import io.github.pashashiz.parser.Parsers;

import java.util.List;

import static io.github.pashashiz.parser.Parsers.*;

/**
 *  Grammar:
 *  <pre>
 *  PropertyTree = PropertyObject | PropertyList
 *  PropertyObject = field.PropertyObject | fieldPropertyList | key=value
 *  PropertyList = [n].PropertyObject | [n]=value
 *  </pre>
 *
 *  Examples:
 *  <pre>
 *  key=value
 *  ns.key=value
 *  ns1.ns2.key=value
 *  [1]=value
 *  [1].key=value
 *  ns[1].key=value
 *  </pre>
 */
public class PropertyParsers {

    public static Parser<PropertyTree> tree() {
        return atLeastOne(or(object(), list().label("tree")))
                .flatMap(PropertyParsers::reducePropertyTree);
    }

    public static Parser<PropertyObject> object() {
        return or(or(keyEqualsValue(), fieldAndObject()), fieldAndList()).label("object");
    }

    private static Parser<PropertyTree> reducePropertyTree(List<PropertyTree> all) {
        return all.stream().reduce(PropertyTree::merge)
                .map(Parsers::success).orElse(failure("al least one line is required"));
    }

    private static Parser<PropertyObject> fieldAndObject() {
        return and(
                skipRight(word(), string(".")),
                deferred(PropertyParsers::object),
                (field, object) -> new PropertyObject().add(field, object)).label("nested object");
    }

    private static Parser<PropertyObject> fieldAndList() {
        return and(
                word(),
                deferred(PropertyParsers::list),
                (field, object) -> new PropertyObject().add(field, object)).label("list in an object");
    }

    private static Parser<PropertyObject> keyEqualsValue() {
        return and(
                skipRight(token(word()), token(string("="))),
                allUntilEol(),
                (key, value) -> new PropertyObject().add(key, new PropertyValue(value))).label("key=value");
    }

    public static Parser<PropertyList> list() {
        return or(indexEqualsValue(), deepList()).label("list");
    }

    private static Parser<PropertyList> deepList() {
        return and(
                skipRight(index(), string(".")),
                deferred(PropertyParsers::object),
                (index, value) -> new PropertyList().add(index, value)).label("object in a list");
    }

    private static Parser<PropertyList> indexEqualsValue() {
        return and(
                skipRight(token(index()), token(string("="))),
                allUntilEol(),
                (index, value) -> new PropertyList().add(index, new PropertyValue(value))).label("[index]=value");
    }

    private static Parser<Integer> index() {
        return surround(string("["), natural(), string("]")).label("[index]");
    }
}
