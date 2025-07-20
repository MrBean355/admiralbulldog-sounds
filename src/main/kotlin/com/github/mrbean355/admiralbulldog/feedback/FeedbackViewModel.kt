package com.github.mrbean355.admiralbulldog.feedback

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import javafx.beans.property.Property
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.FXEvent
import tornadofx.objectProperty
import tornadofx.stringProperty
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FeedbackViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val rating: Property<Any> = objectProperty()
    val comments: Property<String> = stringProperty()

    override fun onReady() {
        ConfigPersistence.setNextFeedback(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90))
    }

    fun onSubmitClicked() {
        val rating = rating.value?.toString()?.toIntOrNull()
        if (rating == null) {
            showWarning(getString("title_feedback"), getString("content_feedback_rating_not_selected"))
            return
        }
        val progressScreen = showProgressScreen()
        viewModelScope.launch {
            val response = discordBotRepository.sendFeedback(rating, comments.value.orEmpty())
            progressScreen.close()
            if (response.isSuccessful()) {
                showInformation(getString("header_feedback_submit_success"), getString("content_feedback_submit_success"))
                fire(CloseEvent())
            } else {
                showError(getString("header_feedback_submit_error"), getString("content_feedback_submit_error"), ButtonType.CANCEL, RETRY_BUTTON) {
                    if (it === RETRY_BUTTON) {
                        onSubmitClicked()
                    } else {
                        fire(CloseEvent())
                    }
                }
            }
        }
    }

    class CloseEvent : FXEvent()

    companion object {

        fun shouldPrompt(): Boolean {
            return ConfigPersistence.getNextFeedback() <= System.currentTimeMillis() && Random.nextDouble() <= 0.15
        }
    }
}