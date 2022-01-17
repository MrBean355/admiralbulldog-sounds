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
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import com.github.mrbean355.admiralbulldog.styles.reloadAppStyles
import com.github.mrbean355.admiralbulldog.ui.prepareTrayIcon
import javafx.stage.Stage
import tornadofx.App
import kotlin.system.exitProcess

class DotaApplication : App(icon = BulldogIcon(), stylesheet = arrayOf(AppStyles::class)) {

    override fun init() {
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandlerImpl(hostServices))
        ConfigPersistence.initialise()
        reloadAppStyles()
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
