package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.map
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.application.HostServices
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.reflect.KClass

private const val COLUMNS = 3

class DiscordBotStage(private val hostServices: HostServices) : Stage() {
    private val viewModel = DiscordBotViewModel()
    private val toggles: Map<KClass<out SoundEvent>, BooleanProperty> = loadToggles()

    init {
        val root = VBox(PADDING_MEDIUM).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += GridPane().apply {
            add(CheckBox(LABEL_ENABLE_DISCORD_BOT).apply {
                selectedProperty().bindBidirectional(viewModel.botEnabled)
            }, 0, 0)
            add(Hyperlink(LINK_INVITE_BOT).apply {
                setOnAction { inviteClicked() }
                alignment = Pos.TOP_RIGHT
            }, 1, 0)
        }
        root.children += TextField().apply {
            promptText = PROMPT_DISCORD_MAGIC_NUMBER
            disableProperty().bind(viewModel.botEnabled.not())
            textProperty().bindBidirectional(viewModel.token)
        }
        root.children += Label().apply {
            textProperty().bind(viewModel.status)
            graphicProperty().bind(viewModel.statusImage.map {
                it?.let { ImageView(Image(it.image)) }
            })
        }
        root.children += Label(LABEL_PLAY_ON_DISCORD)
        root.children += GridPane().apply {
            hgap = PADDING_SMALL
            vgap = PADDING_SMALL

            var row = 0
            var col = 0
            SOUND_EVENT_TYPES.forEach {
                add(CheckBox(it.friendlyName).apply {
                    disableProperty().bind(viewModel.botEnabled.not())
                    selectedProperty().bindBidirectional(toggles[it])
                    selectedProperty().addListener { _, _, _ ->
                        soundEventToggled()
                    }
                }, col++, row)
                if (col == COLUMNS) {
                    col = 0
                    ++row
                }
            }
        }
        root.children += Button(ACTION_SOUND_BOARD).apply {
            disableProperty().bind(viewModel.botEnabled.not())
            setOnAction { soundBoardClicked() }
        }
        finalise(title = TITLE_DISCORD_BOT, root = root)
        setOnHiding {
            viewModel.onClose()
        }

        viewModel.init()
    }

    private fun loadToggles(): Map<KClass<out SoundEvent>, BooleanProperty> {
        return SOUND_EVENT_TYPES.associateWith { SimpleBooleanProperty(ConfigPersistence.isPlayedThroughDiscord(it)) }
    }

    private fun inviteClicked() {
        logAnalyticsEvent(eventType = "button_click", eventData = "invite_bot_to_server")
        hostServices.showDocument(URL_DISCORD_BOT_INVITE)
    }

    private fun soundEventToggled() {
        toggles.forEach { (key, value) ->
            ConfigPersistence.setPlayedThroughDiscord(key, value.value)
        }
    }

    private fun soundBoardClicked() {
        SoundBoardStage().showModal(owner = this)
    }
}