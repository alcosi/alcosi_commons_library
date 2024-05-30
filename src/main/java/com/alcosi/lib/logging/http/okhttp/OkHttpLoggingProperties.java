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

package com.alcosi.lib.logging.http.okhttp;

import com.alcosi.lib.logging.JavaLoggingLevel;
import com.alcosi.lib.logging.http.resttemplate.RestTemplateProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * The OkHttpLoggingProperties class represents the configuration properties for OkHttp logging.
 *
 * These properties can be used*/
@ConfigurationProperties("common-lib.okhttp")
public class OkHttpLoggingProperties {
    /**
     * The disabled variable represents whether a feature is disabled or not.
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
     * @see OkHttpLoggingProperties
     */
    private Boolean loggingDisabled = false;
    /**
     * The {@code contextHeadersDisabled} variable represents a boolean value indicating whether the context headers are disabled for OkHttp logging.
     *
     * By default, the context headers are enabled. When set to {@code true}, the context headers will be disabled.
     *
     * The context headers are used to store additional information about the request/response during logging, such as correlation id, user id, etc.
     *
     * Example usage:
     *
     * OkHttpLoggingProperties properties = new OkHttpLoggingProperties();
     * properties.setContextHeadersDisabled(true);
     * boolean isContextHeadersDisabled = properties.getContextHeadersDisabled();
     *
     * Note: This variable is part of the {@code OkHttpLoggingProperties} class and is initially set to {@code false}.
     *
     * Note: The {@code OkHttpLoggingProperties} class provides additional configuration properties for OkHttp logging, such as logging level, timeouts, etc.
     *
     * Note: The {@code JavaLoggingLevel} enum represents the different levels of logging in the Java logging library and is used for specifying the logging level in the {@code Ok
     * HttpLoggingProperties} class.
     */
    private Boolean contextHeadersDisabled = false;
    /**
     *
     */
    private Integer maxLogBodySize = 10000;
    /**
     * The loggingLevel variable represents the logging level for OkHttp logging.
     *
     * This variable is of type JavaLoggingLevel, which is an enum representing the different levels of logging in the Java logging library.
     *
     * The levels are ordered from highest to lowest severity: SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST.
     * The loggingLevel variable is initially set to JavaLoggingLevel.INFO.
     *
     * Usage examples:
     * - JavaLoggingLevel.SEVERE: Represents a severe error that requires immediate attention.
     * - JavaLoggingLevel.WARNING: Represents a warning that may indicate a potential problem.
     * - JavaLoggingLevel.INFO: Represents an informative message about the progress of the application.
     * - JavaLoggingLevel.CONFIG: Represents configuration information.
     * - JavaLoggingLevel.FINE: Represents fine-grained tracing information.
     * - JavaLoggingLevel.FINER: Represents even more detailed tracing information.
     * - JavaLoggingLevel.FINEST: Represents the lowest level of tracing information.
     *
     * @see OkHttpLoggingProperties
     * @see JavaLoggingLevel
     */
    private JavaLoggingLevel loggingLevel = JavaLoggingLevel.INFO;
    /**
     *
     */
    private Duration connectTimeout = Duration.ofSeconds(10);
    /**
     * The readTimeout variable represents the duration of the timeout for reading from a connection in seconds.
     * If the timeout is reached while reading from a connection, an IOException will be thrown.
     * The default value is 120 seconds.
     *
     * Example usage:
     *
     * OkHttpLoggingProperties properties = new OkHttpLoggingProperties();
     * Duration timeout = properties.getReadTimeout();
     *
     * Note: This documentation does not have an explicit author or version tag.
     */
    private Duration readTimeout = Duration.ofSeconds(120);
    /**
     * The writeTimeout variable represents the duration after which a write operation will be considered as timed out.
     *
     * The default value is 120 seconds.
     *
     * This variable is a part of the OkHttpLoggingProperties class, which represents the configuration properties for OkHttp logging.
     * You can use this variable to configure the timeout for write operations when using OkHttp.
     *
     * Example usage:
     *     OkHttpLoggingProperties okHttpLoggingProperties = new OkHttpLoggingProperties();
     *     Duration writeTimeout = okHttpLoggingProperties.getWriteTimeout();
     *     // Use the writeTimeout value in your code to set the write timeout for OkHttp requests
     *     // ...
     *     okHttpLoggingProperties.setWriteTimeout(Duration.ofSeconds(60));
     *     // Update the writeTimeout value to set a new write timeout for OkHttp requests
     *
     * Note: The writeTimeout variable is of type `Duration`, which is a class in the `java.time` package introduced in Java 8.
     * The `Duration` class represents a time-based amount of time, such as "30 seconds" or "2 minutes and 30 seconds".
     * To create a `Duration` object, you can use the `Duration.ofSeconds()` method, specifying the number of seconds as the argument.
     * You can also use other methods provided by the `Duration` class to create a `Duration` object with different units of time, such as minutes, hours, or days.
     */
    private Duration writeTimeout = Duration.ofSeconds(120);

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

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
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
