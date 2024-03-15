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
