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

package com.alcosi.lib.db

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

/**
 * EscapingSqlParameter is a subclass of MapSqlParameterSource that provides additional functionality
 * to escape string values before adding them to the map of parameters.
 */
open class EscapingSqlParameter : MapSqlParameterSource() {
    /**
     * Escapes special characters in a string value.
     *
     * @param value The value to be escaped.
     * @return The escaped value. If the input is not a string, it is returned as is.
     */
    protected fun escapeString(value: Any?): Any? {
        return if (value is String) {
            serializationReplacePairs.fold(value) { acc, pair -> acc.replace(pair.first, pair.second) }
        } else {
            value
        }
    }

    /**
     * Adds a parameter value to the MapSqlParameterSource by calling the superclass's addValue method.
     * The provided value is escaped using the escapeString method before adding it to the map.
     *
     * @param paramName The name of the parameter.
     * @param value The value of*/
    override fun addValue(
        paramName: String,
        value: Any?,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value))
    }

    /**
     * Adds a parameter value to the MapSqlParameterSource with the specified parameter name, value, and SQL type.
     *
     * @param paramName The name of the parameter.
     * @param value The value of the parameter.
     * @param sqlType The SQL type of the parameter.
     * @return The MapSqlParameterSource instance after adding the parameter.
     */
    override fun addValue(
        paramName: String,
        value: Any?,
        sqlType: Int,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value), sqlType)
    }

    /**
     * Adds a named parameter and its value to this MapSqlParameterSource object.
     *
     * @param paramName the name of the parameter
     * @param value the value of the parameter
     * @param sqlType the SQL type of the parameter
     * @param typeName the type name of the parameter
     * @return the updated MapSqlParameterSource object
     */
    override fun addValue(
        paramName: String,
        value: Any?,
        sqlType: Int,
        typeName: String,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value), sqlType, typeName)
    }

    /**
     * Adds values to a MapSqlParameterSource object.
     *
     * @param values The values to be added. It should be a mutable map with string keys and any type of values.
     * @return The modified MapSqlParameterSource object with the added values.
     */
    override fun addValues(values: MutableMap<String, *>?): MapSqlParameterSource {
        return super.addValues(values?.mapValues { escapeString(it) })
    }

    /**
     * The Companion object of the EscapingSqlParameter class.
     *
     * @property serializationReplacePairs A list of pairs containing characters to be replaced during serialization.
     */
    companion object{
        /**
         * List of pairs used for replacing certain characters during variable serialization.
         */
        val serializationReplacePairs = listOf("\u0000" to "<0x00>")
    }
}
