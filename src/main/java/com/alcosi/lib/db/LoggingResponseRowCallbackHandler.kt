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

import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet
import java.util.logging.Level
import java.util.logging.Level.OFF
import java.util.logging.Level.SEVERE
import java.util.logging.Logger

/**
 * A callback handler that logs response rows from a ResultSet.
 *
 * @param maxBodySize The maximum size of the value to be logged.
 * @param logLevel The logging level for the handler.
 */
open class LoggingResponseRowCallbackHandler(
    val maxBodySize: Int,
    protected val logLevel: Level?,
) : RowCallbackHandler {
    /**
     * Processes a row from a ResultSet and logs the column values.
     *
     * @param rs The ResultSet containing the row to be processed.
     */
    override fun processRow(rs: ResultSet) {
        if (logLevel == null || logLevel == OFF) {
            return
        }
        val metaData = rs.metaData
        val columnCount: Int = metaData.columnCount
        val columnNames: MutableList<Pair<String, Int>> = ArrayList()
        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            if (columnName != null) {
                columnNames.add(columnName to i)
            } else {
                columnNames.add("<null>" to i)
            }
        }
        val map = columnNames.map { "${it.second}:${it.first}:${serializeValue(rs, it.second)}" }.joinToString(";")
        logger.log(logLevel, map)
    }

    /**
     * Serializes a value from a ResultSet.
     *
     * @param rs The ResultSet containing the value to be serialized.
     * @param it The index of the column in the ResultSet.
     * @return The serialized value as a String.
     */
    protected open fun serializeValue(
        rs: ResultSet,
        it: Int,
    ): String {
        try {
            if (it == null) {
                return "<null>"
            }
            val obj = rs.getObject(it)
            val value = obj?.toString() ?: "<null>"
            val length = value.length
            return if (length > maxBodySize) "<TOO BIG $length bytes>" else value
        } catch (t: Throwable) {
            logger.log(SEVERE, "Error serializing rs", t)
            return "<error>"
        }
    }

    /**
     * The `Companion` class is a companion object that contains a logger.
     * It serves as a utility for logging purposes.
     *
     * @property logger The logger instance used for logging.
     */
    companion object {
        /**
         * Variable logger is an instance of the Logger class used for logging purposes.
         *
         * @see Logger
         */
        val logger = Logger.getLogger(this::class.java.name)
    }
}
