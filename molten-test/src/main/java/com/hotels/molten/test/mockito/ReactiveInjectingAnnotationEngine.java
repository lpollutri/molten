/*
 * Copyright (c) 2020 Expedia, Inc.
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

package com.hotels.molten.test.mockito;

import java.lang.reflect.Field;
import java.util.Set;

import org.mockito.internal.configuration.InjectingAnnotationEngine;

/**
 * Entry point {@link org.mockito.plugins.AnnotationEngine} of reactive mocking.
 * <p>
 * Extends {@link InjectingAnnotationEngine} behaviour with {@link ReactiveAnnotationEngine}.
 *
 * @deprecated will be removed along with {@link ReactiveMock}, see details there
 */
@Deprecated
public class ReactiveInjectingAnnotationEngine extends InjectingAnnotationEngine {
    private final ReactiveAnnotationEngine reactiveAnnotationEngine = new ReactiveAnnotationEngine();

    @Override
    protected void onInjection(Object testInstance, Class<?> clazz, Set<Field> mockDependentFields, Set<Object> mocks) {
        mocks.addAll(reactiveAnnotationEngine.process(clazz, testInstance));
    }
}
