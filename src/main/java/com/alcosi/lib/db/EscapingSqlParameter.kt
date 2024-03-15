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

class EscapingSqlParameter : MapSqlParameterSource() {
    protected fun escapeString(value: Any?): Any? {
        if (value is String) {
            return value.replace("\u0000", "<0x00>")
        } else {
            return value
        }
    }

    override fun addValue(
        paramName: String,
        value: Any?,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value))
    }

    override fun addValue(
        paramName: String,
        value: Any?,
        sqlType: Int,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value), sqlType)
    }

    override fun addValue(
        paramName: String,
        value: Any?,
        sqlType: Int,
        typeName: String,
    ): MapSqlParameterSource {
        return super.addValue(paramName, escapeString(value), sqlType, typeName)
    }

    override fun addValues(values: MutableMap<String, *>?): MapSqlParameterSource {
        return super.addValues(values?.mapValues { escapeString(it) })
    }
}
