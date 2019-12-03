/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.intellij.fs.notifier;

import org.gradle.api.Action;
import org.gradle.tooling.BuildAction;
import org.gradle.tooling.BuildController;
import org.gradle.tooling.model.build.InvalidatedPaths;
import org.gradle.tooling.model.build.PathsToInvalidate;

import java.io.Serializable;
import java.util.List;

public class TellGradleToInvalidateLocations implements BuildAction<Void>, Serializable {

    private final List<String> toInvalidate;

    public TellGradleToInvalidateLocations(List<String> toInvalidate) {
        this.toInvalidate = toInvalidate;
    }

    @Override
    public Void execute(BuildController controller) {
        System.out.println("Invalidating paths");
        controller.getModel(InvalidatedPaths.class, PathsToInvalidate.class, new PathsToInvalidateAction(toInvalidate));
        return null;
    }

    private static class PathsToInvalidateAction implements Action<PathsToInvalidate>, Serializable {

        private final List<String> toInvalidate;

        public PathsToInvalidateAction(List<String> toInvalidate) {
            this.toInvalidate = toInvalidate;
        }

        @Override
        public void execute(PathsToInvalidate pathsToInvalidate) {
            pathsToInvalidate.setPaths(toInvalidate);
        }
    }
}
