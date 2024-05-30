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

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.PreparedStatementCreatorFactory
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.lang.Nullable
import java.util.function.Consumer
import java.util.logging.Level
import java.util.logging.Level.OFF
import java.util.logging.Logger

/**
 * A logging wrapper for NamedParameterJdbcTemplate that provides additional logging functionality.
 *
 * @property maxBodySize The maximum body size of values to be logged.
 * @property jdbcTemplate The JdbcTemplate instance to delegate to.
 * @property queryLoggingLevel The logging level for executing SQL queries.
 * @property logParamsLevel The logging level for SQL parameters.
 */
open class LoggingNamedParameterJdbcTemplate(
    val maxBodySize: Int,
    jdbcTemplate: JdbcTemplate,
    val queryLoggingLevel: Level?,
    val logParamsLevel: Level?,
) : NamedParameterJdbcTemplate(jdbcTemplate) {
    val logger = Logger.getLogger(this.javaClass.name)

    /**
     * Returns a PreparedStatementCreator object based on the given SQL statement and SqlParameterSource.
     *
     * @param sql The SQL statement to be executed.
     * @param paramSource The SqlParameterSource containing the parameter values.
     * @param customizer (Optional) A Consumer that customizes the PreparedStatementCreatorFactory.
     * @return A PreparedStatementCreator object that can be used to create a PreparedStatement.
     */
    override fun getPreparedStatementCreator(
        sql: String,
        paramSource: SqlParameterSource,
        @Nullable customizer: Consumer<PreparedStatementCreatorFactory>?,
    ): PreparedStatementCreator {
        return execute(sql, paramSource, customizer)
    }

    /**
     * Executes an SQL statement with the provided parameters and customizer,
     * and returns a PreparedStatementCreator.
     *
     * @param sql The SQL statement to be executed.
     * @param paramSource The parameters to be used in the SQL statement.
     * @param customizer A customizer function to customize the PreparedStatementCreatorFactory.
     * @return The PreparedStatementCreator for the executed SQL statement.
     */
    protected open fun execute(
        sql: String,
        paramSource: SqlParameterSource,
        customizer: Consumer<PreparedStatementCreatorFactory>?,
    ): PreparedStatementCreator {
        if (queryLoggingLevel != null && queryLoggingLevel != OFF) {
            logger.log(queryLoggingLevel, "Executing SQL: $sql")
        }
        if (paramSource.parameterNames != null && logParamsLevel != null && logParamsLevel != OFF) {
            val params = paramSource.parameterNames?.map { "$it:${serializeValue(paramSource, it)}" }?.joinToString(";")
            logger.log(logParamsLevel, "SQL params: $params")
        }
        return super.getPreparedStatementCreator(sql, paramSource, customizer)
    }

    /**
     * Serializes the value of a parameter obtained from a SqlParameterSource
     *
     * @param paramSource The SqlParameterSource object containing the parameter value
     * @param it The name of the parameter
     *
     * @return The serialized value of the parameter as a String
     */
    protected open fun serializeValue(
        paramSource: SqlParameterSource,
        it: String?,
    ): String {
        if (it == null) {
            return "<null>"
        }
        val value = paramSource.getValue(it)?.toString() ?: "<null>"
        val length = value.length
        return if (length > maxBodySize) "<TOO BIG $length bytes>" else value
    }
}
