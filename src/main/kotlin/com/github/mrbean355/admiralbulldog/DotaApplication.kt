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

package com.github.mrbean355.admiralbulldog

import com.github.mrbean355.admiralbulldog.common.BulldogIcon
import com.github.mrbean355.admiralbulldog.exception.UncaughtExceptionHandlerImpl
import com.github.mrbean355.admiralbulldog.home.MainScreen
import com.github.mrbean355.admiralbulldog.mods.OldModMigration
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import javafx.scene.paint.Color.RED
import javafx.scene.text.FontPosture.ITALIC
import javafx.scene.text.FontWeight.BOLD
import javafx.stage.Stage
import tornadofx.App
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px
import kotlin.system.exitProcess

class DotaApplication : App(primaryView = MainScreen::class, icon = BulldogIcon(), stylesheet = arrayOf(AppStyles::class)) {

    override fun init() {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
        OldModMigration.run()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        prepareTrayIcon(stage)
    }

    override fun stop() {
        super.stop()
        exitProcess(0)
    }
}

class AppStyles : Stylesheet() {

    companion object {
        val boldFont by cssclass()
        val italicFont by cssclass()
        val smallFont by cssclass()
        val mediumFont by cssclass()
        val largeFont by cssclass()
        val monospacedFont by cssclass()
        val inlineError by cssclass()
        val iconButton by cssclass()
    }

    init {
        boldFont {
            fontWeight = BOLD
        }
        italicFont {
            fontStyle = ITALIC
        }
        smallFont {
            fontSize = 10.px
        }
        mediumFont {
            fontSize = 14.px
        }
        largeFont {
            fontSize = 18.px
        }
        monospacedFont {
            fontFamily = "Lucida Console"
        }
        inlineError {
            textFill = RED
            fontWeight = BOLD
        }
        iconButton {
            padding = box(vertical = 2.px, horizontal = 4.px)
        }
    }
}
