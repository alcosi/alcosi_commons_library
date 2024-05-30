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
import java.util.logging.Logger

/**
 * This class is a RowCallbackHandler implementation that logs SQL warnings from a ResultSet.
 *
 * @property logLevel The logging level for the SQL warnings. If null or Level.OFF, no logging will be performed.
 */
open class LoggingWarningRowCallbackHandler(protected val logLevel: Level?) : RowCallbackHandler {
    /**
     * This method is called to process a single row of a ResultSet and log any SQL warnings.
     * It is part of the LoggingWarningRowCallbackHandler class.
     *
     * @param rs The ResultSet containing the row to process.
     */
    override fun processRow(rs: ResultSet) {
        if (logLevel == null || logLevel == Level.OFF) {
            return
        }
        var warning = rs.statement?.warnings
        while (warning != null) {
            logger.log(logLevel, "SQL warning ${warning.sqlState}:${warning.message}")
            warning = warning.nextWarning
        }
    }

    /**
     * The `Companion` class contains a single companion object with a logger property.
     *
     *
     * @property logger The logger instance for this class.
     */
    companion object {
        /**
         * The logger variable is used to log messages. It is an instance of the Logger class from the java.util.logging package.
         */
        val logger = Logger.getLogger(this::class.java.name)
    }
}
