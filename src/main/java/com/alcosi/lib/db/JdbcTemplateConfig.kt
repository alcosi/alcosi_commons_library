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

package com.alcosi.lib.db

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowCallbackHandler
import org.springframework.jdbc.core.simple.JdbcClient
import java.util.logging.Level
import javax.sql.DataSource

@ConditionalOnClass(JdbcTemplate::class)
@ConditionalOnProperty(prefix = "common-lib.jdbc-template", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(JdbcProperties::class, JdbcTemplateProperties::class)
class JdbcTemplateConfig {
    @Bean
    fun namedParameterJdbcTemplate(
        jdbcTemplate: JdbcTemplate,
        jdbcTemplateProperties: JdbcTemplateProperties,
    ): LoggingNamedParameterJdbcTemplate {
        return LoggingNamedParameterJdbcTemplate(
            jdbcTemplateProperties.loggingMaxBodySize,
            jdbcTemplate,
            Level.parse(jdbcTemplateProperties.loggingQueryLevel),
            Level.parse(jdbcTemplateProperties.loggingParametersLevel),
        )
    }

    @Bean
    fun jdbcLoggingWarningCallBackHandler(jdbcTemplateProperties: JdbcTemplateProperties): LoggingWarningRowCallbackHandler {
        return LoggingWarningRowCallbackHandler(Level.parse(jdbcTemplateProperties.loggingWarningLevel))
    }

    @Bean
    fun jdbcLoggingResponseCallBackHandler(jdbcTemplateProperties: JdbcTemplateProperties): LoggingResponseRowCallbackHandler {
        return LoggingResponseRowCallbackHandler(
            jdbcTemplateProperties.loggingMaxBodySize,
            Level.parse(jdbcTemplateProperties.loggingResponseLevel),
        )
    }

    @Bean
    fun jdbcTemplate(
        callbacks: List<RowCallbackHandler>,
        dataSource: DataSource,
        properties: JdbcProperties,
    ): JdbcTemplate {
        val jdbcTemplate = LoggingJdbcTemplate(callbacks, dataSource)
        val template: JdbcProperties.Template = properties.template
        jdbcTemplate.fetchSize = template.fetchSize
        jdbcTemplate.maxRows = template.maxRows
        if (template.queryTimeout != null) {
            jdbcTemplate.queryTimeout = template.queryTimeout.seconds.toInt()
        }
        return jdbcTemplate
    }

    @Bean
    @ConditionalOnClass(JdbcClient::class)
    fun JdbcClient(template: LoggingNamedParameterJdbcTemplate): JdbcClient {
        return JdbcClient.create(template)
    }
}
