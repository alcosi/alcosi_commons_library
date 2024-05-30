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

package com.alcosi.lib.logging.http.resttemplate;

import com.alcosi.lib.logging.JavaLoggingLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * The RestTemplateProperties class represents the configuration properties for RestTemplate.
 * It is annotated with @ConfigurationProperties to indicate that the properties are bound to this class.
 */
@ConfigurationProperties("common-lib.rest-template")
public class RestTemplateProperties {
    /**
     * Represents whether the functionality is disabled or not.
     */
    private Boolean disabled = false;
    /**
     * Represents whether the functionality of logging is disabled or not.
     *
     * The value of this variable determines whether logging should be enabled or disabled.
     * If it is set to true, logging functionality will be disabled. If it is set to false, logging
     * functionality will be enabled.
     *
     * By default, the value of this variable is set to false, indicating that logging is enabled.
     *
     * This variable is used within the RestTemplateProperties class to configure the logging
     * behavior for RestTemplate.
     *
     * @see RestTemplateProperties
     */
    private Boolean loggingDisabled = false;
    /**
     * Represents whether the context headers functionality is disabled or not.
     *
     * The value of this variable determines whether the context headers functionality should be enabled or disabled.
     * If it is set to true, the context headers functionality will be disabled. If it is set to false, the context headers
     * functionality will be enabled.
     *
     * By default, the value of this variable is set to false, indicating*/
    private Boolean contextHeadersDisabled = false;
    /**
     * The maxLogBodySize variable represents the maximum size of the log body.
     *
     * The value of this variable is an Integer representing the maximum size of the log body.
     * By default, it is set to 10000, which indicates a maximum size of 10000 bytes.
     *
     * Example usage:
     *     Integer maxSize = maxLogBodySize;
     *
     * This variable is defined in the RestTemplateProperties class, which also contains other fields
     * and methods related to the RestTemplate configuration.
     *
     * Note: The RestTemplateProperties class does not have an explicit author or version tag.
     */
    private Integer maxLogBodySize = 10000;
    /**
     * The loggingLevel variable represents the current logging level for the application.
     *
     * The available levels are defined in the JavaLoggingLevel enum, which represents the different levels of logging in the Java logging library.
     * The levels are ordered from highest to lowest severity: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST.
     *
     * The loggingLevel variable is of type JavaLoggingLevel and its default value is INFO.
     *
     * Example usage:
     *
     * JavaLoggingLevel loggingLevel = JavaLoggingLevel.WARNING;
     *
     * This sets the logging level to WARNING, which represents a warning that may indicate a potential problem.
     *
     * Note: The JavaLoggingLevel class does not require an author or version tag.
     */
    private JavaLoggingLevel loggingLevel =JavaLoggingLevel.INFO;
    /**
     * The connectionTimeout variable represents the duration of the connection timeout in seconds.
     *
     * The default value is 6 seconds.
     *
     * Usage Example:
     *     Duration connectionTimeout = Duration.ofSeconds(6);
     *
     * Note: This documentation does not contain example code, author, or version tags.
     */
    private Duration connectionTimeout =Duration.ofSeconds(6);
    /**
     * The readTimeout variable represents the duration for which the reading of a request or response from the server is allowed to take place. If the reading process exceeds this
     *  duration, a timeout exception will be thrown.
     *
     * By default, the readTimeout is set to 60 seconds (1 minute).
     */
    private Duration readTimeout =Duration.ofSeconds(60);

    public Boolean getLoggingDisabled() {
        return loggingDisabled;
    }

    public void setLoggingDisabled(Boolean loggingDisabled) {
        this.loggingDisabled = loggingDisabled;
    }

    public Boolean getContextHeadersDisabled() {
        return contextHeadersDisabled;
    }

    public void setContextHeadersDisabled(Boolean contextHeadersDisabled) {
        this.contextHeadersDisabled = contextHeadersDisabled;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getMaxLogBodySize() {
        return maxLogBodySize;
    }

    public void setMaxLogBodySize(Integer maxLogBodySize) {
        this.maxLogBodySize = maxLogBodySize;
    }

    public JavaLoggingLevel getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(JavaLoggingLevel loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}
