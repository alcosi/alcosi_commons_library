/*
 * Copyright (c) 2023  Alcosi Group Ltd. and affiliates.
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

package com.alcosi.lib.rabbit

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException
import org.springframework.http.HttpStatus
import org.springframework.util.ErrorHandler
import java.nio.charset.StandardCharsets.UTF_8
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

open class RabbitErrorHandler(val objectMapper: ObjectMapper) : RabbitListenerErrorHandler {
    init {
        RabbitErrorRs.OBJECT_MAPPER = objectMapper
    }

    override fun handleError(
        amqpMessage: Message?,
        message: org.springframework.messaging.Message<*>?,
        exception: ListenerExecutionFailedException?
    ): Any {
        val t = if (exception?.cause == null) exception else exception.cause!!
        logger.log(Level.SEVERE,"Rabbit handler uncaught exception ${amqpMessage?.messageProperties?.receivedRoutingKey}:${amqpMessage?.messageProperties?.correlationId}", exception)
       return getRs(RabbitErrorRs.fromThrowable(t), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    fun getRs(r: Any?, status: HttpStatus): Message {
        val bytes = if (r is String) r.toByteArray(UTF_8)
        else if (r is ByteArray) r
        else objectMapper.writeValueAsBytes(r)
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
            return OBJECT_MAPPER!!.writeValueAsString(this)
        }

        companion object {
            var OBJECT_MAPPER: ObjectMapper? = null
            fun fromThrowable(t: Throwable?): RabbitErrorRs {
                return RabbitErrorRs(t?.javaClass?.name, t?.message)
            }
        }
    }
    companion object{
        val logger= Logger.getLogger(this::class.java.name)
    }

}