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