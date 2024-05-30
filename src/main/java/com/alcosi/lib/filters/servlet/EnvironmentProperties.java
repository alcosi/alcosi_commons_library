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

package com.alcosi.lib.filters.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The EnvironmentProperties class represents the properties related to the environment in which the application is running.
 * The properties can be configured using the @ConfigurationProperties annotation.
 */
@ConfigurationProperties("spring.application")
public class EnvironmentProperties {
    /**
     * The name field represents the name of the environment.
     * It is a private variable in the EnvironmentProperties class.
     * The default value of the name field is "dev".
     */
    private String name="dev";
    /**
     * The Environment variable represents the current environment in which the application is running.
     * The environment indicates whether the application is running in development, test, or production.
     */
    private String environment="dev";

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
