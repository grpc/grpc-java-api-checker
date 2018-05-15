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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.errorprone.matchers.Description.NO_MATCH;

import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.IdentifierTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.MemberSelectTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import javax.lang.model.element.AnnotationMirror;
import java.util.Set;

abstract class AnnotationChecker extends BugChecker implements IdentifierTreeMatcher,
    MemberSelectTreeMatcher {

  private final String annotationType;

  // When this is set to true, method calls will only match the annotation if all members of the
  // method hierarchy are annotated. This is used to avoid io.grpc.internal implementations
  // "hiding" publicly declared API methods.
  private final boolean requireAnnotationOnMethodHierarchy;

  AnnotationChecker(String annotationType) {
    this(annotationType, false);
  }

  AnnotationChecker(String annotationType, boolean requireAnnotationOnMethodHierarchy) {
    this.annotationType = checkNotNull(annotationType, "annotationType");
    this.requireAnnotationOnMethodHierarchy = requireAnnotationOnMethodHierarchy;
  }

  /**
   * Returns non-null if api is annotated.
   */
  private AnnotationMirror findAnnotatedApi(Symbol symbol) {
    if (symbol == null) {
      return null;
    }
    for (AnnotationMirror annotation : symbol.getAnnotationMirrors()) {
      if (annotation.getAnnotationType().toString().equals(annotationType)) {
        return annotation;
      }
    }
    // recursive
    return findAnnotatedApi(symbol.owner);
  }

  /**
   * Returns the description if tree is annotated.
   */
  private Description match(Tree tree, VisitorState state) {
    Symbol symbol = ASTHelpers.getSymbol(tree);
    if (symbol == null) {
      return NO_MATCH;
    }
    AnnotationMirror annotation = findAnnotatedApi(symbol);
    if (annotation == null) {
      return NO_MATCH;
    }
    if (requireAnnotationOnMethodHierarchy && symbol instanceof MethodSymbol) {
      Set<MethodSymbol> superMethods =
              ASTHelpers.findSuperMethods((MethodSymbol) symbol, state.getTypes());
      for (MethodSymbol superMethod : superMethods) {
        AnnotationMirror superAnnotation = findAnnotatedApi(superMethod);
        if (superAnnotation == null) {
          return NO_MATCH;
        }
      }
    }
    return describe(tree, annotation);
  }

  protected abstract Description describe(Tree tree, AnnotationMirror annotation);

  @Override
  public Description matchIdentifier(IdentifierTree tree, VisitorState state) {
    return match(tree, state);
  }

  @Override
  public Description matchMemberSelect(MemberSelectTree tree, VisitorState state) {
    return match(tree, state);
  }
}
