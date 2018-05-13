/*
 * Copyright 2018 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.annotations.checkers;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExperimentalApiCheckerTest {

  private CompilationTestHelper compiler;

  @Before
  public void setUp() {
    compiler = CompilationTestHelper.newInstance(ExperimentalApiChecker.class, getClass());

    // add the @ExperimentalApi annotation
    compiler.addSourceLines("io/grpc/ExperimentalApi.java",
        "package io.grpc;",
        "",
        "import java.lang.annotation.Documented;",
        "import java.lang.annotation.ElementType;",
        "import java.lang.annotation.Retention;",
        "import java.lang.annotation.RetentionPolicy;",
        "import java.lang.annotation.Target;",
        "",
        "@Retention(RetentionPolicy.CLASS)",
        "@Target({",
        "   ElementType.ANNOTATION_TYPE,",
        "   ElementType.CONSTRUCTOR,",
        "   ElementType.FIELD,",
        "   ElementType.METHOD,",
        "   ElementType.PACKAGE,",
        "   ElementType.TYPE})",
        "@Documented",
        "public @interface ExperimentalApi {",
        "  String value() default \"\";",
        "}");

    // add an annotated class
    compiler.addSourceLines("io/grpc/AnnotatedClass.java",
        "package io.grpc;",
        "",
        "import io.grpc.ExperimentalApi;",
        "",
        "@ExperimentalApi(\"https://example.com/issue\")",
        "public class AnnotatedClass {",
        "  public static final int MEMBER = 42;",
        "  public static int foo() { return 42; }",
        "}");

    // add a stable api class
    compiler.addSourceLines("io/grpc/StableApi.java",
        "package io.grpc;",
        "",
        "public class StableApi {",
        "  public void foo() {}",
        "}");

    // add a annotated class members;
    compiler.addSourceLines("io/grpc/AnnotatedMember.java",
        "package io.grpc;",
        "",
        "import io.grpc.ExperimentalApi;",
        "",
        "public class AnnotatedMember {",
        "  @ExperimentalApi",
        "  public void instanceMethod() {}",
        "",
        "  @ExperimentalApi",
        "  public static int MEMBER = 42;",
        "",
        "  @ExperimentalApi",
        "  public static void staticMethod() {};",
        "",
        "",
        "  @ExperimentalApi",
        "  public final int member = 42;",
        "}");

    // add an annotated interface
    compiler.addSourceLines("io/grpc/IAnnotated.java",
        "package io.grpc;",
        "",
        "import io.grpc.ExperimentalApi;",
        "",
        "@ExperimentalApi",
        "public interface IAnnotated {",
        "}");
  }

  @Test
  public void negative() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    System.out.println(args);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeInstantiationStableApi() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.StableApi;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    StableApi api = new StableApi();",
            "    System.out.println(args);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void negativeWildCardImport() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.*;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    StableApi api = new StableApi();",
            "    System.out.println(args);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveInstantiationAndCaptureDescriptionLinkUrl() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  public static void main(String[] args) {",
            "    // BUG: Diagnostic contains: https://example.com/issue",
            "    new AnnotatedClass();",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveStaticMemberSelection() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(AnnotatedClass.MEMBER);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveStaticMethodInvocation() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(AnnotatedClass.foo());",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveInstanceMethodInvocation() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  public static void main(String args[]) {",
            "    // BUG: Diagnostic contains: ",
            "    AnnotatedClass foo = new AnnotatedClass();",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(foo.foo());",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingAsParameter() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  // BUG: Diagnostic contains: ",
            "  public static void foo(AnnotatedClass bar) {}",
            "  public static void main(String args[]) {}",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingAsTypeParameter() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "import java.util.List;",
            "",
            "public class Test {",
            "  // BUG: Diagnostic contains: ",
            "  public static void foo(List<AnnotatedClass> bar) {}",
            "  public static void main(String args[]) {",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingAsSuperType() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "// BUG: Diagnostic contains: ",
            "public class Test extends AnnotatedClass {}")
        .doTest();
  }

  @Test
  public void positiveUsingAsClassVarianceBoundary() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "// BUG: Diagnostic contains: ",
            "public class Test<T extends AnnotatedClass> {}")
        .doTest();
  }

  @Test
  public void positiveUsingImplementVariance() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "import java.util.List;",
            "",
            "// BUG: Diagnostic contains: ",
            "abstract class Test implements List<AnnotatedClass> {}")
        .doTest();
  }

  @Test
  public void positiveUsingMethodVarianceBoundary() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.AnnotatedClass;",
            "",
            "public class Test {",
            "  // BUG: Diagnostic contains: ",
            "  public static <T extends AnnotatedClass> T foo() { return null; }",
            "}")
        .doTest();
  }

  @Test
  public void positiveFullQualifiedReference() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(io.grpc.AnnotatedClass.MEMBER);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveStaticImportAndUse() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import static io.grpc.AnnotatedClass.MEMBER;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(MEMBER);",
            "  }",
            "}")
        .doTest();
  }

  // ----- members tests ----

  @Test
  public void negativeNonAnnotatedClassInstantiation() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.AnnotatedMember;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    new AnnotatedMember();",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingNonAnnotatedClassButInstanceMethodIsAnnotated() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.AnnotatedMember;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    AnnotatedMember a = new AnnotatedMember();",
            "    // BUG: Diagnostic contains: ",
            "    a.instanceMethod();",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingNonAnnotatedClassButInstanceMemberIsAnnotated() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.AnnotatedMember;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    AnnotatedMember a = new AnnotatedMember();",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(a.member);",
            "  }",
            "}")
        .doTest();
  }

  @Test
  public void positiveUsingNonAnnotatedClassButStaticMemberIsAnnotated() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "import io.grpc.AnnotatedMember;",
            "",
            "public class Test {",
            "  public void foo() {",
            "    AnnotatedMember a = new AnnotatedMember();",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(a.MEMBER);",
            "    // BUG: Diagnostic contains: ",
            "    System.out.println(AnnotatedMember.MEMBER);",
            "  }",
            "}")
        .doTest();
  }

  // ----- interface tests -----
  @Test
  public void positiveInterfaceImplements() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.IAnnotated;",
            "",
            "// BUG: Diagnostic contains: ",
            "public class Test implements IAnnotated {",
            "}")
        .doTest();
  }

  @Test
  public void positiveInterfaceExtends() {
    compiler
        .addSourceLines("example/Test.java",
            "package example;",
            "",
            "// BUG: Diagnostic contains: ",
            "import io.grpc.IAnnotated;",
            "",
            "// BUG: Diagnostic contains: ",
            "public interface Test extends IAnnotated {",
            "}")
        .doTest();
  }
}
