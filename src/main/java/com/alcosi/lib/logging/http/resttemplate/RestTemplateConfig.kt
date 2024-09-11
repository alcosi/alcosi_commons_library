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

package com.alcosi.lib.logging.http.resttemplate

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.logging.http.OrderedComparator
import io.github.breninsul.rest.logging.RestTemplateLoggerConfiguration
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

/** This class configures the RestTemplate for making HTTP requests. */
@AutoConfiguration
@AutoConfigureBefore(RestClientAutoConfiguration::class, RestTemplateAutoConfiguration::class)
@EnableConfigurationProperties(RestTemplateProperties::class)
@ConditionalOnProperty(prefix = "common-lib.rest-template", name = ["enabled"], matchIfMissing = true, havingValue = "true")
class RestTemplateConfig {
    /**
     * Retrieves the `RestTemplateLogRequestResponseFilter` instance.
     *
     * @param properties The properties for configuring the filter.
     * @param headerHelper The HeaderHelper instance used by the filter.
     * @return The RestTemplateLogRequestResponseFilter instance.
     */
    @Bean
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplateLogRequestResponseFilter::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.rest-template.logging", name = ["enabled"], matchIfMissing = true, havingValue = "false")
    fun getLogRequestResponseFilter(
        properties: RestTemplateProperties,
        headerHelper: HeaderHelper,
    ): RestTemplateLogRequestResponseFilter {
        val config = RestTemplateLoggerConfiguration()
        val requestMaskers =
            listOf(
                config.restTemplateRequestRegexJsonBodyMasking(properties.logging.request.mask),
                config.restTemplateRequestFormUrlencodedBodyMasking(properties.logging.request.mask),
            )
        val responseMaskers =
            listOf(
                config.restTemplateResponseRegexJsonBodyMasking(properties.logging.request.mask),
                config.restTemplateResponseFormUrlencodedBodyMasking(properties.logging.request.mask),
            )
        val uriMaskers = listOf(config.restTemplateUriMasking(properties.logging.request.mask))
        return RestTemplateLogRequestResponseFilter(properties.logging, uriMaskers, requestMaskers, responseMaskers, headerHelper)
    }

    /**
     * Retrieves the `RestTemplateContextHeadersFilter` instance.
     *
     * @param headerHelper The HeaderHelper instance used by the filter.
     * @return The RestTemplateContextHeadersFilter instance.
     */
    @Bean
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplateContextHeadersFilter::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.rest-template", name = ["context-headers-disabled"], matchIfMissing = true, havingValue = "false")
    fun getRestTemplateContextFilter(headerHelper: HeaderHelper): RestTemplateContextHeadersFilter = RestTemplateContextHeadersFilter(headerHelper, 0)

    /**
     * Retrieves a SimpleClientHttpRequestFactory instance with configured
     * connection and read timeouts.
     *
     * @param properties The properties used to configure the
     *     SimpleClientHttpRequestFactory.
     * @return The SimpleClientHttpRequestFactory instance.
     */
    @Bean("clientHttpRequestFactory")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(ClientHttpRequestFactory::class)
    fun getSimpleClientHttpRequestFactory(properties: RestTemplateProperties): ClientHttpRequestFactory {
        val simpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        simpleClientHttpRequestFactory.setConnectTimeout(properties.connectTimeout.toMillis().toInt())
        simpleClientHttpRequestFactory.setReadTimeout(properties.readTimeout.toMillis().toInt())
        val factory = BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory)
        return factory
    }

    /**
     * Retrieves the RestTemplateBuilder instance with configured filters,
     * request factory, and configurer.
     *
     * @param filters Provider of ClientHttpRequestInterceptor filters.
     * @param factory ClientHttpRequestFactory instance.
     * @param configurer Provider of RestTemplateBuilderConfigurer instances.
     * @return The RestTemplateBuilder instance.
     */
    @Bean("restTemplateBuilder")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplate::class)
    fun getRestTemplateBuilder(
        filters: ObjectProvider<ClientHttpRequestInterceptor>,
        factory: ClientHttpRequestFactory,
        configurer: ObjectProvider<RestTemplateBuilderConfigurer>,
    ): RestTemplateBuilder {
        val filtersList =
            filters.toList().sortedWith(OrderedComparator)
        val builder = RestTemplateBuilder().requestFactory { _ -> factory }.interceptors(filtersList)
        builder.interceptors(filtersList)
        configurer.stream().forEach { it.configure(builder) }
        return builder
    }

    /**
     * Retrieves the RestTemplate instance.
     *
     * @param builder The RestTemplateBuilder instance used to build the
     *     RestTemplate.
     * @return The RestTemplate instance.
     */
    @Bean("restTemplate")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplate::class)
    fun getRestTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()

    /**
     * Retrieves the RestClient instance.
     *
     * @param filters The ObjectProvider of ClientHttpRequestInterceptor
     *     filters.
     * @param configurer The ObjectProvider of RestClientBuilderConfigurer.
     * @param factory The ClientHttpRequestFactory for creating
     *     ClientHttpRequests.
     * @return The RestClient instance.
     */
    @Bean("restClient")
    @ConditionalOnClass(RestClient::class)
    @ConditionalOnMissingBean(RestClient::class)
    fun getRestClient(
        filters: ObjectProvider<ClientHttpRequestInterceptor>,
        configurer: ObjectProvider<RestClientBuilderConfigurer>,
        factory: ClientHttpRequestFactory,
    ): RestClient {
        val builder = RestClient.builder()
        builder.requestFactory(factory)
        val filtersList = filters.toList()
        filtersList.forEach {
            builder.requestInterceptor(it)
        }
        val configurers = configurer.toList()
        configurers.forEach { it.configure(builder) }
        val client =
            builder
                .build()
        return client
    }
}
