package com.github.mrbean355.admiralbulldog.mods

import com.github.mrbean355.admiralbulldog.arch.DotaMod

/** Sort alphabetically by name, placing the base mod first. */
@Suppress("FunctionName")
fun DotaModComparator() = { lhs: DotaMod, rhs: DotaMod ->
    when {
        lhs.name == "Base mod" -> -1
        rhs.name == "Base mod" -> 1
        else -> lhs.name.compareTo(rhs.name)
    }
}
