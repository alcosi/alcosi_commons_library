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
 * LoggingErrorAspect class is an aspect that handles logging of errors.
 *
 */
@Aspect
open class LoggingErrorAspect {

    /**
     * A pointcut method that matches execution of methods annotated
     * with @LogError or within classes annotated with @LogError.
     */
    @Pointcut("@annotation(com.alcosi.lib.logging.annotations.LogError)|| within(@com.alcosi.lib.logging.annotations.LogError *)")
    open fun callAt() {
    }

    /**
     * Logs the execution time of a method and handles logging of any thrown
     * exceptions.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method being
     *     executed.
     * @return The result of the method execution.
     * @throws Throwable if an exception is thrown by the method.
     */
    @Around(value = "callAt()")
    @Throws(Throwable::class)
    open fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            val signature = joinPoint.signature as MethodSignature
            val declaringType = signature.declaringType
            getLogger(joinPoint).log(getLoggingLevel(signature, declaringType), "${declaringType.simpleName}:${signature.name}. Exception:", t)
            throw t
        }
    }

    /**
     * Retrieves the logging level for a given method and class.
     *
     * @param sign The MethodSignature representing the method.
     * @param type The Class representing the class.
     * @return The logging level for the error as a Level enum.
     */
    open fun getLoggingLevel(
        sign: MethodSignature,
        type: Class<Any>,
    ): Level {
        val methodLevel = sign.method.getDeclaredAnnotation(LogError::class.java)?.level
        val level =
            if (methodLevel == null) {
                type.getDeclaredAnnotation(LogError::class.java)?.level ?: JavaLoggingLevel.INFO
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
