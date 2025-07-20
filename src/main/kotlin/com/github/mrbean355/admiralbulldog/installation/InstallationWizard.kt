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
