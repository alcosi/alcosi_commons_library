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

@ConfigurationProperties("common-lib.openapi")
public class OpenAPIProperties {
    private Boolean disabled= false;
    private String apiWebPath= "/openapi/{fileName}";
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
