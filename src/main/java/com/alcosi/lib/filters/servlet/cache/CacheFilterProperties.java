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

package com.alcosi.lib.filters.servlet.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration properties for the CacheFilterProperties class.
 */
@ConfigurationProperties("common-lib.filter.cache")
public class CacheFilterProperties {
    /**
     * Boolean variable indicating whether the feature is disabled.
     *
     * Default value is {@code false}.
     *
     */
    private Boolean disabled = false;
    /**
     * The refresh URI is the endpoint for triggering cache refresh.
     * It is used in the CacheFilterProperties configuration class.
     *
     * The refresh URI is a string that represents the path to the cache refresh endpoint,
     * including any required query parameters.
     *
     * Example:
     * "/cache_refresh?secret=REFRESH"
     *
     * The refresh URI is typically used within the application to send a request to the cache refresh endpoint
     * and trigger the refresh operation.
     */
    private String refreshUri = "/cache_refresh?secret=REFRESH";
    /**
     * The maximum body size for a request.
     *
     * The `maxBodySize` variable represents the maximum size, in bytes, allowed for the body of a request. It is used in the `CacheFilterProperties` configuration class.
     *
     * By default, the maximum body size is set to 10,000 bytes.
     *
     * @see CacheFilterProperties
     */
    private Integer maxBodySize = 10000;
    /**
     * The clearDelay variable represents the duration of the delay before the cache is cleared.
     * It is used in the CacheFilterProperties configuration class.
     *
     * The default value for clearDelay is 1 second.
     */
    private Duration clearDelay = Duration.ofSeconds(1);
    /**
     * The order delta for the CacheFilterProperties class.
     *
     * The order delta is used to adjust the order in which the CacheFilterProperties class is applied relative
     * to other filters. A positive delta value will increase the order, while a negative delta value will
     * decrease the order. The order determines the sequence in which filters are invoked.
     *
     * The default value for the order delta is 4.
     */
    private Integer orderDelta = 4;

    public Integer getOrderDelta() {
        return orderDelta;
    }

    public void setOrderDelta(Integer orderDelta) {
        this.orderDelta = orderDelta;
    }

    public Duration getClearDelay() {
        return clearDelay;
    }

    public void setClearDelay(Duration clearDelay) {
        this.clearDelay = clearDelay;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getRefreshUri() {
        return refreshUri;
    }

    public void setRefreshUri(String refreshUri) {
        this.refreshUri = refreshUri;
    }

    public Integer getMaxBodySize() {
        return maxBodySize;
    }

    public void setMaxBodySize(Integer maxBodySize) {
        this.maxBodySize = maxBodySize;
    }
}
