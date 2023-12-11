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

package com.alcosi.lib.rabbit

import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.core.MessageProperties
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

interface RabbitLoggingMessagePostProcessor : MessagePostProcessor {
    fun MessageProperties.toCompactString(): String {
        val string =
            PROPERTIES_FIELDS
                .asSequence()
                .map { it.first to it.second?.get(this) }
                .filter { it.second != null }
                .map { "${it.first}:${it.second}" }
                .joinToString(", ")
        return "[$string]"
    }

    companion object {
        private val RANDOM = Random()

        fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = integer.toString().padStart(7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }

        val PROPERTIES_FIELDS =
            MessageProperties::class.declaredMemberProperties.asSequence()
                .filter { it.name != "headers" }
                .filter { !it.name.startsWith("target") }
                .map {
                    val javaField = it.javaField
                    javaField?.trySetAccessible()
                    it.name to javaField
                }.toList()
    }
}
