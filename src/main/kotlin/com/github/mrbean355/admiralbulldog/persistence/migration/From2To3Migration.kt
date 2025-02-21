/*
 * Copyright 2024 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.persistence.migration

import com.google.gson.JsonObject
import java.util.concurrent.TimeUnit

class From2To3Migration : Migration(from = 2, to = 3) {

    override fun migrate(config: JsonObject) {
        val feedbackCompleted = config["feedbackCompleted"]?.asJsonPrimitive?.asLong ?: 0
        config.remove("feedbackCompleted")
        config["nextFeedback"] = feedbackCompleted + TimeUnit.DAYS.toMillis(90)
    }
}