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
