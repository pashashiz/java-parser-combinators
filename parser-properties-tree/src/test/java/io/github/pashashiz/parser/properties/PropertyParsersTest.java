package io.github.pashashiz.parser.properties;

import org.junit.Test;

import static org.junit.Assert.assertThat;

public class PropertyParsersTest {

    @Test
    public void object_WhenParseKeyValue() {
        assertThat(
                PropertyParsers.object().parse("key=value\n"),
                ParserMatchers.success(new PropertyObject().add("key", new PropertyValue("value"))));
    }

    @Test
    public void object_WhenParseKeyValueWithWhitespaces() {
        assertThat(
                PropertyParsers.object().parse("key = value\n"),
                ParserMatchers.success(new PropertyObject().add("key", new PropertyValue("value"))));
    }

    @Test
    public void object_WhenParse2LevelObject() {
        assertThat(
                PropertyParsers.object().parse("ns.key=value\n"),
                ParserMatchers.success(new PropertyObject()
                        .add("ns", new PropertyObject()
                                .add("key", new PropertyValue("value")))));
    }

    @Test
    public void object_WhenParseNLevelObject() {
        assertThat(
                PropertyParsers.object().parse("ns1.ns2.key=value\n"),
                ParserMatchers.success(new PropertyObject()
                        .add("ns1", new PropertyObject()
                                .add("ns2", new PropertyObject()
                                        .add("key", new PropertyValue("value"))))));
    }

    @Test
    public void list_WhenParseIndexValue() {
        assertThat(
                PropertyParsers.list().parse("[1]=value\n"),
                ParserMatchers.success(new PropertyList().add(1, new PropertyValue("value"))));
    }

    @Test
    public void list_WhenParse2LevelList() {
        assertThat(
                PropertyParsers.list().parse("[1].key=value\n"),
                ParserMatchers.success(new PropertyList().add(1, new PropertyObject()
                        .add("key", new PropertyValue("value")))));
    }

    @Test
    public void tree_WhenSuccess() {
        assertThat(
                PropertyParsers.tree().parse("ns[1].key=value\n"),
                ParserMatchers.success(new PropertyObject()
                        .add("ns", new PropertyList().add(1, new PropertyObject()
                            .add("key", new PropertyValue("value"))))));
    }

    @Test
    public void tree_WhenMultiline() {
        assertThat(
                PropertyTree.parse(
                        "ns[1].key1=value1\n" +
                        "ns[2].key2=value2\n"),
                ParserMatchers.success(new PropertyObject()
                        .add("ns", new PropertyList()
                                .add(1, new PropertyObject()
                                        .add("key1", new PropertyValue("value1")))
                                .add(2, new PropertyObject()
                                        .add("key2", new PropertyValue("value2"))))));
    }

}