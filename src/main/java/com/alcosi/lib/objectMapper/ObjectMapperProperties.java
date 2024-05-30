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

/**
 * Configuration properties for ObjectMapper settings in the common-lib library.
 */
@ConfigurationProperties("common-lib.object-mapper")
public class ObjectMapperProperties{
    /**
     * Represents configuration properties for String settings.
     */
    private StringSettings string = new StringSettings();
    /**
     * Flag indicating whether the variable is disabled.
     *
     * The default value is {@code false}.
     */
    private Boolean disabled = false;
    /**
     * Represents the mapping future for the ObjectMapperProperties class.
     * The mappingFuture variable is a private Map that maps MapperFeature enum values to Boolean values.
     *
     * Available Features:
     * - ACCEPT_CASE_INSENSITIVE_ENUMS: Indicates whether the ObjectMapper should accept case-insensitive enums.
     *
     * Example Usage:
     *  // Creating a new mappingFuture map with ACCEPT_CASE_INSENSITIVE_ENUMS set to true
     *  Map<MapperFeature,Boolean> mappingFuture = Map.of(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS , true);
     *
     * Class: ObjectMapperProperties
     * Fields: String disabled, mappingFuture, serializationFeature, deserializationFeature, parserFeature
     * Methods: StringSettings getString(), void setString(StringSettings string), Boolean getDisabled(),
     *          void setDisabled(Boolean disabled), Map<MapperFeature, Boolean> getMappingFuture(),
     *          void setMappingFuture(Map<MapperFeature, Boolean> mappingFuture),
     *          Map<SerializationFeature, Boolean> getSerializationFeature(),
     *          void setSerializationFeature(Map<SerializationFeature, Boolean> serializationFeature),
     *          Map<DeserializationFeature, Boolean> getDeserializationFeature(),
     *          void setDeserializationFeature(Map<DeserializationFeature, Boolean> deserializationFeature),
     *          Map<JsonParser.Feature, Boolean> getParserFeature(),
     *          void setParserFeature(Map<JsonParser.Feature, Boolean> parserFeature)
     *
     * Superclasses: java.lang.Object
     *
     * Inner Class: StringSettings
     * Fields: Integer maxStringLength, maxNestingDepth, maxNumberLength
     * Methods: Integer getMaxStringLength(), void setMaxStringLength(Integer maxStringLength),
     *          Integer getMaxNestingDepth(), void setMaxNestingDepth(Integer maxNestingDepth),
     *          Integer getMaxNumberLength(), void setMaxNumberLength(Integer maxNumberLength)
     */
    private Map<MapperFeature,Boolean> mappingFuture = Map.of(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS , true);
    /**
     * This variable represents the serialization features for an ObjectMapper.
     * It is a map that associates each SerializationFeature enum value with a boolean value indicating whether the feature is enabled or disabled.
     * The SerializationFeature enum provides various features related to serialization in Jackson library.
     *
     * In the containing class ObjectMapperProperties, the serializationFeature variable is declared as a private field and initially assigned an empty map using the Map.of() method
     * .
     * To access and modify the serialization features, getter and setter methods are provided in the ObjectMapperProperties class.
     *
     * The serialization features can be retrieved using the getSerializationFeature() method and modified using the setSerializationFeature() method.
     * The getSerializationFeature() method returns the map of SerializationFeature enum values and the corresponding boolean values.
     * The setSerializationFeature() method takes a map of SerializationFeature enum values and the corresponding boolean values as a parameter and sets it as the serialization features
     * .
     *
     * The map key is an enum value from the SerializationFeature enum, which provides the following serialization features:
     * - WRITE_DATES_AS_TIMESTAMPS: Controls whether to write dates as timestamps.
     * - WRITE_DATE_KEYS_AS_TIMESTAMPS: Controls whether to write date keys as timestamps.
     * - WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS: Controls whether to write char arrays as JSON arrays.
     * - WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED: Controls whether to write single-element arrays unwrapped.
     * - WRITE_ENUMS_USING_TO_STRING: Controls whether to write enums using their toString() method.
     * - WRITE_NULL_MAP_VALUES: Controls whether to write null map values.
     * - WRITE_EMPTY_JSON_ARRAYS: Controls whether to write empty JSON arrays.
     * - WRITE_EMPTY_JSON_OBJECTS: Controls whether to write empty JSON objects.
     * - WRITE_BIGDECIMAL_AS_PLAIN: Controls whether to write BigDecimal values as plain strings.
     * - FLUSH_AFTER_WRITE_VALUE: Controls whether to flush the JsonGenerator after writing a value.
     * - SORT_PROPERTIES_ALPHABETICALLY: Controls whether to sort object properties alphabetically.
     * - USE_EQUALITY_FOR_OBJECT_ID: Controls whether to serialize ObjectId objects using their equality.
     * - USE_BASE_TYPE_AS_DEFAULT_IMPL: Controls whether to serialize polymorphic base types using their default implementation.
     * - INDENT_OUTPUT: Controls whether to indent the output.
     * - ORDER_MAP_ENTRIES_BY_KEYS: Controls whether to order map entries by keys.
     * - STRICT_DUPLICATE_DETECTION: Controls whether strict duplicate detection is enabled.
     * - ACCEPT_SINGLE_VALUE_AS_ARRAY: Controls whether a single value is accepted as an array.
     *
     * The boolean value associated with each SerializationFeature enum value determines whether the feature is enabled or disabled.
     * If the value is true, the feature is enabled. If the value is false, the feature is disabled.
     *
     * Example usage:
     *
     * // Create a new instance of ObjectMapperProperties
     * ObjectMapperProperties properties = new ObjectMapperProperties();
     *
     * // Enable the WRITE_DATES_AS_TIMESTAMPS feature
     * properties.setSerializationFeature(Map.of(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true));
     *
     * // Disable the WRITE_EMPTY_JSON_ARRAYS feature
     * properties.setSerializationFeature(Map.of(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false));
     */
    private Map<SerializationFeature,Boolean> serializationFeature = Map.of();
    /**
     * Represents the deserialization features of the ObjectMapperProperties class.
     * It is a map that maps DeserializationFeature objects to boolean values.
     * This map is used to specify the deserialization behavior of the ObjectMapper.
     * Each DeserializationFeature represents a specific feature or behavior that can be enabled or disabled.
     * The boolean value indicates whether the feature is enabled or disabled.
     *
     * The deserializationFeature variable is an instance of java.util.Map<DeserializationFeature,Boolean>.
     * It is initialized with an empty map using the Map.of() factory method.
     *
     * Example usage:
     * ```
     * ObjectMapperProperties properties = new ObjectMapperProperties();
     * properties.getDeserializationFeature().put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
     * ```
     *
     * Note: The DeserializationFeature enum is not provided in the given information.
     * The possible values for the DeserializationFeature enum would determine the available features in the map.
     */
    private Map<DeserializationFeature,Boolean> deserializationFeature = Map.of();
    /**
     * The parserFeature variable is a private Map that stores the enabled or disabled state of different JSON parser features.
     * The keys of the map are JsonParser.Feature enums, and the values are Boolean indicating whether the feature is enabled or disabled.
     *
     * Note: The initial value of the parserFeature map is an empty map.
     *
     * Containing class: ObjectMapperProperties
     * Class fields: string, disabled, mappingFuture, serializationFeature, deserializationFeature, parserFeature
     * Class methods: getString(), setString(StringSettings), getDisabled(), setDisabled(Boolean), getMappingFuture(), setMappingFuture(Map<MapperFeature, Boolean>),
     *                getSerializationFeature(), setSerializationFeature(Map<SerializationFeature, Boolean>), getDeserializationFeature(),
     *                setDeserializationFeature(Map<DeserializationFeature, Boolean>), getParserFeature(), setParserFeature(Map<JsonParser.Feature, Boolean>)
     * Superclasses: java.lang.Object
     *
     * StringSettings declaration:
     * public static class StringSettings {
     *     public Integer getMaxStringLength() {
     *         return maxStringLength;
     *     }
     *
     *     public void setMaxStringLength(Integer maxStringLength) {
     *         this.maxStringLength = maxStringLength;
     *     }
     *
     *     public Integer getMaxNestingDepth() {
     *         return maxNestingDepth;
     *     }
     *
     *     public void setMaxNestingDepth(Integer maxNestingDepth) {
     *         this.maxNestingDepth = maxNestingDepth;
     *     }
     *
     *     public Integer getMaxNumberLength() {
     *         return maxNumberLength;
     *     }
     *
     *     public void setMaxNumberLength(Integer maxNumberLength) {
     *         this.maxNumberLength = maxNumberLength;
     *     }
     *
     *     private Integer maxStringLength = 20000000;
     *     private Integer maxNestingDepth = 1000;
     *     private Integer maxNumberLength = 1000;
     * }
     */
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

        /**
         * The maximum allowed length for a string.
         *
         * This variable represents the maximum length allowed for a string. It is used in various parts of the code to enforce length restrictions
         * and prevent memory issues.
         *
         * The default value is 20000000.
         */
        private Integer maxStringLength = 20000000;
        /**
         * The maximum allowed nesting depth.
         *
         * This variable represents the maximum allowed nesting depth in the code. It is used in various parts of the code to enforce restrictions
         * on how deeply nested code blocks can be.
         *
         * The default value is 1000.
         *
         * @see*/
        private Integer maxNestingDepth = 1000;
        /**
         * The maximum allowed length for a number.
         *
         * This variable represents the maximum length allowed for a number. It is used in various parts of the code to enforce length restrictions
         * and prevent memory issues.
         *
         * The default value is 1000.
         */
        private Integer maxNumberLength = 1000;
    }
}
