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

package com.alcosi.lib.crypto.nodes;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

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

    public String getNodesLoggingLevel() {
        return nodesLoggingLevel;
    }

    public void setNodesLoggingLevel(String nodesLoggingLevel) {
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
    private Map<Integer, String> url;
    private Duration poolingInterval = Duration.ofSeconds(15);
    private Duration balancerTimeout = Duration.ofSeconds(10);
    private Integer threads = 20;
    private String nodesLoggingLevel = "INFO";
    private Integer nodesLoggingMaxBody = 10000;

    private Duration nodesTimeout = Duration.ofSeconds(15);
    private Health health = new Health();
    private Boolean disabled =false;

    public static class Health{
        private String nodesLoggingLevel = "FINEST";
        private Duration refreshTimeout = Duration.ofSeconds(10);
        private Duration checkDelay = Duration.ofSeconds(60);

        private Integer threads =20;

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

        public String getNodesLoggingLevel() {
            return nodesLoggingLevel;
        }

        public void setNodesLoggingLevel(String nodesLoggingLevel) {
            this.nodesLoggingLevel = nodesLoggingLevel;
        }


    }
}


