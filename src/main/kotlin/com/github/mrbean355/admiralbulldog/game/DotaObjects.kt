package com.github.mrbean355.admiralbulldog.game

data class GameState(
        val provider: Provider,
        val map: DotaMap?,
        val player: Player?,
        val hero: Hero?,
        val items: Items?) {

    fun hasValidProperties(): Boolean {
        return !listOf(map, player, hero, items).contains(null)
    }
}

data class Provider(
        val name: String,
        val appid: Int,
        val version: Int,
        val timestamp: Long
)

data class DotaMap(
        val name: String,
        val matchid: String,
        val game_time: Long,
        val clock_time: Long,
        val daytime: Boolean,
        val nightstalker_night: Boolean,
        val game_state: String,
        val paused: Boolean,
        val win_team: String,
        val customgamename: String,
        val ward_purchase_cooldown: Float?,
        val radiant_ward_purchase_cooldown: Float?,
        val dire_ward_purchase_cooldown: Float?,
        val roshan_state: String?,
        val roshan_state_end_seconds: Float?
)

data class Player(
        val steamid: String,
        val name: String,
        val activity: String,
        val kills: Int,
        val deaths: Int,
        val assists: Int,
        val last_hits: Int,
        val denies: Int,
        val kill_streak: Int,
        val commands_issued: Long,
        val kill_list: Any,
        val team_name: String,
        val gold: Int,
        val gold_reliable: Int,
        val gold_unreliable: Int,
        val gold_from_hero_kills: Int,
        val gold_from_creep_kills: Int,
        val gold_from_income: Int,
        val gold_from_shared: Int,
        val gpm: Float,
        val xpm: Float
)

data class Hero(
        val xpos: Float,
        val ypos: Float,
        val id: Int,
        val name: String,
        val level: Int,
        val alive: Boolean,
        val respawn_seconds: Int,
        val buyback_cost: Int,
        val buyback_cooldown: Float,
        val health: Float,
        val max_health: Float,
        val health_percent: Float,
        val mana: Float,
        val max_mana: Float,
        val mana_percent: Float,
        val silenced: Boolean,
        val stunned: Boolean,
        val disarmed: Boolean,
        val magicimmune: Boolean,
        val hexed: Boolean,
        val muted: Boolean,
        val smoked: Boolean,
        val has_debuff: Boolean,
        val talent_1: Boolean,
        val talent_2: Boolean,
        val talent_3: Boolean,
        val talent_4: Boolean,
        val talent_5: Boolean,
        val talent_6: Boolean,
        val talent_7: Boolean,
        val talent_8: Boolean,
        val `break`: Boolean
)

data class Items(
        val slot0: Item,
        val slot1: Item,
        val slot2: Item,
        val slot3: Item,
        val slot4: Item,
        val slot5: Item,
        val slot6: Item,
        val slot7: Item,
        val slot8: Item,
        val stash0: Item,
        val stash1: Item,
        val stash2: Item,
        val stash3: Item,
        val stash4: Item,
        val stash5: Item
)

data class Item(
        val name: String,
        val purchaser: Float,
        val can_cast: Boolean,
        val cooldown: Float,
        val passive: Boolean
)
