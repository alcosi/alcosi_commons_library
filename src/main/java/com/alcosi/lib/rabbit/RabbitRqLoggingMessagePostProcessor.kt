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
