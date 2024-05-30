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
import java.util.function.Supplier

/**
 * The `ThreadContext` class provides a mechanism to store and retrieve thread-local data in a mutable map.
 * It utilizes an `InheritableThreadLocal` variable to maintain the thread-local data across thread boundaries.
 *
 * The `ThreadContext` class provides the following methods for managing thread-local data:
 *
 * - `getAll()`: Returns the entire thread-local map.
 * - `get(name: String)`: Returns the value associated with the specified key in the thread-local map.
 * - `contains(name: String)`: Checks if the thread-local map contains a value associated with the specified key.
 * - `setAuthPrincipal(value: PrincipalDetails?)`: Sets the authentication principal in the thread-local map.
 * - `getAuthPrincipal()`: Returns the authentication principal from the thread-local map.
 * - `getRqId()`: Returns the request ID from the thread-local map.
 * - `set(name: String, value: Any?)`: Sets a value in the thread-local map with the specified key.
 * - `clear()`: Clears the thread-local map.
 *
 * The `ThreadContext` class also provides a companion object with the following constants and utility methods:
 *
 * Constants:
 * - `AUTH_PRINCIPAL`: Represents the key for the authentication principal in the thread-local map.
 * - `RQ_ID`: Represents the key for the request ID in the thread-local map.
 * - `RQ_ID_INDEX`: Represents the key for the request ID index in the thread-local map.
 * - `REQUEST_ORIGINAL_IP`: Represents the key for the original request IP in the thread-local map.
 * - `REQUEST_ORIGINAL_AUTHORISATION_TOKEN`: Represents the key for the original request authorization token in the thread-local map.
 * - `REQUEST_ORIGINAL_USER_AGENT`: Represents the key for the original request user agent in the thread-local map.
 * - `REQUEST_PLATFORM`: Represents the key for the request platform in the thread-local map.
 *
 * Utility Methods:
 * - `getIdString()`: Generates a random ID string with the format "XXXX-XXXX".
 *
 * Example Usage:
 *
 * ```
 * val threadContext = ThreadContext()
 *
 **/
open class ThreadContext {
    /**
     * Represents a local variable stored as an inheritable thread-local variable.
     *
     * The variable is stored as a mutable map with string keys and any values
     * in order to allow storing different types of values.
     *
     * The variable can be accessed and modified by multiple threads while preserving
     * each thread's own copy of the variable.
     */
    protected val local: InheritableThreadLocal<MutableMap<String, Any?>> = PresetInheritableThreadLocal(Supplier { mutableMapOf() })

    /**
     * Retrieves all the elements from the local storage.
     *
     * @return a mutable map containing all the elements from the local storage.
     */
    open fun getAll(): MutableMap<String, Any?> {
        return local.get()
    }

    /**
     * Returns the value associated with the given name.
     */
    open fun <T> get(name: String): T? {
        val any = getAll()[name]
        return any as T?
    }

    /**
     * Checks whether the given name is contained in the*/
    open fun contains(name: String): Boolean {
        return getAll().contains(name)
    }

    /**
     * Sets the authentication principal for the current thread context.
     *
     * @*/
    open fun setAuthPrincipal(value: PrincipalDetails?) {
        getAll()[AUTH_PRINCIPAL] = value
    }

    /**
     * Retrieves the authentication principal from the stored collection.
     *
     * @return The authentication principal, or null if not found.
     * @param T The type of the principal details.
     */
    open fun <T : PrincipalDetails> getAuthPrincipal(): T? {
        return getAll()[AUTH_PRINCIPAL] as T?
    }

    /**
     * Returns the request ID.
     *
     * @return The request ID as a String.
     */
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

    /**
     * Sets a value with the given name in the data set.
     *
     * @param name The name of the value to set.
     * @param value The value to set.
     */
    open fun set(
        name: String,
        value: Any?,
    ) {
        getAll()[name] = value
    }

    /**
     * Clears the local storage of the current thread context.
     * Any previously stored values will be removed.
     */
    open fun clear() {
        local.remove()
    }

    /**
     * This class represents the companion object of the ThreadContext class.
     * It provides static properties and methods that can be accessed without an instance of the class.
     */
    companion object {
        val AUTH_PRINCIPAL = "AUTH_PRINCIPAL"
        val RQ_ID = "RQ_ID"
        val RQ_ID_INDEX = "RQ_ID_INDEX"

        val REQUEST_ORIGINAL_IP = "REQUEST_ORIGINAL_IP"
        val REQUEST_ORIGINAL_AUTHORISATION_TOKEN = "REQUEST_ORIGINAL_AUTHORISATION_TOKEN"

        val REQUEST_ORIGINAL_USER_AGENT = "REQUEST_ORIGINAL_USER_AGENT"
        val REQUEST_PLATFORM = "REQUEST_PLATFORM"

        private val RANDOM = Random()

        /**
         * Generates a random ID string.
         *
         * @return The randomly generated ID string.
         */
        protected fun getIdString(): String {
            val integer = RANDOM.nextInt(10000000)
            val leftPad = StringUtils.leftPad(integer.toString() + "", 7, '0')
            return leftPad.substring(0, 4) + '-' + leftPad.substring(5)
        }
    }
}
