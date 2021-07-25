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

import com.github.mrbean355.admiralbulldog.log.Log
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.BiDiBodyLens
import org.http4k.server.Netty
import org.http4k.server.asServer

private val gameStateLens: BiDiBodyLens<GameState> = Body.auto<GameState>().toLens()

object GameStateIntegrationServer {
    private var previous: GameState? = null

    fun start() {
        val app = { request: Request ->
            try {
                handle(gameStateLens(request))
            } catch (t: Throwable) {
                Log.error("Error receiving GSI request", t)
            }
            Response(Status.OK)
        }
        app.asServer(Netty(12345)).start()
    }

    private fun handle(current: GameState) {
        val previous = previous
        if (previous != null) {
            // TODO: Check triggers.
        }
        this.previous = current
    }
}