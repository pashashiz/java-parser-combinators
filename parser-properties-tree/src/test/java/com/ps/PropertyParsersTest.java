package com.ps;

import org.junit.Test;

import static com.ps.PropertyParsers.*;
import static com.ps.ParserMatchers.success;
import static org.junit.Assert.assertThat;

public class PropertyParsersTest {

    @Test
    public void object_WhenParseKeyValue() throws Exception {
        assertThat(
                object().run("key[=value\n"),
                success(new PropertyObject().add("key", new PropertyValue("value"))));
    }

    @Test
    public void object_WhenParse2LevelObject() throws Exception {
        assertThat(
                object().run("ns.key=value\n"),
                success(new PropertyObject()
                        .add("ns", new PropertyObject()
                                .add("key", new PropertyValue("value")))));
    }

    @Test
    public void object_WhenParseNLevelObject() throws Exception {
        assertThat(
                object().run("ns1.ns2.key=value\n"),
                success(new PropertyObject()
                        .add("ns1", new PropertyObject()
                                .add("ns2", new PropertyObject()
                                        .add("key", new PropertyValue("value"))))));
    }

    @Test
    public void list_WhenParseIndexValue() throws Exception {
        assertThat(
                list().run("[1]=value\n"),
                success(new PropertyList().add(1, new PropertyValue("value"))));
    }

    @Test
    public void list_WhenParse2LevelList() throws Exception {
        assertThat(
                list().run("[1].key=value\n"),
                success(new PropertyList().add(1, new PropertyObject()
                        .add("key", new PropertyValue("value")))));
    }

    @Test
    public void tree_WhenSuccess() throws Exception {
        assertThat(
                tree().run("ns[1].key=value\n"),
                success(new PropertyObject()
                        .add("ns", new PropertyList().add(1, new PropertyObject()
                            .add("key", new PropertyValue("value"))))));
    }

}