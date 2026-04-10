package com.github.mrbean355.admiralbulldog.triggers

import com.github.mrbean355.admiralbulldog.persistence.ConfigPersistence

/** Plays a sound shortly before the bounty runes spawn. */
class OnBountyRunesSpawn : RunesSpawnTrigger(
    frequencyMinutes = 4, // Update the 'trigger_desc_bounty_runes' string as well
    spawnsAtStart = true,
    provideWarningPeriod = { ConfigPersistence.getBountyRuneTimer() },
)