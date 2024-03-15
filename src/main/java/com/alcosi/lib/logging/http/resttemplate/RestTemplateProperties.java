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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("common-lib.rest-template")
public class RestTemplateProperties {
    private Boolean disabled = false;
    private Boolean loggingDisabled = false;
    private Boolean contextHeadersDisabled = false;


    private Integer maxLogBodySize = 10000;
    private String loggingLevel ="INFO";

    private Duration connectionTimeout =Duration.ofSeconds(6);
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

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

}
