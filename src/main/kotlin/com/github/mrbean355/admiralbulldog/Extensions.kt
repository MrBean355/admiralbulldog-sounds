package com.github.mrbean355.admiralbulldog

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tornadofx.ViewModel

/** Get a logger whose name is the simple name of the receiver class. */
val ViewModel.logger: Logger
    get() = LoggerFactory.getLogger(this::class.java.simpleName)
