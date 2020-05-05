package com.github.mrbean355.admiralbulldog.ui2.discord

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.Spacing
import com.github.mrbean355.admiralbulldog.ui2.alert
import com.github.mrbean355.admiralbulldog.ui2.triggers.SoundTrigger
import javafx.scene.control.Alert
import javafx.util.StringConverter
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.listview
import tornadofx.paddingAll
import tornadofx.textfield
import tornadofx.useCheckbox

class DiscordBotScreen : View(getString("tab_discord")) {
    private val viewModel by inject<DiscordBotViewModel>()

    override val root = form {
        paddingAll = Spacing.MEDIUM
        fieldset {
            field(getString("label_bot_enabled")) {
                checkbox(property = viewModel.enabledProperty)
            }
            field(getString("label_magic_number")) {
                textfield(property = viewModel.magicNumberProperty)
                button(getString("label_magic_number_test")) {
                    action { viewModel.onTestClicked() }
                }
            }
        }
        fieldset(getString("label_play_through_discord")) {
            listview(viewModel.items) {
                useCheckbox(SoundTriggerStringConverter(), viewModel::getPlayThroughDiscordProperty)
            }
        }
    }

    init {
        subscribe<DiscordBotViewModel.SuccessEvent> {
            alert(Alert.AlertType.INFORMATION, getString("label_magic_number"), it.message)
                    .show()
        }
        subscribe<DiscordBotViewModel.ErrorEvent> {
            alert(Alert.AlertType.ERROR, getString("label_magic_number"), it.message)
                    .show()
        }
    }
}

class SoundTriggerStringConverter : StringConverter<SoundTrigger>() {

    override fun toString(obj: SoundTrigger) = obj.name

    override fun fromString(string: String) = throw UnsupportedOperationException()
}
