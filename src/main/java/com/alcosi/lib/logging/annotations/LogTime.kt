/*
 * Copyright (c) 2023 Alcosi Group Ltd. and affiliates.
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
 *
 */

package com.alcosi.lib.logging.annotations

import com.alcosi.lib.logging.JavaLoggingLevel


/**
 * Annotation used to mark methods or classes for logging execution time.
 *
 * @property level The logging level for the annotated method or class.
 *     Defaults to "INFO". The levels in descending order are: SEVERE
 *     (highest value) WARNING INFO CONFIG FINE FINER FINEST (lowest value)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
)
annotation class LogTime(val level: JavaLoggingLevel = JavaLoggingLevel.INFO)
