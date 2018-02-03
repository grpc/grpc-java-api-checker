# grpc-java-api-checker

[![Build Status](https://travis-ci.org/grpc/grpc-java-api-checker.svg?branch=master)](https://travis-ci.org/grpc/grpc-java-api-checker)

An Error Prone plugin that checks for usages of grpc-java APIs that are annotated with `@ExperimentalApi` or `@Internal` annotation.

**NOTE: Currently grpc-java-api-checker does not work. But you can use this with HEAD of grpc-java. This problem will be fixed by grpc-java `1.10.0`.**

The error examples:

```
src/main/java/com/example/App.java:10: error: [Internal] @Internal should not be used in application code
    System.out.println(InternalStatus.MESSAGE_KEY);
                       ^
    (see https://github.com/grpc/grpc-java)

src/main/java/com/example/App.java:11: error: [ExperimentalApi] @ExperimentalApi should not be used in application code
    System.out.println(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
                           ^
    (see "https://github.com/grpc/grpc-java/issues/1710")
```

## Usage

Using the grpc-java-api-checker requires configuring your project to build with the Error Prone Java compiler.
You can see the Error Prone documents [here](http://errorprone.info/).

### Examples

If you want to see grpc-java-api-checker is working with examples, first you should do the local publish.

``` sh
mvn install
```

#### Maven
The example is [here](examples/maven)

``` sh
cd examples/maven/
mvn compile
```

#### Gradle
The example is [here](examples/gradle)

``` sh
cd examples/gradle/
./gradlew compileJava
```

## Build and Test

``` sh
# Compile
mvn compile

# Test
mvn test

# Build
mvn build

# Publish to Local
mvn install
```
