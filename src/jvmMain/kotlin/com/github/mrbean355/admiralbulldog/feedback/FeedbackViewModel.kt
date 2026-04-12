package com.github.mrbean355.admiralbulldog.feedback

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.AlertButton
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.common.showWarning
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class FeedbackViewModel : ComposeViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    private val _rating = MutableStateFlow<Int?>(null)
    val rating = _rating.asStateFlow()

    private val _comments = MutableStateFlow("")
    val comments = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        ConfigPersistence.setNextFeedback(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(90))
    }

    fun setRating(value: Int) {
        _rating.value = value
    }

    fun setComments(value: String) {
        _comments.value = value
    }

    fun onSubmitClicked() {
        val currentRating = rating.value
        if (currentRating == null) {
            showWarning(getString("title_feedback"), getString("content_feedback_rating_not_selected"))
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val response = discordBotRepository.sendFeedback(currentRating, comments.value)
            _isLoading.value = false
            if (response.isSuccessful()) {
                showInformation(getString("header_feedback_submit_success"), getString("content_feedback_submit_success"))
                requestWindowClose()
            } else {
                showError(getString("header_feedback_submit_error"), getString("content_feedback_submit_error"), AlertButton.CANCEL, AlertButton.RETRY) { action ->
                    if (action == AlertButton.RETRY) {
                        onSubmitClicked()
                    } else {
                        requestWindowClose()
                    }
                }
            }
        }
    }

    companion object {
        fun shouldPrompt(): Boolean {
            return ConfigPersistence.getNextFeedback() <= System.currentTimeMillis() && Random.nextDouble() <= 0.15
        }
    }
}