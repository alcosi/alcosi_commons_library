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

import org.springframework.jdbc.core.*
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * A subclass of JdbcTemplate that provides logging functionality to the executed SQL queries and parameters.
 *
 * @property callbacks The list of RowCallbackHandlers to be applied to the ResultSet.
 * @param dataSource The DataSource instance to be used.
 */
open class LoggingJdbcTemplate(
    val callbacks: List<RowCallbackHandler>,
    dataSource: DataSource,
) : JdbcTemplate(dataSource) {
    /**
     * Process a ResultSet by applying logging functionality and extracting the result set.
     *
     * @param rs The ResultSet to be processed. Can be null.
     * @param param The ResultSetSupportingSqlParameter used for processing the result set.
     * @return A mutable map containing the processed data from the ResultSet.
     */
    override fun processResultSet(
        rs: ResultSet?,
        param: ResultSetSupportingSqlParameter,
    ): MutableMap<String, Any> {
        if (rs == null) {
            return HashMap()
        }
        return super.processResultSet(LoggingResultSet(callbacks, rs), param)
    }

    /**
     * Executes a SQL query with the provided parameters and applies a ResultSetExtractor to process the result set.
     *
     * @param psc The PreparedStatementCreator used to create a PreparedStatement.
     * @param pss The PreparedStatementSetter used to set the PreparedStatement parameters, can be null.
     * @param rse The ResultSetExtractor used to process the ResultSet and extract the result, should not be null.
     * @param T The type of the extracted result.
     * @return The result of the query execution, or null if the result is empty.
     */
    override fun <T> query(
        psc: PreparedStatementCreator,
        pss: PreparedStatementSetter?,
        rse: ResultSetExtractor<T>,
    ): T? {
        val processor = CallbackProcessorResultSetExtractor(callbacks, rse)
        return super.query(psc, pss, processor)
    }
}
