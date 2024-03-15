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

package com.alcosi.lib.rabbit

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException
import org.springframework.http.HttpStatus
import java.nio.charset.StandardCharsets.UTF_8
import java.util.logging.Level
import java.util.logging.Logger

open class RabbitErrorHandler(val objectMapper: ObjectMapper) : RabbitListenerErrorHandler {
    init {
        RabbitErrorRs.objectMapper = objectMapper
    }

    override fun handleError(
        amqpMessage: Message?,
        message: org.springframework.messaging.Message<*>?,
        exception: ListenerExecutionFailedException?,
    ): Any {
        val t = if (exception?.cause == null) exception else exception.cause!!
        logger.log(Level.SEVERE, "Rabbit handler uncaught exception ${amqpMessage?.messageProperties?.receivedRoutingKey}:${amqpMessage?.messageProperties?.correlationId}", exception)
        return getRs(RabbitErrorRs.fromThrowable(t), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun getRs(
        r: Any?,
        status: HttpStatus,
    ): Message {
        val bytes =
            if (r is String) {
                r.toByteArray(UTF_8)
            } else if (r is ByteArray) {
                r
            } else {
                objectMapper.writeValueAsBytes(r)
            }
        val props = MessageProperties()
        props.contentType = MessageProperties.CONTENT_TYPE_JSON
        props.setHeader("status", status.value())
        val msg = Message(bytes, props)
//        logger.info("Response:${props} ${String(bytes)}",)
//        logger.info("Mapping rs took time :${ System.currentTimeMillis() - time}")
        return msg
    }

    @JvmRecord
    data class RabbitErrorRs(val error: String?, val message: String?, val code: Int?) {
        constructor(error: String?, msg: String?) : this(error, msg, null)

        override fun toString(): String {
            return objectMapper!!.writeValueAsString(this)
        }

        companion object {
            var objectMapper: ObjectMapper? = null

            fun fromThrowable(t: Throwable?): RabbitErrorRs {
                return RabbitErrorRs(t?.javaClass?.name, t?.message)
            }
        }
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}
