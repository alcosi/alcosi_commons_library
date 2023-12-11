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
