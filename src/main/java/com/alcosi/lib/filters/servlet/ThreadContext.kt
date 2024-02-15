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

package com.alcosi.lib.filters.servlet

import com.alcosi.lib.executors.PresetInheritableThreadLocal
import com.alcosi.lib.security.PrincipalDetails
import org.apache.commons.lang3.StringUtils
import java.util.*

open class ThreadContext {
    protected val local: InheritableThreadLocal<MutableMap<String, Any?>> = PresetInheritableThreadLocal(mutableMapOf())

    open fun getAll(): MutableMap<String, Any?> {
        return local.get()
    }

    open fun <T> get(name: String): T? {
        val any = getAll()[name]
        return any as T?
    }

    open fun contains(name: String): Boolean {
        return getAll().contains(name)
    }

    open fun setAuthPrincipal(value: PrincipalDetails) {
        getAll()[AUTH_PRINCIPAL] = value
    }

    open fun <T : PrincipalDetails> getAuthPrincipal(): T? {
        return getAll()[AUTH_PRINCIPAL] as T?
    }

    open fun getRqId(): String {
        val value = get<String>(RQ_ID)
        if (value != null) {
            return value
        } else {
            val generated = getIdString()
            set(RQ_ID, generated)
            return generated
        }
    }

    open fun set(
        name: String,
        value: Any?,
    ) {
        getAll()[name] = value
    }

    open fun clear() {
        local.set(mutableMapOf())
    }

    companion object {
        val AUTH_PRINCIPAL = "AUTH_PRINCIPAL"
        val RQ_ID = "RQ_ID"
        val RQ_ID_INDEX = "RQ_ID_INDEX"

        val REQUEST_ORIGINAL_IP = "REQUEST_ORIGINAL_IP"
        val REQUEST_ORIGINAL_USER_AGENT = "REQUEST_ORIGINAL_USER_AGENT"
        val REQUEST_PLATFORM = "REQUEST_PLATFORM"

        private val RANDOM = Random()

        protected fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = StringUtils.leftPad(integer.toString() + "", 7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }
    }
}
