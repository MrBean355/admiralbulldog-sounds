/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.gsi

import com.github.mrbean355.admiralbulldog.AppScope
import com.github.mrbean355.admiralbulldog.log.Log
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private val JsonConfig = Json { ignoreUnknownKeys = true }

object GameStateIntegrationServer {
    private var previous: GameState? = null

    fun start() {
        AppScope.launch(Dispatchers.IO) {
            embeddedServer(Netty, 12345 /* TODO */) {
                install(ContentNegotiation) {
                    json(JsonConfig)
                }
                routing {
                    post {
                        try {
                            handle(call.receive<GameState>())
                        } catch (t: Throwable) {
                            Log.error("Exception during game state update", t)
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }.start()
        }
    }

    private fun handle(current: GameState) {
        val previous = previous
        if (previous != null) {
            // TODO: Check triggers.
        }
        this.previous = current
    }
}