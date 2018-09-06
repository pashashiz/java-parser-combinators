# Parser combinators for Java

[![Build Status](https://travis-ci.org/pashashiz/java-parser-combinators.svg?branch=master)](https://travis-ci.org/pashashiz/java-parser-combinators)

This is baseline implementation of popular in Scala and Haskell 
[parser combinators](https://en.wikipedia.org/wiki/Parser_combinator) for Java.   
It has very basic functional so far. 

Gradle dependency:
```groovy
compile group: 'io.github.pashashiz', name: 'parser-combinators-core', version: '1.0.0'
```

Maven dependency:
```xml
<dependency>
    <groupId>io.github.pashashiz</groupId>
    <artifactId>parser-combinators-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Examples:

### JSON parser

Implemented in `parser-json` module. 

To parse a JSON just call:

```java
Result<Json> result = Json.parse(
        "{\"name\": \"John\", \"age\": 30, \"car\": null}");
```


### Properties tree parser (the way Spring parses the properties and binds to the objects)

Implemented in `parser-properties-tree` module.

To parse properties just call:

```java
Result<PropertyTree> properties = PropertyTree.parse(
       "ns[1].key1=value1\n" +
       "ns[2].key2=value2\n");
```
