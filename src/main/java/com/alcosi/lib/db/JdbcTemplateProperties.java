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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("common-lib.jdbc-template")
public class JdbcTemplateProperties {
    private Integer loggingMaxBodySize = 10000;
    private String loggingQueryLevel = "INFO";
    private String loggingParametersLevel = "INFO";
    private String loggingResponseLevel = "INFO";
    private String loggingWarningLevel = "INFO";
    private Boolean disabled = false;

    public Integer getLoggingMaxBodySize() {
        return loggingMaxBodySize;
    }

    public void setLoggingMaxBodySize(Integer loggingMaxBodySize) {
        this.loggingMaxBodySize = loggingMaxBodySize;
    }

    public String getLoggingQueryLevel() {
        return loggingQueryLevel;
    }

    public void setLoggingQueryLevel(String loggingQueryLevel) {
        this.loggingQueryLevel = loggingQueryLevel;
    }

    public String getLoggingParametersLevel() {
        return loggingParametersLevel;
    }

    public void setLoggingParametersLevel(String loggingParametersLevel) {
        this.loggingParametersLevel = loggingParametersLevel;
    }

    public String getLoggingResponseLevel() {
        return loggingResponseLevel;
    }

    public void setLoggingResponseLevel(String loggingResponseLevel) {
        this.loggingResponseLevel = loggingResponseLevel;
    }

    public String getLoggingWarningLevel() {
        return loggingWarningLevel;
    }

    public void setLoggingWarningLevel(String loggingWarningLevel) {
        this.loggingWarningLevel = loggingWarningLevel;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
