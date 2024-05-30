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

/**
 * Configures and creates beans related to the JdbcTemplate functionality.
 *
 * This class is conditionally enabled based on the presence of the JdbcTemplate class and the value of the "common-lib.jdbc-template.disabled" property. It is auto-configured and
 *  enables the use of JdbcProperties and JdbcTemplateProperties for configuration.
 */
@ConditionalOnClass(JdbcTemplate::class)
@ConditionalOnProperty(prefix = "common-lib.jdbc-template", name = ["disabled"], matchIfMissing = true, havingValue = "false")
@AutoConfiguration
@EnableConfigurationProperties(JdbcProperties::class, JdbcTemplateProperties::class)
class JdbcTemplateConfig {
    /**
     * Creates an instance of LoggingNamedParameterJdbcTemplate.
     *
     * @param jdbcTemplate The JdbcTemplate instance to be used.
     * @param*/
    @Bean
    fun namedParameterJdbcTemplate(
        jdbcTemplate: JdbcTemplate,
        jdbcTemplateProperties: JdbcTemplateProperties,
    ): LoggingNamedParameterJdbcTemplate {
        return LoggingNamedParameterJdbcTemplate(
            jdbcTemplateProperties.loggingMaxBodySize,
            jdbcTemplate,
            jdbcTemplateProperties.loggingQueryLevel.javaLevel,
            jdbcTemplateProperties.loggingParametersLevel.javaLevel,
        )
    }

    /**
     * Creates an instance of LoggingWarningRowCallbackHandler.
     *
     * @param jdbcTemplateProperties The JdbcTemplateProperties instance to retrieve the logging warning level.
     * @return The LoggingWarningRowCallbackHandler instance.
     */
    @Bean
    fun jdbcLoggingWarningCallBackHandler(jdbcTemplateProperties: JdbcTemplateProperties): LoggingWarningRowCallbackHandler {
        return LoggingWarningRowCallbackHandler(jdbcTemplateProperties.loggingWarningLevel.javaLevel)
    }

    /**
     * Creates an instance of [LoggingResponseRowCallbackHandler].
     *
     * @param jdbcTemplateProperties The [JdbcTemplateProperties] instance to retrieve the logging max body size and response level.
     * @return The created [LoggingResponseRowCallbackHandler] instance.
     */
    @Bean
    fun jdbcLoggingResponseCallBackHandler(jdbcTemplateProperties: JdbcTemplateProperties): LoggingResponseRowCallbackHandler {
        return LoggingResponseRowCallbackHandler(
            jdbcTemplateProperties.loggingMaxBodySize,
            jdbcTemplateProperties.loggingResponseLevel.javaLevel,
        )
    }

    /**
     * Creates an instance of JdbcTemplate.
     *
     * @param callbacks The list of RowCallbackHandlers to be applied to the ResultSet.
     * @param dataSource The DataSource instance to be used.
     * @param properties The JdbcProperties instance to retrieve the template configuration.
     * @return The created JdbcTemplate instance.
     */
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

    /**
     * Creates an instance of JdbcClient.
     *
     * @param template The LoggingNamedParameterJdbcTemplate instance to be used.
     * @return The created JdbcClient instance.
     */
    @Bean
    @ConditionalOnClass(JdbcClient::class)
    fun JdbcClient(template: LoggingNamedParameterJdbcTemplate): JdbcClient {
        return JdbcClient.create(template)
    }
}
