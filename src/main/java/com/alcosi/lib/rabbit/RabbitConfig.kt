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

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@ConditionalOnClass(value = [RabbitAdmin::class, SimpleRabbitListenerContainerFactory::class])
@ConditionalOnProperty(prefix = "common-lib.rabbit", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(RabbitProperties::class)
class RabbitConfig(val objectMapper: ObjectMapper) {
    @Bean
    @ConditionalOnMissingBean(RabbitAdmin::class)
    fun getRabbitAdmin(template: RabbitTemplate): RabbitAdmin {
        val rabbitAdmin = RabbitAdmin(template)
        return rabbitAdmin
    }

    @Bean("rabbitExHandler")
    @ConditionalOnMissingBean(RabbitListenerErrorHandler::class)
    fun getRabbitHandler(): RabbitListenerErrorHandler {
        return RabbitErrorHandler(objectMapper)
    }

    @Bean("rabbitRqLoggingMessagePostProcessor")
    @ConditionalOnMissingBean(RabbitRqLoggingMessagePostProcessor::class)
    fun getRabbitRqLoggingMessagePostProcessor(properties: RabbitProperties): RabbitRqLoggingMessagePostProcessor {
        return RabbitRqLoggingMessagePostProcessor(properties.maxLogBodySize)
    }

    @Bean("rabbitRsLoggingMessagePostProcessor")
    @ConditionalOnMissingBean(RabbitRsLoggingMessagePostProcessor::class)
    fun getRabbitRsLoggingMessagePostProcessor(properties: RabbitProperties): RabbitRsLoggingMessagePostProcessor {
        return RabbitRsLoggingMessagePostProcessor(properties.maxLogBodySize)
    }

    @Bean
    @ConditionalOnMissingBean(SimpleRabbitListenerContainerFactory::class)
    fun rabbitListenerContainerFactory(
        connectionFactory: CachingConnectionFactory,
        configurer: SimpleRabbitListenerContainerFactoryConfigurer,
        rsLogger: RabbitRsLoggingMessagePostProcessor,
        rqLogger: RabbitRqLoggingMessagePostProcessor,
        errorHandler: RabbitListenerErrorHandler,
    ): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        configurer.configure(factory, connectionFactory)
        factory.setAfterReceivePostProcessors(rqLogger)
        factory.setBeforeSendReplyPostProcessors(rsLogger)
        factory.setConnectionFactory(connectionFactory)
        return factory
    }
}
