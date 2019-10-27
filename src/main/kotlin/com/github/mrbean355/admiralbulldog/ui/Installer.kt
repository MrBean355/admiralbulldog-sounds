package com.github.mrbean355.admiralbulldog.ui

import com.github.mrbean355.admiralbulldog.ACTION_TRY_AGAIN
import com.github.mrbean355.admiralbulldog.HEADER_INSTALLER
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER_CANT_CREATE
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER_SUCCESS
import com.github.mrbean355.admiralbulldog.MSG_INSTALLER_WRONG_FOLDER
import com.github.mrbean355.admiralbulldog.TITLE_INSTALLER
import com.github.mrbean355.admiralbulldog.TITLE_MAIN_WINDOW
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File

private const val DOTA_ROOT = "dota 2 beta"
private const val DOTA_CFG = "$DOTA_ROOT/game/dota/cfg"
private const val DOTA_GSI = "gamestate_integration"
private const val GSI_FILE = "gamestate_integration_bulldog.cfg"

fun createGsiFile(stage: Stage) {
    val btn = Alert(Alert.AlertType.INFORMATION, MSG_INSTALLER, ButtonType.NEXT).showMine()
    if (btn !== ButtonType.NEXT) {
        return
    }

    val result = DirectoryChooser().run {
        title = TITLE_INSTALLER
        showDialog(stage)
    } ?: return

    val selectedPath = result.absolutePath
    val root = selectedPath.substringBefore(DOTA_ROOT)
    val cfg = File("$root/$DOTA_CFG".safe())
    if (!cfg.exists()) {
        val tryAgain = ButtonType(ACTION_TRY_AGAIN)
        val btn = Alert(Alert.AlertType.WARNING, MSG_INSTALLER_WRONG_FOLDER, tryAgain)
                .showMine()
        if (btn === tryAgain) {
            createGsiFile(stage)
        }
        return
    }
    val gsi = File(cfg.absolutePath + "/$DOTA_GSI".safe())
    if (!gsi.exists()) {
        gsi.mkdir()
    }
    val bulldog = File(gsi.absolutePath + "/$GSI_FILE".safe())
    if (!bulldog.exists() && !bulldog.createNewFile()) {
        Alert(Alert.AlertType.WARNING, MSG_INSTALLER_CANT_CREATE.format(bulldog.absolutePath), ButtonType.CANCEL)
                .showMine()
        return
    }
    bulldog.writeText("""
            "AdmiralBulldog Sounds"
            {
                "uri"           "http://localhost:12345"
                "timeout"       "5.0"
                "buffer"        "0.1"
                "throttle"      "0.1"
                "heartbeat"     "30.0"
                "data"
                {
                    "provider"      "1"
                    "map"           "1"
                    "player"        "1"
                    "hero"          "1"
                    "abilities"     "1"
                    "items"         "1"
                }
            }
        """.trimIndent())

    Alert(Alert.AlertType.INFORMATION, MSG_INSTALLER_SUCCESS, ButtonType.FINISH)
            .showMine()
}

private fun Alert.showMine(): ButtonType? {
    title = TITLE_MAIN_WINDOW
    headerText = HEADER_INSTALLER
    return showAndWait().orElse(null)
}

private fun String.safe(): String {
    return replace("/", File.separator)
}