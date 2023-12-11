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

import org.springframework.jdbc.core.RowCallbackHandler
import java.sql.ResultSet
import java.util.logging.Level
import java.util.logging.Level.OFF
import java.util.logging.Level.SEVERE
import java.util.logging.Logger

class LoggingResponseRowCallbackHandler(
    val maxBodySize: Int,
    private val logLevel: Level?,
) : RowCallbackHandler {
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

    private fun serializeValue(
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

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}
