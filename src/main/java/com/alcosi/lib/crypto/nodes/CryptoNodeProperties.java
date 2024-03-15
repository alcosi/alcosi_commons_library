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


