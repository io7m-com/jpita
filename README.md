jpita
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jpita/com.io7m.jpita.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jpita%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.jpita/com.io7m.jpita?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jpita/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jpita.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jpita)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.jpita](./src/site/resources/jpita.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpita/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jpita/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpita/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jpita/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpita/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jpita/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jpita/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jpita/actions?query=workflow%3Amain.windows.temurin.lts)|

## jpita

Java functions to align and justify plain text.

## Features

* Full justification (flush left, flush right).
* Left alignment (flush left, ragged right).
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Usage

Add a series of words to a `JPAlignerType` instance:

```
var a = JPAlignerBasic.create(80);

a.addWord("Hello");
a.addWord("World");
var s = a.finish();
```

```
var a = JPJustifierBasic.create(OVERFLOW_ANYWAY, JUSTIFY_UNDER_HALF, 80);

a.addWord("Hello");
a.breakLine();
a.addWord("World");
var s = a.finish();
```

The result of `finish()` is a list of justified and/or aligned lines of text.

