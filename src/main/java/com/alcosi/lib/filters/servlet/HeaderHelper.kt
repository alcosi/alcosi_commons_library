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

import com.alcosi.lib.filters.servlet.ThreadContext.Companion.AUTH_PRINCIPAL
import com.alcosi.lib.filters.servlet.ThreadContext.Companion.RQ_ID_INDEX
import com.alcosi.lib.filters.servlet.context.ContextFilter
import com.alcosi.lib.security.AccountDetails
import com.alcosi.lib.security.UserDetails
import org.springframework.http.HttpHeaders
import java.util.concurrent.atomic.AtomicInteger

/**
 * Helper class for creating request headers.
 *
 * @property serviceName The name of the service.
 * @property environment The environment of the service.
 * @property threadContext The thread context instance.
 *
 * @constructor Creates a HeaderHelper instance with the given serviceName, environment, and threadContext.
 */
open class HeaderHelper(
    val serviceName: String,
    val environment: String,
    protected val threadContext: ThreadContext,
) {
    /**
     * `contextHeaders` represents a list of context headers used in the application.
     * It is an open property that can be accessed and modified from subclasses.
     *
     * The context headers are stored as a list of strings, containing the headers names.
     * The headers are used in various parts of the application for different purposes.
     *
     * Usage example:
     * - The `contextHeaders` property is used in the `ContextFilterConfig` class as a parameter for creating the `ContextFilter`.
     * - The `contextHeaders` property is used in the `contextFilter` function of the `ContextFilterConfig` class to set the context headers of the `ContextFilter`.
     *
     * The `contextHeaders` property is initialized with the following values:
     * - `ACCOUNT_ID`: Represents the account ID header.
     * - `USER_ID`: Represents the user ID header.
     * - `RQ_ID`: Represents the request ID header.
     * - `ACTION`: Represents the action header.
     *
     * Example usage:
     * ```
     * val headerHelper = HeaderHelper()
     * val headers = headerHelper.contextHeaders
     **/
    open val contextHeaders = listOf(ACCOUNT_ID, USER_ID, RQ_ID, ACTION)

    /**
     * List of JSON headers.
     *
     * This variable represents a list of [JsonHeader] objects. Each [JsonHeader] object contains
     * information about a JSON header, including the header value, the class representing the header
     * value, and the name of the property in the thread context.
     *
     * @property jsonHeaders The list of [JsonHeader] objects.
     *
     * @see JsonHeader
     */
    open val jsonHeaders =
        listOf(
            ContextFilter.JsonHeader(USER_DETAILS, UserDetails::class, AUTH_PRINCIPAL),
            ContextFilter.JsonHeader(ACCOUNT_DETAILS, AccountDetails::class, AUTH_PRINCIPAL),
        )

    /**
     * Creates a map of request headers.
     *
     * @return The map of request headers.
     */
    open fun createRequestHeadersMap(): Map<String, String> {
        val headers = mutableMapOf<String, String?>()
        headers[ENV_HEADER] = environment
        headers[SERVICE_NAME] = serviceName
        headers[ACCOUNT_ID] = threadContext.get(ACCOUNT_ID)
        headers[USER_ID] = threadContext.get(USER_ID)
        headers[RQ_ID] = "${getContextRqId()}-${getRqIdIndex()}"
        return headers.filter { it.value != null } as Map<String, String>
    }

    /**
     * Creates the request headers for an HTTP request.
     *
     * @return The HttpHeaders object containing the request headers.
     */
    open fun createRequestHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        createRequestHeadersMap().forEach {
            headers[it.key] = it.value
        }
        return headers
    }

    /**
     * Returns the request ID for the current context.
     *
     * @return The request ID as a String.
     */
    open fun getContextRqId(): String {
        return threadContext.getRqId()
    }

    /**
     * Returns the index of the request ID.
     *
     * @return The index of the id
     */
    protected open fun getRqIdIndex(): Int {
        return if (threadContext.contains(RQ_ID_INDEX)) {
            threadContext.get<AtomicInteger>(RQ_ID_INDEX)!!.incrementAndGet()
        } else {
            val firstIndex = 1
            threadContext.set(RQ_ID_INDEX, AtomicInteger(firstIndex))
            firstIndex
        }
    }

    /**
     * The `Companion` class contains constant values used for request headers.
     * These constants can be accessed directly from the class, without the need to instantiate an object.
     *
     * Example usage:
     * ```
     * val serviceName = Companion.SERVICE_NAME
     * val accountId = Companion.ACCOUNT_ID
     * ```
     */
    companion object {
        val KEY_PROVIDER_AUTH_HEADER = "KEY_PROVIDER_ACCESS_KEY"
        val SERVICE_AUTH_HEADER = "AUTHORIZATION"
        val ENV_HEADER = "ENVIRONMENT"
        val SERVICE_NAME = "SERVICE_NAME"
        val ACCOUNT_ID = "ACCOUNT_ID"
        val USER_ID = "USER_ID"
        val USER_DETAILS = "USER_DETAILS"
        val ACCOUNT_DETAILS = "ACCOUNT_DETAILS"
        val ORIGINAL_AUTHORISATION = "ORIGINAL_AUTHORISATION"
        val RQ_ID = "RQ_ID"
        val ACTION = "ACTION"
    }
}
