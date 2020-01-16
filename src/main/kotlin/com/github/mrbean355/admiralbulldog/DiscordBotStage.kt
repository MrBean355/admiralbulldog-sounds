package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.application.HostServices
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.reflect.KClass

private const val COLUMNS = 3

class DiscordBotStage(private val hostServices: HostServices) : Stage() {
    private val botEnabled = SimpleBooleanProperty(ConfigPersistence.isUsingDiscordBot())
    private val token = SimpleStringProperty(ConfigPersistence.getDiscordToken())
    private val toggles: Map<KClass<out SoundEvent>, BooleanProperty> = loadToggles()

    init {
        val root = VBox(PADDING_MEDIUM).apply {
            padding = Insets(PADDING_MEDIUM)
        }
        root.children += GridPane().apply {
            add(CheckBox(LABEL_ENABLE_DISCORD_BOT).apply {
                selectedProperty().bindBidirectional(botEnabled)
                selectedProperty().addListener { _, _, newValue ->
                    enableBotToggled(newValue)
                }
            }, 0, 0)
            add(Hyperlink(LINK_INVITE_BOT).apply {
                setOnAction { inviteClicked() }
                alignment = Pos.TOP_RIGHT
            }, 1, 0)
        }
        root.children += TextField().apply {
            promptText = PROMPT_DISCORD_MAGIC_NUMBER
            disableProperty().bind(botEnabled.not())
            textProperty().bindBidirectional(token)
            textProperty().addListener { _, _, newValue ->
                ConfigPersistence.setDiscordToken(newValue)
            }
        }
        root.children += Label(LABEL_PLAY_ON_DISCORD)
        root.children += GridPane().apply {
            hgap = PADDING_SMALL
            vgap = PADDING_SMALL

            var row = 0
            var col = 0
            SOUND_EVENT_TYPES.forEach {
                add(CheckBox(it.friendlyName).apply {
                    disableProperty().bind(botEnabled.not())
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
            disableProperty().bind(botEnabled.not())
            setOnAction { soundBoardClicked() }
        }
        finalise(title = TITLE_DISCORD_BOT, root = root)
    }

    private fun loadToggles(): Map<KClass<out SoundEvent>, BooleanProperty> {
        return SOUND_EVENT_TYPES.associateWith { SimpleBooleanProperty(ConfigPersistence.isPlayedThroughDiscord(it)) }
    }

    private fun enableBotToggled(enabled: Boolean) {
        ConfigPersistence.setUsingDiscordBot(enabled)
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