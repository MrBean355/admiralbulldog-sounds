package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.service.Result
import com.github.mrbean355.admiralbulldog.service.lookupToken
import com.github.mrbean355.admiralbulldog.ui.getString
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Callable

class DiscordBotViewModel {
    private val coroutineScope = CoroutineScope(Default + Job())
    private val lookupResponse = SimpleStringProperty()

    val botEnabled = SimpleBooleanProperty(ConfigPersistence.isUsingDiscordBot())
    val token = SimpleStringProperty(ConfigPersistence.getDiscordToken())
    val statusImage = SimpleObjectProperty<Status>()
    val status: StringBinding = Bindings.createStringBinding(Callable {
        if (botEnabled.get()) {
            lookupResponse.get()
        } else {
            statusImage.set(Status.NEUTRAL)
            getString("msg_bot_disabled")
        }
    }, botEnabled, lookupResponse)

    init {
        botEnabled.addListener { _, _, newValue ->
            ConfigPersistence.setUsingDiscordBot(newValue)
            if (newValue) {
                lookupDiscordGuild()
            }
        }
        token.addListener { _, _, newValue ->
            ConfigPersistence.setDiscordToken(newValue)
            lookupDiscordGuild()
        }
    }

    fun init() {
        if (botEnabled.get()) {
            lookupDiscordGuild()
        } else {
            statusImage.set(Status.NEUTRAL)
        }
    }

    fun onClose() {
        coroutineScope.cancel()
    }

    private fun lookupDiscordGuild() {
        statusImage.set(Status.LOADING)
        lookupResponse.set(getString("msg_bot_loading"))

        coroutineScope.launch {
            val result = lookupToken(token.get())
            withContext(Main) {
                when (result) {
                    is Result.Success -> {
                        statusImage.set(Status.GOOD)
                        lookupResponse.set(getString("msg_bot_active", result.data))
                    }
                    is Result.Error -> {
                        statusImage.set(Status.BAD)
                        if (result.statusCode == 404) {
                            lookupResponse.set(getString("msg_bot_not_found"))
                        } else {
                            lookupResponse.set(getString("msg_bot_error"))
                        }
                    }
                }
            }
        }
    }

    enum class Status(val image: String) {
        NEUTRAL("grey_dot.png"),
        GOOD("green_dot.png"),
        BAD("red_dot.png"),
        LOADING("yellow_dot.png")
    }
}