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

package com.alcosi.lib.rabbit

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

open class RabbitRqLoggingMessagePostProcessor(val maxBodySize: Int) : RabbitLoggingMessagePostProcessor {
    override fun postProcessMessage(message: Message): Message {
        val time = System.currentTimeMillis()
        val properties = message.messageProperties
        val id = properties.correlationId ?: RabbitLoggingMessagePostProcessor.getIdString()
        val propsString = properties.toCompactString()
        val headers = properties.headers.map { "${it.key}:${it.value}" }
        val body =
            if (message.body == null || message.body.isEmpty()) {
                ""
            } else if (message.body.size > maxBodySize) {
                "<TOO BIG ${message.body.size} bytes>"
            } else {
                String(message.body, StandardCharsets.UTF_8)
            }
        val logBody = constructRqBody(id, properties, headers, propsString, body)
        logger.info(logBody)
        properties.correlationId = "$id;${properties.receivedExchange};${properties.receivedRoutingKey};${properties.consumerQueue};$time"
        return message
    }

    protected open fun constructRqBody(
        id: String,
        properties: MessageProperties,
        headers: List<String>,
        propsString: String,
        body: String,
    ): String {
        val logBody =
            """
            
            ===========================SERVER AMQP request begin===========================
            =ID           : $id
            =Exchange     : ${properties.receivedExchange}
            =Routing key  : ${properties.receivedRoutingKey}
            =Queue        : ${properties.consumerQueue}
            =Headers      : $headers
            =Properties   : $propsString    
            =Body         : $body
            ===========================SERVER AMQP request end   ==========================
            """.trimIndent()
        return logBody
    }

    companion object {
        val logger = Logger.getLogger(this.javaClass.name)
    }
}
