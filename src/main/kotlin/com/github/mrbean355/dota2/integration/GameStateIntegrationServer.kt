package com.github.mrbean355.dota2.integration

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/** Receives game state updates from Dota 2. */
class GameStateIntegrationServer(port: Int, private val processGameState: (GameState) -> Unit) {
    private val applicationEngine: ApplicationEngine

    init {
        applicationEngine = embeddedServer(Netty, port) {
            install(ContentNegotiation) {
                gson()
            }
            routing {
                post {
                    try {
                        processGameState(call.receive())
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

    fun start() {
        applicationEngine.start(wait = false)
    }
}