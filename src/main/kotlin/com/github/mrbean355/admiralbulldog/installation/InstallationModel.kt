package com.github.mrbean355.admiralbulldog.installation

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import tornadofx.ViewModel

class InstallationModel : ViewModel() {
    val dotaPath: StringProperty = SimpleStringProperty()
}
