package com.github.mrbean355.admiralbulldog.mod.modular

import com.github.mrbean355.admiralbulldog.arch.DotaMod
import com.github.mrbean355.admiralbulldog.arch.DotaModPart

data class ModTreeItem(
        val mod: DotaMod,
        val part: DotaModPart? = null
) {
    override fun toString(): String {
        return part?.name ?: mod.name
    }
}
