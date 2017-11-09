## Parser combinators for Java

[![Build Status](https://travis-ci.org/pashashiz/java-parser-combinators.svg?branch=master)](https://travis-ci.org/pashashiz/java-parser-combinators)

This is baseline implementation of popular in Scala and Haskell 
[parser combinators](https://en.wikipedia.org/wiki/Parser_combinator) for Java.   
It has very basic functional so far. There is an example of properties tree parser based on it, 
look at `parser-properties-tree` module.

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