package io.github.pashashiz.parser.json;

import io.github.pashashiz.parser.Pair;
import io.github.pashashiz.parser.Parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.pashashiz.parser.Parsers.*;

public class JsonParsers {

    public static Parser<JNull> jNull() {
        return token(null_()).map(v -> new JNull()).label("JSON null");
    }

    public static Parser<JNumber> jNumber() {
        return token(double_()).map(JNumber::new).label("JSON number");
    }

    public static Parser<JString> jString() {
        return token(quoted()).map(JString::new).label("JSON string");
    }

    public static Parser<JBoolean> jBoolean() {
        return token(boolean_()).map(JBoolean::new).label("JSON boolean");
    }

    public static Parser<Json> jLiteral() {
        return or(jNull(), jNumber(), jBoolean(), jString()).label("JSON literal");
    }

    public static Parser<Json> jValue() {
        return or(jLiteral(), deferred(JsonParsers::jArray), deferred(JsonParsers::jObject)).label("JSON value");
    }

    public static Parser<JArray> jArray() {
        return token(surround(
                        string("["),
                        separated(jValue(), string(",")),
                        string("]")))
                .map(JArray::new)
                .label("JSON array");
    }

    public static Parser<Pair<String, Json>> jPair() {
        return token(
                skipRight(jString(), string(":")).and(jValue(),
                        (key, value) -> new Pair<>(key.getValue(), value)))
                .label("Json object key value pair");
    }

    public static Parser<JObject> jObject() {
        return token(surround(
                        string("{"),
                        separated(jPair(), string(",")),
                        string("}")))
                .map(pairs -> new JObject(toMap(pairs)))
                .label("JSON object");
    }

    public static Parser<Json> json() {
        return skipRight(or(jArray(), jObject()), end());
    }

    public static Map<String, Json> toMap(List<Pair<String, Json>> list) {
        return list
                .stream()
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
