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
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

private const val PORT = 12345

fun main(args: Array<String>) {
    val gameStateMonitor = GameStateMonitor()
    val server = embeddedServer(Netty, port = PORT) {
        install(ContentNegotiation) {
            gson()
        }
        routing {
            post {
                try {
                    gameStateMonitor.onUpdate(call.receive())
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
                call.respond(HttpStatusCode.OK)
            }
        }
    }
    server.start(wait = true)
}
