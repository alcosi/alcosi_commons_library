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

    open fun setAuthPrincipal(value: PrincipalDetails?) {
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
        val REQUEST_ORIGINAL_AUTHORISATION_TOKEN = "REQUEST_ORIGINAL_AUTHORISATION_TOKEN"

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
