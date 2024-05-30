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
 * Represents a ResultSet that provides logging capabilities by overriding the `next()` method and applying a list of
 * row callbacks to the original ResultSet.
 *
 * @property callbacks The list of RowCallbackHandlers to be applied to the ResultSet.
 * @property original The original ResultSet to be wrapped.
 */
data class LoggingResultSet(val callbacks: List<RowCallbackHandler>, val original: ResultSet) : ResultSet by original {
    /**
     * Overrides the next() method of the ResultSet interface to provide logging capabilities.
     *
     * @return `true` if the iteration has more elements, `false` otherwise.
     */
    override fun next(): Boolean {
        try {
            val rs = original.next()
            if (rs) {
                callbacks.forEach { it.processRow(original) }
            }
            return rs
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error! ", t)
            return false
        }
    }

    /**
     * The `Companion` class defines a singleton object containing a logger for logging purposes*/
    companion object {
        /**
         * This variable represents an instance of a logger used for logging purposes.
         */
        val logger = Logger.getLogger(this::class.java.name)
    }
}
