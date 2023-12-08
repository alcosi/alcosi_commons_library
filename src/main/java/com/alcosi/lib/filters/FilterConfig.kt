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

package com.alcosi.lib.filters

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.filter.OncePerRequestFilter

import java.util.*
import java.util.logging.Level

@Configuration
@ConditionalOnClass(OncePerRequestFilter::class)
@ConditionalOnProperty(prefix = "common-lib.filter.all",name = arrayOf("disabled"), matchIfMissing = true, havingValue = "false")
open class FilterConfig {
    @ConditionalOnProperty(prefix = "common-lib.filter.logging",name = arrayOf("disabled"), matchIfMissing = true, havingValue = "false")

    @Bean(name = ["logInternalService"], value = ["logInternalService"])
    fun logInternalService(@Value("\${common-lib.logging.level.server_logging:INFO}")  loggingLevel: String, @Value("\${common-lib.server.max.request_body.logging:10000}")  maxBodySize:Int,): LoggingFilter.LogInternalService {
        return LoggingFilter.LogInternalService(Level.parse(loggingLevel),maxBodySize)
    }
    @ConditionalOnProperty(prefix = "common-lib.filter.logging",name = arrayOf("disabled"), matchIfMissing = true, havingValue = "false")
    @Bean(name = ["loggingFilterBean"], value = ["loggingFilterBean"])
    @ConditionalOnClass(ServletWebServerFactory::class)
    fun loggingFilter(@Value("\${common-lib.logging.level.filters.base_order:-2147483648}")  baseLoggingOrder: Int,
                      @Value("\${common-lib.server.max.request_body.logging:10000}") maxBodySize: Int,
                      logInternalService: LoggingFilter.LogInternalService
    ): FilterRegistrationBean<LoggingFilter> {
        val registrationBean = FilterRegistrationBean<LoggingFilter>()
        registrationBean.filter = LoggingFilter(logInternalService, maxBodySize)
        registrationBean.order = baseLoggingOrder
        return registrationBean
    }
    @ConditionalOnClass(Scheduled::class)
    @ConditionalOnProperty(prefix = "common-lib.filter.cache",name = arrayOf("disabled"), matchIfMissing = true, havingValue = "false")
    @Bean(name = ["cachingRqRsFilterBean"], value = ["cachingRqRsFilterBean"])
    fun cachingFilter(@Value("\${common-lib.logging.level.filters.base_order:-2147483648}")  baseLoggingOrder: Int,@Value("\${common-lib.server.max.request_body.cache:10000}")  maxBodySize:Int ,@Value("\${cache.control.refresh_uri:/cache_refresh?secret=REFRESH}") refreshUri:String): FilterRegistrationBean<CachingRqRsFilter> {
        val registrationBean = FilterRegistrationBean<CachingRqRsFilter>()
        registrationBean.filter = CachingRqRsFilter(refreshUri,maxBodySize)
        registrationBean.order = baseLoggingOrder + 1
        return registrationBean
    }


    @Bean(name = ["corsFilterBean"], value = ["corsFilterBean"])
    @ConditionalOnProperty(prefix = "common-lib.filter.cors",name = arrayOf("disabled"), matchIfMissing = true, havingValue = "false")
    fun corsFilter(@Value("\${common-lib.logging.level.filters.base_order:-2147483648}")  baseLoggingOrder: Int,): FilterRegistrationBean<CorsFilter> {
        val registrationBean = FilterRegistrationBean<CorsFilter>()
        registrationBean.filter = CorsFilter()
        registrationBean.order = baseLoggingOrder + 2
        return registrationBean
    }

}