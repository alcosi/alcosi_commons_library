/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
open class LoggingErrorAspect {
    protected open var loggerMap: MutableMap<Class<*>, Logger> = HashMap()

    @Pointcut("@annotation(com.alcosi.lib.logging.annotations.LogError)|| within(@com.alcosi.lib.logging.annotations.LogError *)")
    fun callAt() {
    }

    @Around(value = "callAt()")
    @Throws(Throwable::class)
    fun logMethodTime(joinPoint: ProceedingJoinPoint): Any? {
        return try {
            joinPoint.proceed()
        } catch (t: Throwable) {
            val signature = joinPoint.signature as MethodSignature
            val declaringType = signature.declaringType
            getLogger(joinPoint).log(getLoggingLevel(signature, declaringType), "${declaringType.simpleName}:${signature.name}. Exception:", t)
            throw t
        }
    }

    open fun getLoggingLevel(
        sign: MethodSignature,
        type: Class<Any>,
    ): Level {
        val methodLevel = sign.method.getDeclaredAnnotation(LogError::class.java)?.level
        val level =
            if (methodLevel == null) {
                type.getDeclaredAnnotation(LogError::class.java)?.level ?: "INFO"
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
