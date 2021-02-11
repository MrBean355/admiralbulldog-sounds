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

import com.github.mrbean355.admiralbulldog.common.PADDING_MEDIUM
import com.github.mrbean355.admiralbulldog.common.PADDING_SMALL
import com.github.mrbean355.admiralbulldog.common.PADDING_TINY
import com.github.mrbean355.admiralbulldog.common.WIKI_GAME_FILES
import com.github.mrbean355.admiralbulldog.common.getString
import com.github.mrbean355.admiralbulldog.styles.AppStyles
import tornadofx.Fragment
import tornadofx.action
import tornadofx.addClass
import tornadofx.hyperlink
import tornadofx.label
import tornadofx.paddingLeft
import tornadofx.paddingTop
import tornadofx.textflow
import tornadofx.vbox

class ShowRationaleStep : Fragment() {

    override val root = vbox(spacing = PADDING_SMALL) {
        label(getString("install_rationale_1"))
        textflow {
            label(getString("install_example_1"))
            label(getString("install_example_2")) {
                paddingLeft = PADDING_TINY
                addClass(AppStyles.boldFont)
            }
        }
        textflow {
            paddingTop = PADDING_MEDIUM
            label(getString("install_rationale_2"))
            hyperlink(getString("install_rationale_3")) {
                action { hostServices.showDocument(WIKI_GAME_FILES) }
            }
        }
        label(getString("install_rationale_4"))
    }
}
