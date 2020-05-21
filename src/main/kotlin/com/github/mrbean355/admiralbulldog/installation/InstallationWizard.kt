package com.github.mrbean355.admiralbulldog.installation

import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import tornadofx.Wizard

class InstallationWizard : Wizard(getString("title_app"), getString("install_header")) {
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
}
