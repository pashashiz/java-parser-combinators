package io.github.pashashiz.parser.json;

import io.github.pashashiz.parser.Pair;
import io.github.pashashiz.parser.Result;
import org.junit.Test;

import static io.github.pashashiz.parser.json.JsonParsers.*;
import static io.github.pashashiz.parser.json.PMatchers.failure;
import static io.github.pashashiz.parser.json.PMatchers.success;
import static org.junit.Assert.*;

public class JsonTest {

    @Test
    public void jLiteral_WhenNull() {
        assertThat(jLiteral().parse("null"), success(new JNull()));
    }

    @Test
    public void jLiteral_WhenNumber() {
        assertThat(jLiteral().parse("1.5"), success(new JNumber(1.5)));
    }

    @Test
    public void jLiteral_WhenBoolean() {
        assertThat(jLiteral().parse("true"), success(new JBoolean(true)));
    }

    @Test
    public void jLiteral_WhenString() {
        assertThat(jLiteral().parse("\"value\""), success(new JString("value")));
    }

    @Test
    public void jLiteral_WhenFailure() {
        assertThat(jLiteral().parse("<tag/>"), failure());
    }

    @Test
    public void jArray_WhenSuccess() {
        assertThat(
                jArray().parse("[1,true,\"data\",null]"),
                success(new JArray(new JNumber(1), new JBoolean(true), new JString("data"), new JNull())));
    }

    @Test
    public void jArray_WhenWithSpacesSuccess() {
        assertThat(
                jArray().parse("[1, true, \"data\", null]"),
                success(new JArray(new JNumber(1), new JBoolean(true), new JString("data"), new JNull())));
    }

    @Test
    public void jPair_WhenSuccess() {
        assertThat(
                jPair().parse("\"key\": \"value\""),
                success(new Pair<>("key", new JString("value"))));
    }

    @Test
    public void jPair_WhenFailure() {
        assertThat(
                jPair().parse("\"key\": x"),
                failure());
    }

    @Test
    public void jObject_WhenSuccess() {
        assertThat(
                jObject().parse("{\"key\": 1}"),
                success(new JObject("key", new JNumber(1))));
    }

    @Test
    public void jObject_WhenFailure() {
        assertThat(
                jObject().parse("[1,true,\"data\",null]"),
                failure());
    }

    @Test
    public void json_WhenSuccess() {
        Result<Json> result = Json.parse("{\n" +
                "    \"glossary\": {\n" +
                "        \"title\": \"example glossary\",\n" +
                "\t\t\"GlossDiv\": {\n" +
                "            \"title\": \"S\",\n" +
                "\t\t\t\"GlossList\": {\n" +
                "                \"GlossEntry\": {\n" +
                "                    \"ID\": \"SGML\",\n" +
                "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
                "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
                "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
                "\t\t\t\t\t\"GlossDef\": {\n" +
                "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
                "                    },\n" +
                "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n");
        assertThat(result, success());
        System.out.println(result);
    }
}