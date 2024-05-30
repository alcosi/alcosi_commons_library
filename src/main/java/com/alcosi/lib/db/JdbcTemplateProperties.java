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

package com.alcosi.lib.db;

import com.alcosi.lib.logging.JavaLoggingLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JdbcTemplate.
 */
@ConfigurationProperties("common-lib.jdbc-template")
public class JdbcTemplateProperties {
    /**
     * The maximum body size for logging.
     */
    private Integer loggingMaxBodySize = 10000;
    /**
     * The loggingQueryLevel variable represents the logging level for queries in the JdbcTemplate library.
     *
     * The logging level determines the severity of the logged queries. The higher the level, the more severe the queries that will be logged.
     *
     * Available logging levels (in order of severity):
     *     - SEVERE: Represents a severe error that requires immediate attention.
     *     - WARNING: Represents a warning that may indicate a potential problem.
     *     - INFO: Represents an informative message about the progress of the application.
     *     - CONFIG: Represents configuration information.
     *     - FINE: Represents fine-grained tracing information.
     *     - FINER: Represents even more detailed tracing information.
     *     - FINEST: Represents the lowest level of tracing information.
     *
     * By default, the loggingQueryLevel is set to INFO.
     *
     * Example usage:
     *     loggingQueryLevel = JavaLoggingLevel.WARNING;
     *
     * Note: This variable is a member of the JdbcTemplateProperties class.
     */
    private JavaLoggingLevel loggingQueryLevel = JavaLoggingLevel.INFO;
    /**
     * The loggingParametersLevel variable represents the logging level for logging parameters.
     *
     * It is of type JavaLoggingLevel, an enum representing different levels of logging in the Java logging library.
     *
     * The levels are ordered from highest to lowest severity:
     * - SEVERE
     * - WARNING
     * - INFO
     * - CONFIG
     * - FINE
     * - FINER
     * - FINEST
     *
     * Each level is associated with a corresponding Java Level object, defined in the java.util.logging.Level class.
     *
     * Usage Examples:
     *     JavaLoggingLevel.SEVERE : Represents a severe error that requires immediate attention.
     *     JavaLoggingLevel.WARNING : Represents a warning that may indicate a potential problem.
     *     JavaLoggingLevel.INFO : Represents an informative message about the progress of the application.
     *     JavaLoggingLevel.CONFIG : Represents configuration information.
     *     JavaLoggingLevel.FINE : Represents fine-grained tracing information.
     *     JavaLoggingLevel.FINER : Represents even more detailed tracing information.
     *     JavaLoggingLevel.FINEST : Represents the lowest level of tracing information.
     *
     * It is initially set to JavaLoggingLevel.INFO by default.
     *
     * Note: This variable is private and belongs to the JdbcTemplateProperties class. It is used to configure the logging level for logging parameters.
     */
    private JavaLoggingLevel loggingParametersLevel = JavaLoggingLevel.INFO;
    /**
     * The loggingResponseLevel variable represents the logging level for logging responses in Java logging library.
     *
     * The logging level determines the severity of the logged message. It indicates the importance of the message and
     * controls which messages should be logged based on their severity. The available levels are ordered from highest to lowest severity:
     * - SEVERE: Represents a severe error that requires immediate attention.
     * - WARNING: Represents a warning that may indicate a potential problem.
     * - INFO: Represents an informative message about the progress of the application.
     * - CONFIG: Represents configuration information.
     * - FINE: Represents fine-grained tracing information.
     * - FINER: Represents even more detailed tracing information.
     * - FINEST: Represents the lowest level of tracing information.
     *
     * Each level is associated with a corresponding Java Level object, defined in the java.util.logging.Level class.
     *
     * The initial value of the loggingResponseLevel variable is JavaLoggingLevel.INFO, which represents an informative message.
     *
     * Usage Examples:
     *    JavaLoggingLevel.SEVERE: Log severe errors that require immediate attention.
     *    JavaLoggingLevel.WARNING: Log warnings that may indicate potential problems.
     *    JavaLoggingLevel.INFO: Log informative messages about the progress of the application.
     *    JavaLoggingLevel.CONFIG: Log configuration information.
     *    JavaLoggingLevel.FINE: Log fine-grained tracing information.
     *    JavaLoggingLevel.FINER: Log even more detailed tracing information.
     *    JavaLoggingLevel.FINEST: Log the lowest level of tracing information.
     *
     * Note: This variable is defined in the JdbcTemplateProperties class.
     */
    private JavaLoggingLevel loggingResponseLevel = JavaLoggingLevel.INFO;
    /**
     * The `loggingWarningLevel` variable represents the logging level for warning messages in the Java logging library.
     * Warning messages indicate potential problems in the application.
     *
     * The `JavaLoggingLevel` enum is used to specify the different levels of logging in the Java logging library.
     * The levels are ordered from highest to lowest severity:
     * - SEVERE
     * - WARNING
     * - INFO
     * - CONFIG
     * - FINE
     * - FINER
     * - FINEST
     * Each level is associated with a corresponding Java Level object, defined in the java.util.logging.Level class.
     *
     * The default value for `loggingWarningLevel` is `JavaLoggingLevel.INFO`.
     *
     * Usage Example:
     * ```java
     * JavaLoggingLevel level = JavaLoggingLevel.WARNING;
     * // Set the logging level to WARNING
     * loggingWarningLevel = level;
     * ```
     *
     * Note: The `loggingWarningLevel` variable is used in the `JdbcTemplateProperties` class, which is a configuration
     * properties class for JdbcTemplate.
     */
    private JavaLoggingLevel loggingWarningLevel = JavaLoggingLevel.INFO;
    /**
     * The "disabled" variable represents a boolean value indicating whether a certain functionality is disabled or not.
     * In this case, it is used in the context of configuring the JdbcTemplate in the "common-lib" library.
     * The default value for this variable is "false".
     *
     * Example usage:
     *
     * JdbcTemplateProperties properties = new JdbcTemplateProperties();
     * properties.setDisabled(true); // Disable the functionality
     *
     * Note: This documentation does not include example code, @author tags, or @version tags.
     */
    private Boolean disabled = false;

    public Integer getLoggingMaxBodySize() {
        return loggingMaxBodySize;
    }

    public void setLoggingMaxBodySize(Integer loggingMaxBodySize) {
        this.loggingMaxBodySize = loggingMaxBodySize;
    }

    public JavaLoggingLevel getLoggingQueryLevel() {
        return loggingQueryLevel;
    }

    public void setLoggingQueryLevel(JavaLoggingLevel loggingQueryLevel) {
        this.loggingQueryLevel = loggingQueryLevel;
    }

    public JavaLoggingLevel getLoggingParametersLevel() {
        return loggingParametersLevel;
    }

    public void setLoggingParametersLevel(JavaLoggingLevel loggingParametersLevel) {
        this.loggingParametersLevel = loggingParametersLevel;
    }

    public JavaLoggingLevel getLoggingResponseLevel() {
        return loggingResponseLevel;
    }

    public void setLoggingResponseLevel(JavaLoggingLevel loggingResponseLevel) {
        this.loggingResponseLevel = loggingResponseLevel;
    }

    public JavaLoggingLevel getLoggingWarningLevel() {
        return loggingWarningLevel;
    }

    public void setLoggingWarningLevel(JavaLoggingLevel loggingWarningLevel) {
        this.loggingWarningLevel = loggingWarningLevel;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
