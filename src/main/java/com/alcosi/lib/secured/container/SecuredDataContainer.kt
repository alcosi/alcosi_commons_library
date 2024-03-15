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

package com.alcosi.lib.secured.container

import com.alcosi.lib.serializers.SecuredDataContainerDeSerializer
import com.alcosi.lib.serializers.SecuredDataContainerSerializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = SecuredDataContainerSerializer::class)
@JsonDeserialize(using = SecuredDataContainerDeSerializer::class)
interface SecuredDataContainer<T> {
    val className: String
    val originalLength: Int
    val encrypted: ByteArray

    fun decoded(key: ByteArray): T
}
