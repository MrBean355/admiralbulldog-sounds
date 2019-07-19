package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.gamestate.*
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

fun main() {
    val toggles = togglePlugins()
    val gameStateMonitor = GameStateMonitor(toggles)
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

private val ALL_LISTENERS = setOf(
        DeathGameStateListener::class.java,
        HealGameStateListener::class.java,
        KillGameStateListener::class.java,
        MatchEndGameStateListener::class.java,
        MidasGameStateListener::class.java,
        PeriodicGameStateListener::class.java,
        RunesGameStateListener::class.java,
        SmokeOfDeceitGameStateListener::class.java)

fun togglePlugins(): List<GameStateListener> {
    println("Which plugins to enable?")
    val plugins = mutableListOf<GameStateListener>()
    ALL_LISTENERS.forEach { `class` ->
        print("${`class`.simpleName.removeSuffix("GameStateListener")}? (Y/n) ")
        val input = readLine()
        if (input.isNullOrBlank() || input.equals("y", ignoreCase = true)) {
            plugins += `class`.newInstance()
        }
    }
    return plugins
}