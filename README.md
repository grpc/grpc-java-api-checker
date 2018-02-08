# grpc-java-api-checker

[![Build Status](https://travis-ci.org/grpc/grpc-java-api-checker.svg?branch=master)](https://travis-ci.org/grpc/grpc-java-api-checker)

An Error Prone plugin that checks for usages of grpc-java APIs that are annotated with `@ExperimentalApi` or `@Internal`.

**NOTE: grpc-java-api-checker works with grpc-java version 1.10.0 or greater.**

The error examples:

```
src/main/java/com/example/App.java:10: error: [GrpcInternal] @Internal should not be used in application code
    System.out.println(InternalStatus.MESSAGE_KEY);
                       ^
    (see https://github.com/grpc/grpc-java)

src/main/java/com/example/App.java:11: error: [GrpcExperimentalApi] @ExperimentalApi should not be used in application code
    System.out.println(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
                           ^
    (see "https://github.com/grpc/grpc-java/issues/1710")
```

## Usage

Using the grpc-java-api-checker requires configuring your project to build with the Error Prone Java compiler.
You can see the Error Prone documents [here](http://errorprone.info/).

### Examples

To use grpc-java-api-checker, you must first publish it locally. (TODO: update with link to jars on Maven once they are published)

``` sh
mvn install
```

#### Maven
The example is [here](examples/pom.xml)

``` sh
cd examples/
mvn compile
```

#### Gradle
The example is [here](examples/build.gradle)

``` sh
cd examples/
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
