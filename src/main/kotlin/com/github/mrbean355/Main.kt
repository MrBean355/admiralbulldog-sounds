package com.github.mrbean355

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

//
//data class Team(
//        val name: String,
//        val players: List<TeamPlayer>
//)
//
//data class TeamPlayer(
//        val id: Int,
//        val name: String
//)

fun main(args: Array<String>) {
//    val typeAdapter = FakeArrayJsonDeserializer<Team, TeamPlayer>(TeamPlayer::class.java, "players", "player") { obj, items ->
//        obj.copy(players = items)
//    }
//    val result = GsonBuilder()
//            .registerTypeAdapter(Team::class.java, typeAdapter)
//            .create()
//            .fromJson("" +
//                    "{\n" +
//                    "  \"name\": \"Radiant\",\n" +
//                    "  \"players\": {\n" +
//                    "    \"player0\": {\n" +
//                    "      \"id\": 1,\n" +
//                    "      \"name\": \"Bean\"\n" +
//                    "    },\n" +
//                    "    \"player1\": {\n" +
//                    "      \"id\": 2,\n" +
//                    "      \"name\": \"Noob\"\n" +
//                    "    }\n" +
//                    "  }\n" +
//                    "}", Team::class.java)
//
//    println("result=$result")

    val gameStateMonitor = GameStateMonitor()
    val server = embeddedServer(Netty, port = 12345) {
        install(ContentNegotiation) {
            gson()
        }
        install(CallLogging)
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
