package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.arch.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.assets.SoundByte
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.Alert
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SoundBoardViewModel(private val stage: Stage) {
    private val coroutineScope = CoroutineScope(Default + Job())
    private val discordBotRepository = DiscordBotRepository()

    val soundBoard: ObservableList<SoundByte> = FXCollections.observableArrayList<SoundByte>()
    val isEmpty = SimpleBooleanProperty()

    fun init() {
        refresh()
    }

    fun refresh() {
        soundBoard.setAll(ConfigPersistence.getSoundBoard())
        isEmpty.set(soundBoard.isEmpty())
    }

    fun onSoundClicked(soundByte: SoundByte) {
        coroutineScope.launch {
            val response = discordBotRepository.playSound(soundByte)
            if (!response.isSuccessful()) {
                withContext(Dispatchers.Main) {
                    Alert(type = Alert.AlertType.ERROR,
                            header = HEADER_DISCORD_SOUND,
                            content = MSG_DISCORD_PLAY_FAILED.format(soundByte.name),
                            buttons = arrayOf(ButtonType.OK),
                            owner = stage
                    ).show()
                }
            }
        }
    }

    fun onClose() {
        coroutineScope.cancel()
    }
}