package com.github.mrbean355.admiralbulldog.gsi

import com.github.mrbean355.admiralbulldog.game.GameState
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
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("GsiServer")

@Suppress("FunctionName")
fun GsiServer(onNewGameState: (GameState) -> Unit): ApplicationEngine {
    // TODO: Configurable port.
    return embeddedServer(Netty, 12345) {
        install(ContentNegotiation) {
            gson()
        }
        routing {
            post {
                try {
                    onNewGameState(call.receive())
                } catch (t: Throwable) {
                    logger.error("Error receiving game state", t)
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}