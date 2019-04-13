package com.github.mrbean355

import com.github.mrbean355.plugin.*

/** Notify registered plugins when the game state changes. */
class GameStateMonitor {
    private var previousState: GameState? = null
    private val gamePlugins: List<GamePlugin> = listOf(
            DeathGamePlugin(),
            HealGamePlugin(),
            KillGamePlugin(),
            MatchEndGamePlugin(),
            SmokeOfDeceitGamePlugin(),
            RunesGamePlugin(),
            MidasGamePlugin(),
            PeriodicGamePlugin()
    )

    init {
        // Preload
        javafx.embed.swing.JFXPanel()
        SoundEffect.HANDS_UP.play()
    }

    fun onUpdate(newState: GameState) {
        val previousState = previousState
        if (previousState != null && previousState.hasValidProperties() && newState.hasValidProperties()) {
            gamePlugins.forEach {
                it.onGameStateUpdated(previousState, newState)
            }
        } else {
            println("Bad data :(")
        }
        this.previousState = newState
    }
}

/** spectating */
/*
GameState(
    provider=Provider(name=Dota 2, appid=570, version=47, timestamp=1554822515),
    map=DotaMap(name=start, matchid=4633470070, game_time=769, clock_time=602, daytime=true, nightstalker_night=false, game_state=DOTA_GAMERULES_STATE_GAME_IN_PROGRESS, paused=false, win_team=none, customgamename=, ward_purchase_cooldown=0.0),
    player=Player(steamid=null, name=null, activity=null, kills=0, deaths=0, assists=0, last_hits=0, denies=0, kill_streak=0, commands_issued=0, kill_list=null, team_name=null, gold=0, gold_reliable=0, gold_unreliable=0, gold_from_hero_kills=0, gold_from_creep_kills=0, gold_from_income=0, gold_from_shared=0, gpm=0.0, xpm=0.0),
    hero=Hero(xpos=0.0, ypos=0.0, id=0, name=null, level=0, alive=false, respawn_seconds=0, buyback_cost=0, buyback_cooldown=0.0, health=0.0, max_health=0.0, health_percent=0.0, mana=0.0, max_mana=0.0, mana_percent=0.0, silenced=false, stunned=false, disarmed=false, magicimmune=false, hexed=false, muted=false, smoked=false, has_debuff=false, talent_1=false, talent_2=false, talent_3=false, talent_4=false, talent_5=false, talent_6=false, talent_7=false, talent_8=false, break=false),
    abilities=Abilities(ability0=null, ability1=null, ability2=null, ability3=null),
    items=Items(slot0=null, slot1=null, slot2=null, slot3=null, slot4=null, slot5=null, slot6=null, slot7=null, slot8=null, stash0=null, stash1=null, stash2=null, stash3=null, stash4=null, stash5=null)
)
 */