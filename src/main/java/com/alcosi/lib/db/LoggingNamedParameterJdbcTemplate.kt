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

open class LoggingNamedParameterJdbcTemplate(
    val maxBodySize: Int,
    jdbcTemplate: JdbcTemplate,
    val queryLoggingLevel: Level?,
    val logParamsLevel: Level?,
) : NamedParameterJdbcTemplate(jdbcTemplate) {
    val logger = Logger.getLogger(this.javaClass.name)

    override fun getPreparedStatementCreator(
        sql: String,
        paramSource: SqlParameterSource,
        @Nullable customizer: Consumer<PreparedStatementCreatorFactory>?,
    ): PreparedStatementCreator {
        return execute(sql, paramSource, customizer)
    }

    private fun execute(
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

    private fun serializeValue(
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
