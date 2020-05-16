package com.github.mrbean355.admiralbulldog.ui2.installation

import com.github.mrbean355.admiralbulldog.ui.getString
import com.github.mrbean355.admiralbulldog.ui2.persistance.AppConfig
import tornadofx.Wizard

class InstallationWizard : Wizard(getString("app_title"), getString("install_header")) {
    private val installationModel by inject<InstallationModel>()

    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete

    init {
        showSteps = false
        add<ShowRationaleStep>()
        add<ChooserDotaDirectoryStep>()

        onComplete {
            AppConfig.dotaPathProperty().set(installationModel.dotaPath.get())
        }
    }
}
