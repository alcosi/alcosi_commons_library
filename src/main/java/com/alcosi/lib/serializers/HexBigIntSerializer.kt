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

package com.alcosi.lib.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.math.BigInteger

/**
 * This class is a serializer for converting BigInteger objects to hexadecimal strings.
 *
 * @param T The type of objects that this serializer can serialize. In this case, it is BigInteger.
 * @property value The BigInteger object to serialize.
 * @property gen The JsonGenerator used for writing JSON content.
 * @property provider The SerializerProvider used for accessing serialization context.
 */
open class HexBigIntSerializer : StdSerializer<BigInteger>(BigInteger::class.java) {
    /**
     * Serializes a BigInteger object to a hexadecimal string representation.
     *
     * @param value The BigInteger object to serialize. It can be null.
     * @param gen The JsonGenerator used for writing JSON content.
     * @param provider The SerializerProvider used for accessing serialization context.
     */
    override fun serialize(
        value: BigInteger?,
        gen: JsonGenerator,
        provider: SerializerProvider,
    ) {
        if (value == null) {
            gen.writeNull()
        } else {
            gen.writeString("0x${value.toString(16)}")
        }
    }
}
