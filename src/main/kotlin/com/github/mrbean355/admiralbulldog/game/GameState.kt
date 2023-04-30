/*
 * Copyright 2023 Michael Johnston
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

package com.github.mrbean355.admiralbulldog.game

import com.github.mrbean355.dota2.event.DotaEvent
import com.github.mrbean355.dota2.hero.Hero
import com.github.mrbean355.dota2.item.Items
import com.github.mrbean355.dota2.map.DotaMap
import com.github.mrbean355.dota2.player.Player

class GameState(
    val map: DotaMap,
    val player: Player?,
    val hero: Hero?,
    val items: Items?,
    val events: List<DotaEvent>,
)