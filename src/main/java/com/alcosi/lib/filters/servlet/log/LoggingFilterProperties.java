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

package com.alcosi.lib.filters.servlet.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common-lib.filter.logging")
public class LoggingFilterProperties {
    private Boolean disabled = false;
    private Integer orderDelta = 1;
    private Integer maxBodySizeLog = 10000;
    private Integer maxBodySize = 10000;


    private String loggingLevel="INFO";

    public Integer getMaxBodySizeLog() {
        return maxBodySizeLog;
    }

    public void setMaxBodySizeLog(Integer maxBodySizeLog) {
        this.maxBodySizeLog = maxBodySizeLog;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getOrderDelta() {
        return orderDelta;
    }

    public void setOrderDelta(Integer orderDelta) {
        this.orderDelta = orderDelta;
    }

    public Integer getMaxBodySize() {
        return maxBodySize;
    }

    public void setMaxBodySize(Integer maxBodySize) {
        this.maxBodySize = maxBodySize;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }
}
