package io.github.pashashiz.parser.properties;

import io.github.pashashiz.parser.Parser;

import java.util.List;

import static io.github.pashashiz.parser.Parser.*;

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
        return many1(or(object().cast(PropertyTree.class), list().cast(PropertyTree.class).label("tree")))
                .flatMap(PropertyParsers::reducePropertyTree);
    }

    public static Parser<PropertyObject> object() {
        return or(or(keyEqualsValue(), fieldAndObject()), fieldAndList()).label("object");
    }

    private static Parser<PropertyTree> reducePropertyTree(List<PropertyTree> all) {
        return all.stream().reduce(PropertyTree::merge)
                .map(Parser::success).orElse(failure("al least one line is required"));
    }

    private static Parser<PropertyObject> fieldAndObject() {
        return map2(
                skipRight(word(), string(".")),
                PropertyParsers::object,
                (field, object) -> new PropertyObject().add(field, object)).label("nested object");
    }

    private static Parser<PropertyObject> fieldAndList() {
        return map2(
                word(),
                PropertyParsers::list,
                (field, object) -> new PropertyObject().add(field, object)).label("list in an object");
    }

    private static Parser<PropertyObject> keyEqualsValue() {
        return map2(
                skipRight(token(word()), token(string("="))),
                allUntilEol(),
                (key, value) -> new PropertyObject().add(key, new PropertyValue(value))).label("key=value");
    }

    public static Parser<PropertyList> list() {
        return or(indexEqualsValue(), deepList()).label("list");
    }

    private static Parser<PropertyList> deepList() {
        return map2(
                skipRight(index(), string(".")).map(Integer::parseInt),
                PropertyParsers::object,
                (index, value) -> new PropertyList().add(index, value)).label("object in a list");
    }

    private static Parser<PropertyList> indexEqualsValue() {
        return map2(
                skipRight(token(index()), token(string("="))).map(Integer::parseInt),
                allUntilEol(),
                (index, value) -> new PropertyList().add(index, new PropertyValue(value))).label("[index]=value");
    }

    private static Parser<String> index() {
        return surround(string("["), natural(), string("]")).label("[index]");
    }
}
