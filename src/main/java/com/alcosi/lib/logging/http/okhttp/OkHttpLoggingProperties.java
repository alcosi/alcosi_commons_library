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

import io.github.breninsul.okhttp.logging.OkHttpLoggerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * The OkHttpLoggingProperties class represents the configuration properties for OkHttp logging.
 *
 * These properties can be used*/
@ConfigurationProperties("common-lib.okhttp")
public class OkHttpLoggingProperties {
    private Boolean enabled = true;
    private Boolean contextHeadersDisabled = false;
    private Duration connectTimeout = Duration.ofSeconds(10);
    private Duration readTimeout = Duration.ofSeconds(120);
    private Duration writeTimeout = Duration.ofSeconds(120);
    private OkHttpLoggerProperties logging= new OkHttpLoggerProperties();

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public OkHttpLoggerProperties getLogging() {
        return logging;
    }

    public void setLogging(OkHttpLoggerProperties logging) {
        this.logging = logging;
    }
}
