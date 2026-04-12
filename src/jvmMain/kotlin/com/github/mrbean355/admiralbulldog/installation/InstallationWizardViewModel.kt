package com.github.mrbean355.admiralbulldog.installation

import com.github.mrbean355.admiralbulldog.arch.ComposeViewModel
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence
import com.github.mrbean355.admiralbulldog.persistence.DotaPath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.swing.JFileChooser

class InstallationWizardViewModel : ComposeViewModel() {

    enum class Step { RATIONALE, DOTA_PATH }

    private val _currentStep = MutableStateFlow(Step.RATIONALE)
    val currentStep: StateFlow<Step> = _currentStep.asStateFlow()

    private val _dotaPath = MutableStateFlow(getString("install_no_path"))
    val dotaPath: StateFlow<String> = _dotaPath.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete.asStateFlow()

    private var selectedDirectory: File? = null

    fun nextStep() {
        if (_currentStep.value == Step.RATIONALE) {
            _currentStep.value = Step.DOTA_PATH
        }
    }

    fun previousStep() {
        if (_currentStep.value == Step.DOTA_PATH) {
            _currentStep.value = Step.RATIONALE
        }
    }

    fun onChooseDirectoryClicked() {
        val chooser = JFileChooser().apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            selectedFile = selectedDirectory
            dialogTitle = getString("install_chooser_title")
        }
        val result = chooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = chooser.selectedFile
            selectedDirectory = file
            val verifiedPath = DotaPath.getDotaRootDirectory(file.absolutePath)
            if (verifiedPath != null) {
                _dotaPath.value = verifiedPath
                _errorMessage.value = null
                _isComplete.value = true
            } else {
                _dotaPath.value = file.absolutePath
                _errorMessage.value = getString("install_invalid_path")
                _isComplete.value = false
            }
        }
    }

    fun onFinish() {
        if (_isComplete.value) {
            ConfigPersistence.setDotaPath(_dotaPath.value)
            requestWindowClose()
        }
    }
}
