package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.events.SOUND_EVENT_TYPES
import com.github.mrbean355.admiralbulldog.events.SoundEvent
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import com.github.mrbean355.admiralbulldog.ui.finalise
import com.github.mrbean355.admiralbulldog.ui.showModal
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import kotlin.reflect.KClass

class ToggleSoundEventsStage : Stage() {
    private val toggles: Map<KClass<out SoundEvent>, BooleanProperty> = loadToggles()

    init {
        val root = GridPane()
        root.hgap = PADDING_SMALL
        root.vgap = PADDING_SMALL
        root.padding = Insets(PADDING_MEDIUM)
        root.columnConstraints.addAll(ColumnConstraints().apply {
            hgrow = Priority.ALWAYS
        })
        root.add(Label(LABEL_VOLUME), 0, 0, 2, 1)
        root.add(Slider(MIN_VOLUME, MAX_VOLUME, ConfigPersistence.getVolume()).apply {
            isShowTickLabels = true
            isShowTickMarks = true
            majorTickUnit = VOLUME_MAJOR_TICK_UNIT
            minorTickCount = VOLUME_MINOR_TICK_COUNT
            isSnapToTicks = true
            valueProperty().addListener { _, _, newValue ->
                ConfigPersistence.setVolume(newValue.toDouble())
            }
        }, 0, 1, 2, 1)
        SOUND_EVENT_TYPES.forEachIndexed { i, type ->
            val checkBox = CheckBox(type.friendlyName).apply {
                tooltip = Tooltip(type.description)
                selectedProperty().bindBidirectional(toggles[type])
                selectedProperty().addListener { _, _, newValue ->
                    ConfigPersistence.toggleSoundEvent(type, newValue)
                }
            }
            root.add(checkBox, 0, i + 2)
            root.add(Button("", ImageView(settingsIcon())).apply {
                disableProperty().bind(checkBox.selectedProperty().not())
                setOnAction { configureClicked(type) }
            }, 1, i + 2)
        }
        finalise(title = TITLE_TOGGLE_SOUND_EVENTS, root = root)
        width = WINDOW_WIDTH
    }

    private fun loadToggles(): Map<KClass<out SoundEvent>, BooleanProperty> {
        return SOUND_EVENT_TYPES.associateWith {
            SimpleBooleanProperty(ConfigPersistence.isSoundEventEnabled(it))
        }
    }

    private fun configureClicked(type: KClass<out SoundEvent>) {
        logAnalyticsEvent(eventType = "configure_sound", eventData = type.simpleName.orEmpty().ifEmpty { "unknown" })
        ChooseSoundFilesStage(type).showModal(owner = this)
    }
}