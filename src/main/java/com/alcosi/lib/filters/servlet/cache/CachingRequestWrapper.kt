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
package com.alcosi.lib.filters.servlet.cache

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

open class CachingRequestWrapper(val maxBodySize: Int, val request: HttpServletRequest) : HttpServletRequest by request {
    val body: ByteArray
    val isCached: Boolean

    init {
        isCached = request.contentLength < maxBodySize
        val parameterMap = request.parameterMap
        body = if (isCached) request.inputStream.readAllBytes() else "<TOO BIG ${request.contentLength} bytes>".toByteArray()
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        return if (isCached) BodyInputStream(body.inputStream()) else request.inputStream
    }

    private class BodyInputStream(val delegate: ByteArrayInputStream) : ServletInputStream() {
        override fun isFinished(): Boolean {
            return false
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(readListener: ReadListener) {
            throw UnsupportedOperationException()
        }

        @Throws(IOException::class)
        override fun read(): Int {
            return delegate.read()
        }

        @Throws(IOException::class)
        override fun read(
            b: ByteArray,
            off: Int,
            len: Int,
        ): Int {
            return delegate.read(b, off, len)
        }

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int {
            return delegate.read(b)
        }

        @Throws(IOException::class)
        override fun skip(n: Long): Long {
            return delegate.skip(n)
        }

        @Throws(IOException::class)
        override fun available(): Int {
            return delegate.available()
        }

        @Throws(IOException::class)
        override fun close() {
            delegate.close()
        }

        @Synchronized
        override fun mark(readlimit: Int) {
            delegate.mark(readlimit)
        }

        @Synchronized
        @Throws(IOException::class)
        override fun reset() {
            delegate.reset()
        }

        override fun markSupported(): Boolean {
            return delegate.markSupported()
        }
    }
}
