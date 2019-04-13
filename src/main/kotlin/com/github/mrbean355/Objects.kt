package com.github.mrbean355

data class GameState(
        val provider: Provider,
        val map: DotaMap?,
        val player: Player?,
        val hero: Hero?,
//        val abilities: Abilities,
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
        val kill_list: KillList,
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

//data class SpectatedPlayer(
//        val team2: Team,
//        val team3: Team
//)
//
//data class Team(
//        val player: TeamPlayer
//)

//data class TeamPlayer(
//        private val steamid: String?,
//        private val name: String?,
//        private val activity: String?,
//        private val kills: Float,
//        private val deaths: Float,
//        private val assists: Float,
//        private val last_hits: Float,
//        private val denies: Float,
//        private val kill_streak: Float,
//        private val commands_issued: Float,
//        private val Kill_listObject: Any?,
//        private val team_name: String?,
//        private val gold: Float,
//        private val gold_reliable: Float,
//        private val gold_unreliable: Float,
//        private val gold_from_hero_kills: Float,
//        private val gold_from_creep_kills: Float,
//        private val gold_from_income: Float,
//        private val gold_from_shared: Float,
//        private val gpm: Float,
//        private val xpm: Float,
//        private val net_worth: Float,
//        private val hero_damage: Float,
//        private val wards_purchased: Float,
//        private val wards_placed: Float,
//        private val wards_destroyed: Float,
//        private val runes_activated: Float,
//        private val camps_stacked: Float,
//        private val support_gold_spent: Float,
//        private val consumable_gold_spent: Float,
//        private val item_gold_spent: Float,
//        private val gold_lost_to_death: Float,
//        private val gold_spent_on_buybacks: Float
//)

/* TODO: Find out the rest of the fields. */
class KillList

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

data class Abilities(
        val ability0: Ability,
        val ability1: Ability,
        val ability2: Ability,
        val ability3: Ability
)

data class Ability(
        val name: String,
        val level: Int,
        val can_cast: Boolean,
        val passive: Boolean,
        val ability_active: Boolean,
        val cooldown: Float,
        val ultimate: Boolean
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
