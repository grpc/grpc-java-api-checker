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

package io.grpc.annotations.checkers;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.BugPattern.LinkType;
import com.google.errorprone.BugPattern.SeverityLevel;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.Tree;
import java.util.Map.Entry;
import java.util.Optional;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

@AutoService(BugChecker.class)
@BugPattern(
    name = "GrpcExperimentalApi",
    summary = "@ExperimentalApi should not be used in application code",
    explanation = "@ExperimentalApi should not be used in application code",
    severity = SeverityLevel.ERROR,
    linkType = LinkType.CUSTOM,
    link = "https://github.com/grpc/grpc-java"
)
public final class ExperimentalApiChecker extends AnnotationChecker {

  public ExperimentalApiChecker() {
    super("io.grpc.ExperimentalApi");
  }

  private Optional<String> findLink(AnnotationMirror annotation) {
    // Currently, @ExperimentalApi may have a link.
    for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation
        .getElementValues().entrySet()) {
      return Optional.of(entry.getValue().toString());
    }
    return Optional.empty();
  }

  @Override
  protected Description describe(Tree tree, AnnotationMirror annotation) {
    String link = findLink(annotation).orElse(this.linkUrl());
    return Description.builder(
        tree,
        this.canonicalName(),
        link,
        this.defaultSeverity(),
        this.message())
        .build();
  }
}
