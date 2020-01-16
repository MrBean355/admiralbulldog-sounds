package com.github.mrbean355.admiralbulldog.debug

import javafx.application.Application
import javafx.application.Application.launch
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.system.exitProcess

fun main() {
    launch(DebugApplication::class.java)
}

class DebugApplication : Application() {

    override fun start(primaryStage: Stage) {
        val root = VBox(8.0).apply {
            padding = Insets(16.0)
            children += Label().apply {
                text = "OS: ${System.getProperty("os.name")} (${System.getProperty("os.version")}), ${System.getProperty("os.arch")}"
            }
            children += Label().apply {
                text = "Java version: ${System.getProperty("java.version")}"
            }
            children += Label()
            children += Label().apply {
                testService("Connecting to AWS...") {
                    awsService.hasLaterVersion("1.0.0")
                }
            }
            children += Label().apply {
                testService("Connecting to PlaySounds...") {
                    playSoundsService.getHtml()
                }
            }
            children += Label().apply {
                testService("Connecting to Nuuls...") {
                    nuulsService.get("8_9Et.mp3")
                }
            }
        }

        primaryStage.scene = Scene(root)
        primaryStage.width = 450.0
        primaryStage.title = "AdmiralBulldog Sounds - Debug"
        primaryStage.icons.add(Image(DebugApplication::class.java.classLoader.getResourceAsStream("weirdchamp.png")))
        primaryStage.show()
        primaryStage.setOnCloseRequest {
            exitProcess(0)
        }
    }
}
