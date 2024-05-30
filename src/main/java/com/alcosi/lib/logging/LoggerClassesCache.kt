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

import com.alcosi.lib.executors.sync
import org.aspectj.lang.ProceedingJoinPoint
import java.util.concurrent.Semaphore
import java.util.logging.Logger

/**
 * The LoggerClassesCache class provides caching and retrieval of loggers
 * associated with their respective classes.
 */
open class LoggerClassesCache {
    /**
     * A mutable map that stores loggers associated with their respective
     * classes. The logger map is initialized as an empty HashMap.
     */
    protected open val loggerMap: MutableMap<Class<*>, Logger> = HashMap()

    /**
     * Semaphore used for thread synchronization in logging operations.
     *
     * @property loggerSemaphore The semaphore object used for synchronization.
     */
    protected open val loggerSemaphore = Semaphore(1)


    /**
     * Retrieves the logger for the given join point.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method being
     *     executed.
     * @return The logger instance for the class of the join point.
     */
    open fun getLogger(joinPoint: ProceedingJoinPoint): Logger {
        return loggerSemaphore.sync {
            val clazz: Class<*> = joinPoint.target.javaClass
            val logger = loggerMap[clazz]
            if (logger == null) {
                val loggerVal = Logger.getLogger(clazz.name)
                loggerMap[clazz] = loggerVal
                return@sync loggerVal
            } else {
                return@sync logger
            }
        }
    }

    /**
     * The INSTANCE object represents a LoggerClassesCache instance. It
     * provides caching and retrieval of loggers associated with their
     * respective classes.
     *
     * Implementation Notes:
     * - INSTANCE is an object of LoggerClassesCache class.
     *
     * Example Usage:
     *
     *  ```
     *  val logger = INSTANCE.getLogger(joinPoint)
     *  ```
     */
    object INSTANCE : LoggerClassesCache()
}
