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
import java.nio.charset.StandardCharsets
import java.util.logging.Logger

open class RabbitRsLoggingMessagePostProcessor(val maxBodySize: Int) :
    RabbitLoggingMessagePostProcessor {
    override fun postProcessMessage(message: Message): Message {
        val properties = message.messageProperties
        val correlationInfoParts = properties.correlationId.split(";")
        val id = correlationInfoParts[0]
        val exchange = correlationInfoParts[1]
        val routingKey = correlationInfoParts[2]
        val queue = correlationInfoParts[3]
        val time = correlationInfoParts[4].toLong()
        properties.correlationId = id
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
        val logBody = constructRsBody(id, time, exchange, routingKey, queue, headers, propsString, body)
        logger.info(logBody)
        return message
    }

    protected open fun constructRsBody(
        id: String,
        time: Long,
        exchange: String,
        routingKey: String,
        queue: String,
        headers: List<String>,
        propsString: String,
        body: String,
    ): String {
        val logBody =
            """
            
            ===========================SERVER AMQP response begin===========================
            =ID           : $id
            =Took         : ${System.currentTimeMillis() - time} ms
            =Exchange     : $exchange
            =Routing key  : $routingKey
            =Queue        : $queue
            =Headers      : $headers
            =Properties   : $propsString    
            =Body         : $body
            ===========================SERVER AMQP response end   ==========================
            """.trimIndent()
        return logBody
    }

    companion object {
        val logger = Logger.getLogger(this::class.java.name)
    }
}
