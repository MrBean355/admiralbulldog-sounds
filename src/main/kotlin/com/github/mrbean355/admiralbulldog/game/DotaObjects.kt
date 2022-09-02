/*
 * Copyright 2022 Michael Johnston
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

@file:Suppress("SpellCheckingInspection")

package com.github.mrbean355.admiralbulldog.game

data class GameState(
    val map: DotaMap?,
    val player: Player?,
    val hero: Hero?,
    val items: Items?
) {

    fun hasValidProperties(): Boolean {
        return map != null && player != null && hero != null && items != null
    }
}

// ====================================================================================================
// Unused properties are commented out to minimize the amount of JSON deserialization.
// ====================================================================================================

data class DotaMap(
//    val name: String,
    val matchid: String,
//    val game_time: Long,
    val clock_time: Long,
//    val daytime: Boolean,
//    val nightstalker_night: Boolean,
    val game_state: MatchState,
    val paused: Boolean,
    val win_team: String,
//    val customgamename: String,
//    val ward_purchase_cooldown: Float?,
//    val radiant_ward_purchase_cooldown: Float?,
//    val dire_ward_purchase_cooldown: Float?,
//    val roshan_state: String?,
//    val roshan_state_end_seconds: Float?
)

@Suppress("unused")
enum class MatchState {
    DOTA_GAMERULES_STATE_DISCONNECT,
    DOTA_GAMERULES_STATE_GAME_IN_PROGRESS,
    DOTA_GAMERULES_STATE_HERO_SELECTION,
    DOTA_GAMERULES_STATE_INIT,
    DOTA_GAMERULES_STATE_LAST,
    DOTA_GAMERULES_STATE_POST_GAME,
    DOTA_GAMERULES_STATE_PRE_GAME,
    DOTA_GAMERULES_STATE_STRATEGY_TIME,
    DOTA_GAMERULES_STATE_WAIT_FOR_PLAYERS_TO_LOAD,
    DOTA_GAMERULES_STATE_CUSTOM_GAME_SETUP
}

data class Player(
//    val steamid: String,
//    val name: String,
//    val activity: String,
    val kills: Int,
    val deaths: Int,
//    val assists: Int,
//    val last_hits: Int,
//    val denies: Int,
//    val kill_streak: Int,
//    val commands_issued: Long,
//    val kill_list: Any,
    val team_name: String,
//    val gold: Int,
//    val gold_reliable: Int,
//    val gold_unreliable: Int,
//    val gold_from_hero_kills: Int,
//    val gold_from_creep_kills: Int,
//    val gold_from_income: Int,
//    val gold_from_shared: Int,
//    val gpm: Float,
//    val xpm: Float
)

data class Hero(
//    val xpos: Float,
//    val ypos: Float,
//    val id: Int,
//    val name: String,
//    val level: Int,
//    val alive: Boolean,
    val respawn_seconds: Int,
//    val buyback_cost: Int,
//    val buyback_cooldown: Float,
    val health: Float,
    val max_health: Float,
    val health_percent: Float,
//    val mana: Float,
//    val max_mana: Float,
//    val mana_percent: Float,
//    val silenced: Boolean,
//    val stunned: Boolean,
//    val disarmed: Boolean,
//    val magicimmune: Boolean,
//    val hexed: Boolean,
//    val muted: Boolean,
    val smoked: Boolean,
//    val has_debuff: Boolean,
//    val talent_1: Boolean,
//    val talent_2: Boolean,
//    val talent_3: Boolean,
//    val talent_4: Boolean,
//    val talent_5: Boolean,
//    val talent_6: Boolean,
//    val talent_7: Boolean,
//    val talent_8: Boolean,
//    val `break`: Boolean
)

data class Items(
    val slot0: Item,
    val slot1: Item,
    val slot2: Item,
    val slot3: Item,
    val slot4: Item,
    val slot5: Item,
//    val slot6: Item,
//    val slot7: Item,
//    val slot8: Item,
//    val stash0: Item,
//    val stash1: Item,
//    val stash2: Item,
//    val stash3: Item,
//    val stash4: Item,
//    val stash5: Item
)

data class Item(
    val name: String,
//    val purchaser: Float,
//    val can_cast: Boolean,
    val cooldown: Float,
//    val passive: Boolean
)
