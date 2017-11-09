package io.github.pashashiz.parser.properties;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PropertyTreeTest {

    @Test
    public void parse() throws Exception {
//        PropertyTree properties = PropertyParsers.parse(
//                "name=Pavlo\n" +
//                        "langs[0]=java\n" +
//                        "langs[1]=scala\n" +
//                        "langs[2]=haskell\n" +
//                        "address.country=UA\n" +
//                        "address.city=Kiyv");
//        System.out.println(properties);
    }

    @Test
    public void toStringLines() throws Exception {
        PropertyObject object = new PropertyObject()
                .add("name", new PropertyValue("Pavlo"))
                .add("langs", new PropertyList()
                        .add(new PropertyValue("java"))
                        .add(new PropertyValue("scala"))
                        .add(new PropertyValue("haskell")))
                .add("address", new PropertyObject()
                        .add("country", new PropertyValue("UA"))
                        .add("city", new PropertyValue("Kiyv")));
        assertThat("printing object should be valid", object.toString(), CoreMatchers.is(
                "name=Pavlo\n" +
                "langs[0]=java\n" +
                "langs[1]=scala\n" +
                "langs[2]=haskell\n" +
                "address.country=UA\n" +
                "address.city=Kiyv"));
    }

}