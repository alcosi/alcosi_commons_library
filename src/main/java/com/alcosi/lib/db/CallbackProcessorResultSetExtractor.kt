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

import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet

/**
 * Represents a ResultSetExtractor that wraps an existing ResultSetExtractor and applies a list of RowCallbackHandlers to the ResultSet before extracting the data.
 *
 * @param T The type of the extracted data.
 * @property callbacks The list of RowCallbackHandlers to be applied to the ResultSet.
 * @property extractor The ResultSetExtractor to extract the data from the ResultSet.
 */
@JvmRecord
data class CallbackProcessorResultSetExtractor<T>(val callbacks: List<RowCallbackHandler>, val extractor: ResultSetExtractor<T>) : ResultSetExtractor<T> {
    /**
     * Extracts data from the provided ResultSet using the specified extractor.
     *
     * @param rs The ResultSet from which the data will*/
    override fun extractData(rs: ResultSet): T? {
        return extractor.extractData(LoggingResultSet(callbacks, rs))
    }
}
