/*
 * Copyright (c) 2024  Alcosi Group Ltd. and affiliates.
 *
 * Portions of this software are licensed as follows:
 *
 *     All content that resides under the "alcosi" and "atomicon" or “deploy” directories of this repository, if that directory exists, is licensed under the license defined in "LICENSE.TXT".
 *
 *     All third-party components incorporated into this software are licensed under the original license provided by the owner of the applicable component.
 *
 *     Content outside of the above-mentioned directories or restrictions above is available under the MIT license as defined below.
 *
 *
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is urnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
