/*
 * Copyright 2021 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.feedback

import com.github.mrbean355.admiralbulldog.arch.AppViewModel
import com.github.mrbean355.admiralbulldog.arch.repo.DiscordBotRepository
import com.github.mrbean355.admiralbulldog.common.RETRY_BUTTON
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.common.showError
import com.github.mrbean355.admiralbulldog.common.showInformation
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.showProgressScreen
import javafx.beans.property.Property
import javafx.scene.control.ButtonType
import kotlinx.coroutines.launch
import tornadofx.FXEvent
import tornadofx.intProperty
import tornadofx.stringProperty

class FeedbackViewModel : AppViewModel() {
    private val discordBotRepository = DiscordBotRepository()

    val rating: Property<Number> = intProperty(3)
    val comments: Property<String> = stringProperty()

    override fun onReady() {
        ConfigPersistence.setFeedbackCompleted()
    }

    fun onSubmitClicked() {
        val progressScreen = showProgressScreen()
        viewModelScope.launch {
            val response = discordBotRepository.sendFeedback(rating.value.toInt(), comments.value.orEmpty())
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

}