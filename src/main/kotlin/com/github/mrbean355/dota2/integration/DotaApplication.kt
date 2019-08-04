package com.github.mrbean355.dota2.integration

import com.github.mrbean355.dota2.integration.assets.SoundFileRegistry
import javafx.application.Application
import javafx.application.Application.launch
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import kotlin.system.exitProcess

fun main() {
    launch(DotaApplication::class.java)
}

class DotaApplication : Application() {

    override fun init() {
        SoundFileRegistry.initialise()
    }

    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.classLoader.getResource("layout/application.fxml"))
        val parent = loader.load<Parent>()
        loader.getController<MainController>().hostServices = hostServices
        primaryStage.apply {
            scene = Scene(parent)
            icons.add(Image(javaClass.classLoader.getResourceAsStream("bulldog.jpg")))
            title = "Dota 2 Integration"
            isResizable = false
            setOnCloseRequest { exitProcess(0) }
            show()
        }
    }
}