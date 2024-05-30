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

package com.alcosi.lib.doc;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This class represents the properties configuration for OpenAPI in the common-lib library.
 * It provides access to various configurable properties related to OpenAPI.
 */
@ConfigurationProperties("common-lib.openapi")
public class OpenAPIProperties {
    /**
     * Represents the disabled status of a feature.
     *
     * <p>
     * The disabled status indicates whether the feature is enabled or disabled.
     * If the value is {@code true}, the feature is disabled. If the value is
     * {@code false} (default), the*/
    private Boolean disabled= false;
    /**
     * Represents the web path for the OpenAPI API endpoint.
     */
    private String apiWebPath= "/openapi/{fileName}";
    /**
     * This variable represents the file path of the OpenAPI YAML file.
     * The default value is "openapi.yaml".
     *
     * <p>
     * The file path is used to locate the OpenAPI YAML file for generating
     * the API documentation. It specifies the location of the file within
     * the file system.
     *
     */
    private String filePath= "openapi.yaml";

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getApiWebPath() {
        return apiWebPath;
    }

    public void setApiWebPath(String apiWebPath) {
        this.apiWebPath = apiWebPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
