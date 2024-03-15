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

package com.alcosi.lib.objectMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
@ConfigurationProperties("common-lib.object-mapper")
public class ObjectMapperProperties{
    private StringSettings string = new StringSettings();
    private Boolean disabled = false;
    private Map<MapperFeature,Boolean> mappingFuture = Map.of(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS , true);
    private Map<SerializationFeature,Boolean> serializationFeature = Map.of();
    private Map<DeserializationFeature,Boolean> deserializationFeature = Map.of();
    private Map<JsonParser.Feature,Boolean> parserFeature = Map.of();

    public StringSettings getString() {
        return string;
    }

    public void setString(StringSettings string) {
        this.string = string;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Map<MapperFeature, Boolean> getMappingFuture() {
        return mappingFuture;
    }

    public void setMappingFuture(Map<MapperFeature, Boolean> mappingFuture) {
        this.mappingFuture = mappingFuture;
    }

    public Map<SerializationFeature, Boolean> getSerializationFeature() {
        return serializationFeature;
    }

    public void setSerializationFeature(Map<SerializationFeature, Boolean> serializationFeature) {
        this.serializationFeature = serializationFeature;
    }

    public Map<DeserializationFeature, Boolean> getDeserializationFeature() {
        return deserializationFeature;
    }

    public void setDeserializationFeature(Map<DeserializationFeature, Boolean> deserializationFeature) {
        this.deserializationFeature = deserializationFeature;
    }

    public Map<JsonParser.Feature, Boolean> getParserFeature() {
        return parserFeature;
    }

    public void setParserFeature(Map<JsonParser.Feature, Boolean> parserFeature) {
        this.parserFeature = parserFeature;
    }

    public static class StringSettings {
        public Integer getMaxStringLength() {
            return maxStringLength;
        }

        public void setMaxStringLength(Integer maxStringLength) {
            this.maxStringLength = maxStringLength;
        }

        public Integer getMaxNestingDepth() {
            return maxNestingDepth;
        }

        public void setMaxNestingDepth(Integer maxNestingDepth) {
            this.maxNestingDepth = maxNestingDepth;
        }

        public Integer getMaxNumberLength() {
            return maxNumberLength;
        }

        public void setMaxNumberLength(Integer maxNumberLength) {
            this.maxNumberLength = maxNumberLength;
        }

        private Integer maxStringLength = 20000000;
        private Integer maxNestingDepth = 1000;
        private Integer maxNumberLength = 1000;
    }
}
