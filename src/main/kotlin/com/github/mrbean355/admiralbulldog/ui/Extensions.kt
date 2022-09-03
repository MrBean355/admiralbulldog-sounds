/*
 * Copyright 2022 Michael Johnston
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mrbean355.admiralbulldog.ui

import javafx.stage.Stage
import tornadofx.Component
import tornadofx.UIComponent

/** Open the component of type [C] as an un-resizable modal. */
inline fun <reified C : UIComponent> Component.openScreen(
    escapeClosesWindow: Boolean = true,
    block: Boolean = false,
): Stage? = find<C>().openModal(
    escapeClosesWindow = escapeClosesWindow,
    block = block,
    resizable = false
)