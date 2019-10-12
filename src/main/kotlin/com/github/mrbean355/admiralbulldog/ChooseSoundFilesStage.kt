package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.assets.SoundFile
import com.github.mrbean355.admiralbulldog.assets.SoundFiles
import com.github.mrbean355.admiralbulldog.bytes.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlin.reflect.KClass

class ChooseSoundFilesStage(private val type: KClass<out SoundByte>) : Stage() {
    private val playThroughDiscord = SimpleBooleanProperty(ConfigPersistence.isPlayedThroughDiscord(type))
    private val allItems = SoundFiles.getAll()
    private val soundToggles: Map<SoundFile, BooleanProperty> = loadToggles()
    private val searchResults = FXCollections.observableArrayList(allItems).apply {
        FXCollections.sort(this) { lhs, rhs ->
            val lhsSelected = soundToggles[lhs]?.value ?: false
            val rhsSelected = soundToggles[rhs]?.value ?: false
            when {
                lhsSelected == rhsSelected -> 0
                lhsSelected -> -1
                else -> 1
            }
        }
    }

    init {
        val root = VBox(PADDING_SMALL)
        root.padding = Insets(PADDING_MEDIUM)
        val discordBotEnabled = ConfigPersistence.isUsingDiscordBot()
        root.children += CheckBox(LABEL_PLAY_THROUGH_DISCORD).apply {
            selectedProperty().bindBidirectional(playThroughDiscord)
            disableProperty().set(!discordBotEnabled)
        }
        if (!discordBotEnabled) {
            root.children += Label(LABEL_CONFIGURE_BOT)
        }
        root.children += TextField().apply {
            promptText = PROMPT_SEARCH
            textProperty().addListener { _, _, newValue -> filterItems(newValue) }
        }
        root.children += ListView<SoundFile>(searchResults).apply {
            setCellFactory { _ -> CheckBoxWithButtonCell { soundToggles[it] } }
        }
        root.children += Button(ACTION_SAVE).apply {
            setOnAction { saveToggles() }
        }

        title = type.friendlyName()
        scene = Scene(root)
        icons.add(bulldogIcon())
        width = WINDOW_WIDTH
        addEventFilter(KeyEvent.KEY_PRESSED) {
            if (it.code == KeyCode.ESCAPE) {
                close()
            }
        }
    }

    private fun loadToggles(): Map<SoundFile, BooleanProperty> {
        val selection = ConfigPersistence.getSoundsForType(type)
        return allItems.associateWith { SimpleBooleanProperty(it in selection) }
    }

    private fun saveToggles() {
        ConfigPersistence.setPlayedThroughDiscord(type, playThroughDiscord.get())
        ConfigPersistence.saveSoundsForType(type, soundToggles.filterValues { it.value }.keys.toList())
        close()
    }

    private fun filterItems(query: String) {
        searchResults.clear()
        searchResults.addAll(allItems.filter { it.name.contains(query.trim(), ignoreCase = true) })
    }

    private class CheckBoxWithButtonCell(private val getSelectedProperty: (SoundFile?) -> BooleanProperty?) : ListCell<SoundFile>() {
        private val container = GridPane()
        private val checkBox = CheckBox()
        private val button = Button("", ImageView(playIcon()))
        private var booleanProperty: BooleanProperty? = null

        init {
            container.columnConstraints.addAll(ColumnConstraints().apply {
                hgrow = Priority.ALWAYS
            })
            container.add(checkBox, 0, 0)
            container.add(button, 1, 0)
        }

        override fun updateItem(item: SoundFile?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
                return
            }
            graphic = container
            checkBox.text = item?.name
            button.setOnAction { item?.play() }

            booleanProperty?.let {
                checkBox.selectedProperty().unbindBidirectional(booleanProperty)
            }
            booleanProperty = getSelectedProperty(item)
            booleanProperty?.let {
                checkBox.selectedProperty().bindBidirectional(booleanProperty)
            }
        }
    }
}