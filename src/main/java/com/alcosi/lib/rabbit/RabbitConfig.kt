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


/**
 * RabbitConfig is a configuration class for RabbitMQ.
 **/
@ConditionalOnClass(value = [RabbitAdmin::class, SimpleRabbitListenerContainerFactory::class])
@ConditionalOnProperty(prefix = "common-lib.rabbit", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(RabbitProperties::class)
class RabbitConfig {
    /**
     * Returns an instance of RabbitAdmin using the provided RabbitTemplate.
     *
     * @param template The RabbitTemplate instance to be used by RabbitAdmin.
     * @return The RabbitAdmin instance.
     */
    @Bean
    @ConditionalOnMissingBean(RabbitAdmin::class)
    fun getRabbitAdmin(template: RabbitTemplate): RabbitAdmin {
        val rabbitAdmin = RabbitAdmin(template)
        return rabbitAdmin
    }

    /**
     * Returns an instance of RabbitListenerErrorHandler.
     *
     */
    @Bean("rabbitExHandler")
    @ConditionalOnMissingBean(RabbitListenerErrorHandler::class)
    fun getRabbitHandler( objectMapper: ObjectMapper): RabbitListenerErrorHandler {
        return RabbitErrorHandler(objectMapper)
    }

    /**
     * getRabbitRqLoggingMessagePostProcessor method returns an instance of RabbitRqLoggingMessagePostProcessor with the specified maxBodySize*/
    @Bean("rabbitRqLoggingMessagePostProcessor")
    @ConditionalOnMissingBean(RabbitRqLoggingMessagePostProcessor::class)
    fun getRabbitRqLoggingMessagePostProcessor(properties: RabbitProperties): RabbitRqLoggingMessagePostProcessor {
        return RabbitRqLoggingMessagePostProcessor(properties.loggingLevel.javaLevel,properties.maxLogBodySize)
    }

    /**
     * getRabbitRsLoggingMessagePostProcessor is a method that returns an instance of RabbitRsLoggingMessagePostProcessor.
     * This*/
    @Bean("rabbitRsLoggingMessagePostProcessor")
    @ConditionalOnMissingBean(RabbitRsLoggingMessagePostProcessor::class)
    fun getRabbitRsLoggingMessagePostProcessor(properties: RabbitProperties): RabbitRsLoggingMessagePostProcessor {
        return RabbitRsLoggingMessagePostProcessor(properties.loggingLevel.javaLevel,properties.maxLogBodySize)
    }

    /**
     * Creates a SimpleRabbitListenerContainerFactory bean if a SimpleRabbitListenerContainerFactory bean is not already present.
     *
     * @param connectionFactory The CachingConnectionFactory instance to be used by the SimpleRabbitListenerContainerFactory.
     * @param configurer The SimpleRabbitListenerContainerFactoryConfigurer instance used to configure the factory.
     * @param rsLogger The RabbitRsLoggingMessagePostProcessor instance used for post-processing received messages.
     * @param rqLogger The RabbitRqLoggingMessagePostProcessor instance used for pre-processing sent messages.
     * @param errorHandler The RabbitListenerErrorHandler instance used to handle listener errors.
     * @return A SimpleRabbitListenerContainerFactory instance.
     */
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
