/*
 * Copyright 2018, gRPC Authors All rights reserved.
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

package io.grpc.tools.checkers;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.LinkType;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.Tree;
import javax.lang.model.element.AnnotationMirror;

@AutoService(BugChecker.class)
@BugPattern(
    name = "GrpcInternal",
    summary = "@Internal should not be used in application code",
    explanation = "@Internal should not be used in application code",
    severity = SeverityLevel.ERROR,
    linkType = LinkType.CUSTOM,
    link = "https://github.com/grpc/grpc-java"
)
public final class InternalChecker extends AnnotationChecker {

  public InternalChecker() {
    super("io.grpc.Internal");
  }

  @Override
  protected Description describe(Tree tree, AnnotationMirror annotation) {
    return describeMatch(tree);
  }
}
