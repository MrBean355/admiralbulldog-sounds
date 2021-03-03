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

package com.github.mrbean355.admiralbulldog.installation

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.styles.DarkModeStyles
import tornadofx.Wizard
import tornadofx.multi
import tornadofx.style

class InstallationWizard : Wizard(getString("title_app"), getString("header_install_gsi")) {
    private val installationModel by inject<InstallationModel>()

    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    init {
        showSteps = false
        add<ShowRationaleStep>()
        add<ChooserDotaDirectoryStep>()

        onComplete {
            ConfigPersistence.setDotaPath(installationModel.dotaPath.get())
        }
    }

    override fun onDock() {
        super.onDock()
        // FIXME: How can we specify this in the stylesheet?
        root.top.style {
            if (ConfigPersistence.isDarkMode()) {
                backgroundColor = multi(DarkModeStyles.WIZARD_HEADER)
            }
        }
    }
}
