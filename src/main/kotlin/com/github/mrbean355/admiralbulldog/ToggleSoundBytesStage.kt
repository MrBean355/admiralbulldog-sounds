package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.MAX_VOLUME
import com.github.mrbean355.admiralbulldog.persistence.MIN_VOLUME
import com.github.mrbean355.admiralbulldog.service.logAnalyticsEvent
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.util.Duration
import kotlin.reflect.KClass

class ToggleSoundBytesStage : Stage() {
    private val toggles: Map<KClass<out SoundByte>, BooleanProperty> = loadToggles()

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

        SOUND_BYTE_TYPES.forEachIndexed { i, type ->
            val checkBox = CheckBox(type.friendlyName()).apply {
                tooltip = Tooltip(type.description()).apply {
                    showDelay = Duration.ZERO
                }
                selectedProperty().bindBidirectional(toggles[type])
                selectedProperty().addListener { _, _, newValue ->
                    ConfigPersistence.toggleSoundByte(type, newValue)
                }
            }
            root.add(checkBox, 0, i + 2)
            root.add(Button("", ImageView(settingsIcon())).apply {
                disableProperty().bind(checkBox.selectedProperty().not())
                setOnAction { configureClicked(type) }
            }, 1, i + 2)
        }

        title = TITLE_TOGGLE_SOUND_BYTES
        scene = Scene(root)
        icons.add(bulldogIcon())
        width = WINDOW_WIDTH
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    private fun loadToggles(): Map<KClass<out SoundByte>, BooleanProperty> {
        return SOUND_BYTE_TYPES.associateWith {
            SimpleBooleanProperty(ConfigPersistence.isSoundByteEnabled(it))
        }
    }

    private fun configureClicked(type: KClass<out SoundByte>) {
        logAnalyticsEvent(eventType = "configure_sound", eventData = type.simpleName.orEmpty().ifEmpty { "unknown" })
        ChooseSoundFilesStage(type).apply {
            initModality(Modality.WINDOW_MODAL)
            initOwner(this@ToggleSoundBytesStage)
            show()
        }
    }
}