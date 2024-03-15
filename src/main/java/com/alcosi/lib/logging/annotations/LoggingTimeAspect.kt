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

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.logging.Level
import java.util.logging.Logger

@Aspect
open class LoggingTimeAspect {
    protected open var loggerMap: MutableMap<Class<*>, Logger> = HashMap()

    @Pointcut("@annotation(com.alcosi.lib.logging.annotations.LogTime)|| within(@com.alcosi.lib.logging.annotations.LogTime *)")
    fun callAt() {
    }

    @Around(value = "callAt()")
    @Throws(Throwable::class)
    fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
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

    fun getLoggingLevel(
        sign: MethodSignature,
        type: Class<Any>,
    ): Level {
        val methodLevel = sign.method.getDeclaredAnnotation(LogTime::class.java)?.level
        val level =
            if (methodLevel == null) {
                type.getDeclaredAnnotation(LogTime::class.java)?.level ?: "INFO"
            } else {
                methodLevel
            }
        return Level.parse(level)
    }

    @Synchronized
    protected open fun getLogger(joinPoint: ProceedingJoinPoint): Logger {
        val clazz: Class<*> = joinPoint.target.javaClass
        val logger = loggerMap[clazz]
        return if (logger == null) {
            val loggerVal = Logger.getLogger(clazz.name)
            loggerMap[clazz] = loggerVal
            loggerVal
        } else {
            logger
        }
    }
}
