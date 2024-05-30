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
import com.alcosi.lib.logging.LoggerClassesCache
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.logging.Level
import java.util.logging.Logger

/**
 * LoggingTimeAspect class is an aspect that handles logging of method
 * execution time.
 *
 * Initialization: The loggerMap is initialized as a HashMap.
 *
 * Usage: The loggerMap is used to associate each target class with a
 * logger. When a method is executed, the logMethodTime() function is
 * invoked, which measures the execution time of the method and logs it
 * along with the class and method names. The getLogger() function is
 * used to retrieve or create loggers for each class. If a logger for a
 * class already exists in the loggerMap, it is retrieved and returned.
 * Otherwise, a new logger is created using the class name and added to
 * the loggerMap before being returned. The getLoggingLevel() function is
 * used to determine the logging level for a specific method and class. It
 * checks for the presence of the
 *
 * @logTime annotation on the method, and if not found, it checks for
 * the @LogTime annotation on the class. If no annotation is found, it
 * defaults to "INFO" level.
 *
 * Restrictions: The loggerMap property is marked as protected, which
 * means it can only be accessed within the class or its subclasses. The
 * loggerMap property is open, allowing it to be overridden by subclasses.
 *
 * Example usage:
 * ```
 * @Aspect
 * class MyClass {
 *
 *      companion object {
 *          private val logger = Logger.getLogger(MyClass::class.java.name)
 *      }
 *
 *      @LogTime("DEBUG")
 *      fun myMethod() {
 */
@Aspect
open class LoggingTimeAspect {
    /**
     * Represents a pointcut for logging method execution time. This pointcut
     * includes methods annotated with @LogTime and classes annotated
     * with @LogTime.
     */
    @Pointcut("@annotation(com.alcosi.lib.logging.annotations.LogTime)|| within(@com.alcosi.lib.logging.annotations.LogTime *)")
    open fun callAt() {
    }

    /**
     * Logs the execution time of a method and any exceptions thrown during its
     * execution.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method being
     *     executed.
     * @return The result of the method execution.
     * @throws Throwable If an exception occurs during the method execution.
     */
    @Around(value = "callAt()")
    @Throws(Throwable::class)
    open fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        var exception = false
        return try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            exception = true
            throw t
        } finally {
            val signature = joinPoint.signature as MethodSignature
            val declaringType = signature.declaringType
            val msg = "Time metric ${declaringType.simpleName}:${signature.name} took ${System.currentTimeMillis() - start} ms. Exception:$exception"
            getLogger(joinPoint).log(getLoggingLevel(signature, declaringType), msg)
        }
    }

    /**
     * Retrieves the logging level for a given method and class.
     *
     * @param sign The MethodSignature object representing the method.
     * @param type The Class object representing the class.
     * @return The logging level for the method, or "INFO" if no level is
     *     specified.
     */
    open fun getLoggingLevel(
        sign: MethodSignature,
        type: Class<Any>,
    ): Level {
        val methodLevel = sign.method.getDeclaredAnnotation(LogTime::class.java)?.level
        val level =
            if (methodLevel == null) {
                type.getDeclaredAnnotation(LogTime::class.java)?.level ?: JavaLoggingLevel.INFO
            } else {
                methodLevel
            }
        return level.javaLevel
    }

    /**
     * Retrieves or creates a logger for the specified join point target class.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method call.
     * @return The logger associated with the join point target class.
     */
    protected open fun getLogger(joinPoint: ProceedingJoinPoint): Logger {
        return LoggerClassesCache.INSTANCE.getLogger(joinPoint)
    }
}
