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

import com.alcosi.lib.objectMapper.MappingHelper
import com.alcosi.lib.objectMapper.mapOne
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * The `JsonRowMapper` class is responsible for mapping a JSON string retrieved from a database
 * ResultSet to an instance of a specified class using a `MappingHelper` and an `ObjectMapper`.
 *
 * @param T The type of the object to be mapped.
 * @param mappingHelper The `MappingHelper` used for mapping the JSON string to the object.
 * @param type The `TypeReference` representing the type of the object.
 */
open class JsonRowMapper<T : Any>(protected val mappingHelper: ObjectMapper, protected val type: TypeReference<T>) : RowMapper<T> {
    constructor(mappingHelper: ObjectMapper) : this(mappingHelper, object : TypeReference<T>() {})
    @Deprecated("Use ObjectMapper constructors")
    constructor(mappingHelper: MappingHelper) : this(mappingHelper.objectMapper, object : TypeReference<T>() {})
    @Deprecated("Use ObjectMapper constructors")
    constructor(mappingHelper: MappingHelper,type: TypeReference<T>) : this(mappingHelper.objectMapper, type)

    /**
     * Maps a single row from a ResultSet to a target object of type T.
     *
     * @param rs The ResultSet object that contains the data to be mapped.
     * @param rowNum The current row number in the ResultSet.
     * @return The mapped object of type T. Returns null if the ResultSet does not contain data or the mapped object cannot be created.
     */
    override fun mapRow(
        rs: ResultSet,
        rowNum: Int,
    ): T? {
        val json = rs.getString(1) ?: return null
        return mappingHelper.mapOne(json, type)
    }
}
