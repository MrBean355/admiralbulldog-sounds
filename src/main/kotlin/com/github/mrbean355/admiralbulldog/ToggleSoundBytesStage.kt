package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.bytes.SOUND_BYTE_TYPES
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import kotlin.reflect.KClass

class ToggleSoundBytesStage : Stage() {
    private val toggles: Map<KClass<out SoundByte>, BooleanProperty> = loadToggles()

    init {
        val root = GridPane()
        root.hgap = 8.0
        root.vgap = 8.0
        root.padding = Insets(16.0)

        SOUND_BYTE_TYPES.forEachIndexed { i, type ->
            val checkBox = CheckBox(type.simpleName).apply {
                selectedProperty().bindBidirectional(toggles[type])
            }
            root.add(checkBox, 0, i)
            root.add(Button("", ImageView("settings_black.png")).apply {
                disableProperty().bind(checkBox.selectedProperty().not())
                setOnAction { configureClicked(type) }
            }, 1, i)
        }
        root.add(Button("Save").apply {
            setOnAction { saveClicked() }
        }, 0, root.children.size / 2)

        title = "Configure Sounds"
        scene = Scene(root)
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
        ChooseSoundFilesStage(type).show()
    }

    private fun saveClicked() {
        toggles.forEach { (t, u) ->
            ConfigPersistence.toggleSoundByte(t, u.value)
        }
        close()
    }
}