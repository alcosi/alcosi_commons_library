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

package com.alcosi.lib.crypto.nodes;

import com.alcosi.lib.logging.JavaLoggingLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * Configuration class for CryptoNodeProperties.
 */
@ConfigurationProperties("common-lib.crypto.node")
public class CryptoNodeProperties {
    public Map<Integer, String> getUrl() {
        return url;
    }

    public void setUrl(Map<Integer, String> url) {
        this.url = url;
    }

    public Duration getPoolingInterval() {
        return poolingInterval;
    }

    public void setPoolingInterval(Duration poolingInterval) {
        this.poolingInterval = poolingInterval;
    }

    public Duration getBalancerTimeout() {
        return balancerTimeout;
    }

    public void setBalancerTimeout(Duration balancerTimeout) {
        this.balancerTimeout = balancerTimeout;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public JavaLoggingLevel getNodesLoggingLevel() {
        return nodesLoggingLevel;
    }

    public void setNodesLoggingLevel(JavaLoggingLevel nodesLoggingLevel) {
        this.nodesLoggingLevel = nodesLoggingLevel;
    }

    public Integer getNodesLoggingMaxBody() {
        return nodesLoggingMaxBody;
    }

    public void setNodesLoggingMaxBody(Integer nodesLoggingMaxBody) {
        this.nodesLoggingMaxBody = nodesLoggingMaxBody;
    }

    public Duration getNodesTimeout() {
        return nodesTimeout;
    }

    public void setNodesTimeout(Duration nodesTimeout) {
        this.nodesTimeout = nodesTimeout;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }
    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * This private variable `url` is a Map that stores URL information.
     *
     * The keys of the Map are integers, and the values are strings that represent the URLs.
     *
     * Example usage:
     *     url.put(1, "https://www.example.com");
     *     url.put(2, "https://www.google.com");
     *
     * The `url` variable is used in the class `CryptoNodeProperties`, which is a subclass of `Object`.
     *
     * This variable is associated with other fields and methods in the `CryptoNodeProperties` class.
     *
     * Note: This documentation does not have an explicit author or version tag.
     */
    private Map<Integer, String> url;
    /**
     * The poolingInterval variable defines the duration between each poolin process.
     * By default, it is set to 15 seconds.
     */
    private Duration poolingInterval = Duration.ofSeconds(15);
    /**
     * The `balancerTimeout` variable represents the duration of timeout for the balancer in seconds.
     * It is an instance of the `Duration` class from the `java.time` package.
     *
     * The default value for `balancerTimeout` is 10 seconds.
     *
     * This variable is a private member of the `CryptoNodeProperties` class.
     *
     * Possible usage examples:
     *  - Setting the balancer timeout to 5 seconds:
     *    ```
     *    CryptoNodeProperties properties = new CryptoNodeProperties();
     *    properties.setBalancerTimeout(Duration.ofSeconds(5));
     *    ```
     *  - Getting the current balancer timeout value:
     *    ```
     *    CryptoNodeProperties properties = new CryptoNodeProperties();
     *    Duration timeout = properties.getBalancerTimeout();
     *    ```
     *
     * Note: This class does not have an explicit author or version tag.
     */
    private Duration balancerTimeout = Duration.ofSeconds(10);
    /**
     * The "threads" variable represents the number of threads used in a certain context.
     * It is an Integer type variable with an initial value of 20.
     *
     * This variable is private in the class "CryptoNodeProperties".
     *
     * Usage Example:
     *
     * // Get the value of the "threads" variable
     * Integer numThreads = cryptoNodeProperties.getThreads();
     *
     * // Set the value of the "threads" variable
     * cryptoNodeProperties.setThreads(10);
     */
    private Integer threads = 20;
    /**
     * The private variable nodesLoggingLevel of type JavaLoggingLevel represents the logging level for the nodes.
     * It is used to control the verbosity of log messages related to the nodes in the application.
     *
     * The available logging levels defined in the JavaLoggingLevel enum are:
     * - SEVERE: Represents a severe error that requires immediate attention.
     * - WARNING: Represents a warning that may indicate a potential problem.
     * - INFO: Represents an informative message about the progress of the application.
     * - CONFIG: Represents configuration information.
     * - FINE: Represents fine-grained tracing information.
     * - FINER: Represents even more detailed tracing information.
     * - FINEST: Represents the lowest level of tracing information.
     *
     * The default value for nodesLoggingLevel is JavaLoggingLevel.INFO.
     *
     * Note: The JavaLoggingLevel enum has an associated Java Level object defined in the java.util.logging.Level class.
     *
     * This variable is declared inside the CryptoNodeProperties class, which is a part of the containing class hierarchy.
     *
     * @see CryptoNodeProperties#getNodesLoggingLevel()
     * @see CryptoNodeProperties#setNodesLoggingLevel(JavaLoggingLevel)
     * @see JavaLoggingLevel
     * @see java.util.logging.Level
     */
    private JavaLoggingLevel nodesLoggingLevel = JavaLoggingLevel.INFO;
    /**
     * The maximum number of nodes to log the body details for.
     *
     * Default value: 10000
     */
    private Integer nodesLoggingMaxBody = 10000;
    /**
     * The nodesTimeout variable represents the duration of the timeout for nodes in seconds.
     * After the timeout period has elapsed, the nodes will be considered inactive.
     *
     * Default value: 15 seconds
     *
     * Example usage:
     * CryptoNodeProperties cryptoNodeProperties = new CryptoNodeProperties();
     * Duration timeout = cryptoNodeProperties.getNodesTimeout();
     *
     * Note: This documentation does not include example code, author, or version information.
     */
    private Duration nodesTimeout = Duration.ofSeconds(15);
    /**
     * The Health class represents the health configuration settings.
     *
     * It contains the following properties:
     * nodesLoggingLevel - The logging level for nodes (default: "FINEST")
     * refreshTimeout - The timeout duration for refreshing health (default: 10 seconds)
     * checkDelay - The delay duration for health checks (default: 60 seconds)
     * firstDelay - The initial delay duration for health checks (default: 1 second)
     * threads - The number of threads for health checks (default: 20)
     *
     * Usage Examples:
     *     Health health = new Health();
     *     health.setNodesLoggingLevel("FINE"); // Set nodes logging level to FINE
     *     Duration refreshTimeout = health.getRefreshTimeout(); // Get the refresh timeout duration
     *     health.setCheckDelay(Duration.ofSeconds(30)); // Set the check delay to 30 seconds
     *
     * Note: This class does not have an explicit author or version tag.
     */
    private Health health = new Health();
    /**
     * Represents the state of whether a functionality or feature is disabled.
     *
     * This variable is used to determine if a particular functionality or feature is disabled.
     * If the value is `true`, it means the functionality is disabled. If the value is `false`, the functionality is enabled.
     *
     * By default, the `disabled` variable is initialized with a value of `false`.
     *
     * Example usage:
     *
     * CryptoNodeProperties cryptoNodeProperties = new CryptoNodeProperties();
     * cryptoNodeProperties.setDisabled(true);
     * boolean isDisabled = cryptoNodeProperties.getDisabled(); // returns true
     *
     * Note: This documentation does not have an explicit author or version tag.
     */
    private Boolean disabled =false;

    /**
     * The Health class is responsible for managing health-related configurations and settings.
     * It provides methods to get and set various properties such as logging level, timeouts, and thread count.
     */
    public static class Health{
        /**
         * Represents the logging level for node logging in the application.
         */
        private JavaLoggingLevel nodesLoggingLevel = JavaLoggingLevel.FINEST;
        /**
         * The refreshTimeout variable represents the duration interval between refreshing health-related configurations and settings.
         * It is defined in the Health class and holds a value of type Duration.
         * The default value is 10 seconds.
         *
         * The refreshTimeout value is used along with other properties in the Health class to manage health-related configurations and settings.
         * It determines how frequently the configurations and settings are refreshed or updated.
         * A shorter refreshTimeout interval may result in more frequent updates but increased resource usage, while a longer interval may reduce resource usage but delay updates
         * .
         */
        private Duration refreshTimeout = Duration.ofSeconds(10);
        /**
         * The delay between health checks, specified as a duration.
         *
         * The check delay determines the interval between consecutive health checks.
         *
         * The default check delay is 60 seconds.
         */
        private Duration checkDelay = Duration.ofSeconds(60);
        /**
         * The firstDelay variable represents the duration of the initial delay in the Health class.
         *
         * The initial delay is used to determine the amount of time to wait before executing the first health check.
         *
         * Default Value: 1 second
         *
         * Example Usage:
         *     Duration initialDelay = health.getFirstDelay();
         */
        private Duration firstDelay = Duration.ofSeconds(1);
        /**
         * The threads variable represents the number of threads to be used for a specific operation.
         * It is an Integer type variable and has a default value of 20.
         *
         * This variable is a member of the Health class, which is responsible for managing health-related configurations and settings.
         * The Health class provides methods to get and set various properties such as logging level, timeouts, and thread count.
         *
         * Usage Example:
         *     Health health = new Health();
         *     health.setThreads(10);
         *     Integer threadCount = health.getThreads(); // threadCount will be 10
         *
         * Note: The threads variable is private, so its value should be accessed or modified using the appropriate getter and setter methods provided by the Health class.
         */
        private Integer threads =20;
        public Duration getFirstDelay() {
            return firstDelay;
        }

        public void setFirstDelay(Duration firstDelay) {
            this.firstDelay = firstDelay;
        }
        public Duration getCheckDelay() {
            return checkDelay;
        }

        public void setCheckDelay(Duration checkDelay) {
            this.checkDelay = checkDelay;
        }

        public Duration getRefreshTimeout() {
            return refreshTimeout;
        }

        public void setRefreshTimeout(Duration refreshTimeout) {
            this.refreshTimeout = refreshTimeout;
        }

        public Integer getThreads() {
            return threads;
        }

        public void setThreads(Integer threads) {
            this.threads = threads;
        }

        public JavaLoggingLevel getNodesLoggingLevel() {
            return nodesLoggingLevel;
        }

        public void setNodesLoggingLevel(JavaLoggingLevel nodesLoggingLevel) {
            this.nodesLoggingLevel = nodesLoggingLevel;
        }


    }
}


