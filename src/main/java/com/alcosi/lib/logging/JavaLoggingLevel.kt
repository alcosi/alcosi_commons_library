/*
 *
 *  * Copyright (c) 2024 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.alcosi.lib.logging

import java.util.logging.Level

/**
 * The JavaLoggingLevel enum represents the different levels of logging in Java logging library.
 *
 * The levels are ordered from highest to lowest severity:
 * - SEVERE
 * - WARNING
 * - INFO
 * - CONFIG
 * - FINE
 * - FINER
 * - FINEST
 *
 * Each level is associated with a corresponding Java Level object, defined in the java.util.logging.Level class.
 *
 * Usage Examples:
 *     JavaLoggingLevel.SEVERE : Represents a severe error that requires immediate attention.
 *     JavaLoggingLevel.WARNING : Represents a warning that may indicate a potential problem.
 *     JavaLoggingLevel.INFO : Represents an informative message about the progress of the application.
 *     JavaLoggingLevel.CONFIG : Represents configuration information.
 *     JavaLoggingLevel.FINE : Represents fine-grained tracing information.
 *     JavaLoggingLevel.FINER : Represents even more detailed tracing information.
 *     JavaLoggingLevel.FINEST : Represents the lowest level of tracing information.
 *
 * Note: This class does not have an explicit author or version tag.
 */
enum class JavaLoggingLevel(val javaLevel: Level) {
    SEVERE(Level.SEVERE), //(highest value)
    WARNING(Level.WARNING),
    INFO(Level.INFO),
    CONFIG(Level.CONFIG),
    FINE(Level.FINE),
    FINER(Level.FINER),
    FINEST(Level.FINEST)// (lowest value)
}