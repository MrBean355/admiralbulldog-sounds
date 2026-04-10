package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence

/** Plays a sound shortly before the wisdom runes spawn. */
class OnWisdomRunesSpawn : RunesSpawnTrigger(
    frequencyMinutes = 7, // Update the 'trigger_desc_wisdom_runes' string as well
    spawnsAtStart = false,
    provideWarningPeriod = { ConfigPersistence.getWisdomRuneTimer() },
)