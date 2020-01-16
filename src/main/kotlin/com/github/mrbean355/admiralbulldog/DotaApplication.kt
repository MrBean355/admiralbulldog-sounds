package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage
import kotlin.system.exitProcess

class DotaApplication : Application() {

    override fun init() {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
    }

    override fun start(primaryStage: Stage) {
        val viewModel = HomeViewModel(primaryStage, hostServices)
        val root = VBox(PADDING_SMALL).apply {
            padding = Insets(PADDING_MEDIUM)
            alignment = Pos.CENTER
        }
        root.children += Label().apply {
            font = Font(TEXT_SIZE_LARGE)
            textProperty().bind(viewModel.heading)
        }
        root.children += ProgressBar().apply {
            prefWidthProperty().bind(root.widthProperty())
            visibleProperty().bind(viewModel.progressBarVisible)
            managedProperty().bind(visibleProperty())
        }
        root.children += Label().apply {
            textProperty().bind(viewModel.infoMessage)
        }
        root.children += HBox(PADDING_SMALL).apply {
            alignment = Pos.CENTER
            children += Button(getString("btn_change_sounds")).apply {
                setOnAction { viewModel.onChangeSoundsClicked() }
            }
            children += Button(getString("btn_discord_bot")).apply {
                setOnAction { viewModel.onDiscordBotClicked() }
            }
            children += Button(getString("btn_dota_mod")).apply {
                setOnAction { viewModel.onDotaModClicked() }
            }
        }
        root.children += HBox(PADDING_SMALL).apply {
            alignment = Pos.CENTER
            children += Hyperlink(getString("btn_discord_community")).apply {
                setOnAction { viewModel.onDiscordCommunityClicked() }
            }
            children += Hyperlink(getString("btn_project_website")).apply {
                setOnAction { viewModel.onProjectWebsiteClicked() }
            }
        }
        root.children += Label().apply {
            font = Font(TEXT_SIZE_SMALL)
            textProperty().bind(viewModel.version)
        }

        primaryStage.finalise(
                title = getString("title_app"),
                root = root,
                closeOnEscape = false,
                onCloseRequest = EventHandler {
                    exitProcess(0)
                }
        )
        viewModel.init()
    }
}