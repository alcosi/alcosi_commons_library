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

/**
 * RabbitLoggingMessagePostProcessor is an interface that extends the MessagePostProcessor interface.
 * It provides methods to perform logging-related post-processing on RabbitMQ messages.
 */
interface RabbitLoggingMessagePostProcessor : MessagePostProcessor {
    /**
     * Converts the message properties to a compact string representation.
     *
     * @return A string representation of the message properties in a compact format.
     */
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
        /**
         * A private constant variable representing a random number generator.
         * This variable is used to generate random numbers and random strings.
         */
        private val RANDOM = Random()

        /**
         * Generates a unique identifier string.
         *
         * @return A string representing a unique identifier.
         */
        fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = integer.toString().padStart(7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }

        /**
         * PROPERTIES_FIELDS is a constant variable of type List<Pair<String, Field?>>.
         * It contains the list of properties defined in the MessageProperties class, excluding the "headers" property and properties starting with "target".
         * Each element in the list is a Pair of the property name (String) and the corresponding Field object (Field?).
         * The Field object represents the Java field corresponding to the property.
         *
         * Usage example:
         * Inside the toCompactString() function of the RabbitLoggingMessagePostProcessor class, PROPERTIES_FIELDS is used to iterate over the properties and retrieve their values
         *  from the MessageProperties object.
         * The values are then concatenated into a compact string representation of the message properties.
         *
         * To access the properties and fields in PROPERTIES_FIELDS, you can iterate over the list like this:
         * for ((name, field) in PROPERTIES_FIELDS) {
         *    */
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
