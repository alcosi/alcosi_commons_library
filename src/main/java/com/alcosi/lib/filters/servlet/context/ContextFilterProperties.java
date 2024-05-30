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

package com.alcosi.lib.filters.servlet.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class represents the configuration properties for the ContextFilter.
 */
@ConfigurationProperties("common-lib.filter.context")
public class ContextFilterProperties {
    /**
     * Represents whether the functionality associated with the variable is disabled or not.
     * By default, the functionality is enabled (value is false).
     */
    private Boolean disabled = false;
    /**
     * Represents the difference in order for the ContextFilter.
     * The order delta determines the relative order of the ContextFilter compared to other filters in the application.
     * A lower value means the filter will be executed earlier, and a higher value means it will be executed later.
     * The default value is 1.
     */
    private Integer orderDelta = 1;
    /**
     * The headers configuration for the context filter.
     */
    private Headers headers = new Headers();

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public Integer getOrderDelta() {
        return orderDelta;
    }

    public void setOrderDelta(Integer orderDelta) {
        this.orderDelta = orderDelta;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
    public static class Headers{
        private String userAgent="User-Agent";
        private String ip="X-Real-IP";
        private String platform="X-Platform";

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }

}
