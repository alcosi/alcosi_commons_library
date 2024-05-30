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
import java.util.logging.Level
import java.util.logging.Logger

/**
 * RabbitRqLoggingMessagePostProcessor is a class that implements the RabbitLoggingMessagePostProcessor interface.
 * It is responsible for logging the request message before it is sent to the RabbitMQ server.
 * @param loggingLevel Level of message in log
 * @param maxBodySize The maximum size of the message body that will be logged.
 *
 * @constructor Creates an instance of RabbitRqLoggingMessagePostProcessor with the specified maxBodySize.
 */
open class RabbitRqLoggingMessagePostProcessor(val loggingLevel:Level,val maxBodySize: Int) : RabbitLoggingMessagePostProcessor {
    /**
     * Performs post-processing on the given message.
     *
     * @param message The original message to process.
     * @return The processed message.
     */
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
        logger.log(loggingLevel,logBody)
        properties.correlationId = "$id;${properties.receivedExchange};${properties.receivedRoutingKey};${properties.consumerQueue};$time"
        return message
    }

    /**
     * Constructs the request body for logging purposes.
     *
     * @param id The unique identifier of the request.
     * @param properties The message properties containing information about the request.
     * @param headers The list of headers associated with the request.
     * @param propsString A compact string representation of the message properties.
     * @param body The body of the request.
     * @return The constructed request body as a string.
     */
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
