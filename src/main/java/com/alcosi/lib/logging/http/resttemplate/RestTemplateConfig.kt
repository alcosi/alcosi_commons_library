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

package com.alcosi.lib.logging.http.resttemplate

import com.alcosi.lib.filters.servlet.HeaderHelper
import com.alcosi.lib.logging.http.OrderedComparator
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
import java.util.logging.Level

@AutoConfiguration
@AutoConfigureBefore(RestClientAutoConfiguration::class, RestTemplateAutoConfiguration::class)
@EnableConfigurationProperties(RestTemplateProperties::class)
@ConditionalOnProperty(prefix = "common-lib.rest-template", name = ["disabled"], matchIfMissing = true, havingValue = "false")
class RestTemplateConfig {
    @Bean
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplateLogRequestResponseFilter::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.rest-template", name = ["logging-disabled"], matchIfMissing = true, havingValue = "false")
    fun getLogRequestResponseFilter(
        properties: RestTemplateProperties,
        headerHelper: HeaderHelper,
    ): RestTemplateLogRequestResponseFilter {
        return RestTemplateLogRequestResponseFilter(properties.maxLogBodySize, Level.parse(properties.loggingLevel), headerHelper, 1)
    }

    @Bean
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplateContextHeadersFilter::class)
    @ConditionalOnBean(HeaderHelper::class)
    @ConditionalOnProperty(prefix = "common-lib.rest-template", name = ["context-headers-disabled"], matchIfMissing = true, havingValue = "false")
    fun getRestTemplateContextFilter(headerHelper: HeaderHelper): RestTemplateContextHeadersFilter {
        return RestTemplateContextHeadersFilter(headerHelper, 0)
    }

    @Bean("clientHttpRequestFactory")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(ClientHttpRequestFactory::class)
    fun getSimpleClientHttpRequestFactory(properties: RestTemplateProperties): ClientHttpRequestFactory {
        val simpleClientHttpRequestFactory = SimpleClientHttpRequestFactory()
        simpleClientHttpRequestFactory.setConnectTimeout(properties.connectionTimeout.toMillis().toInt())
        simpleClientHttpRequestFactory.setReadTimeout(properties.readTimeout.toMillis().toInt())
        val factory = BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory)
        return factory
    }

    @Bean("restTemplateBuilder")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplate::class)
    fun getRestTemplateBuilder(
        filters: ObjectProvider<ClientHttpRequestInterceptor>,
        factory: ClientHttpRequestFactory,
        configurer: ObjectProvider<RestTemplateBuilderConfigurer>,
    ): RestTemplateBuilder {
        val builder = RestTemplateBuilder().requestFactory { -> factory }
        val filtersList =
            filters.toList().sortedWith(OrderedComparator)
        builder.interceptors(filtersList)
        configurer.stream().forEach { it.configure(builder) }
        return builder
    }

    @Bean("restTemplate")
    @ConditionalOnClass(RestTemplate::class)
    @ConditionalOnMissingBean(RestTemplate::class)
    fun getRestTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

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
