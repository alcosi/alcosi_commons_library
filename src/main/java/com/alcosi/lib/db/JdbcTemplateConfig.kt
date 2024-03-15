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
