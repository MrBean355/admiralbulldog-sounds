package com.github.mrbean355.dota2.integration

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.Date

enum class SoundEffect(private val fileName: String) {
    AAAH("bulldogaaah.mp3"),
    ALLIANCE("alliance.mp3"),
    AWOO("awoo.mp3"),
    BABOONS("goddamnbaboons.mp3"),
    BABY_RAGE("babyrage.mp3"),
    BERRY_DISGUSTING("berrydisgusting.mp3"),
    BLBLBL("blblbl.mp3"),
    CANT_BELIEVE_IT("icantbelieveit.mp3"),
    DUMB_SHIT_PLEB("dumbshitpleb.mp3"),
    EEL("eel.mp3"),
    ELEGIGGLE("elegiggle.mp3"),
    FEELS_BAD_MAN("feelsbadman.mp3"),
    FOURHEAD("bulldog4head.mp3"),
    HANDS_UP("bulldoghandsup.mp3"),
    HELP("help.mp3"),
    IM_COMING("imcoming.mp3"),
    KREYGASM("kreygasm.mp3"),
    LULDOG("luldog.mp3"),
    MOTHER_COMES("mothercomes.mp3"),
    MOVE_YOUR_ASS("moveyourass.mp3"),
    NOT_LIKE_THIS("notlikethis.mp3"),
    NUTS("nuts.mp3"),
    OH_MY_GOD("ohmygod.mp3"),
    OMEGA_EZ("omegaez.mp3"),
    PRAISE("praise.mp3"),
    PEPEGA("goddamnpepega.mp3"),
    PLEBS_ARE_DISGUSTING("plebsaredisgusting.mp3"),
    ROONS("roons.mp3"),
    RUN("run.mp3"),
    SAUSAGE("sausage.mp3"),
    SLOW_DOWN("slowdown.mp3"),
    SMOKE_WEED("weed.mp3"),
    THATS_POWER("thatspower.mp3"),
    UNLIMITED_POWER("unlimitedpower.mp3"),
    USE_MIDAS("useyourmidas.wav"),
    VIVON("vivon.mp3"),
    WE_LOST("wefuckinglost.wav"),
    WTF_MAN("wtfman.mp3");

    fun play() {
        println("[${Date()}] Playing: $this")
        val resource = javaClass.classLoader.getResource(fileName)
        val media = Media(resource.toURI().toString())
        mediaPlayer = MediaPlayer(media).apply {
            volume = VOLUME
            play()
        }
    }

    private companion object {
        private const val VOLUME = 0.20
        /* Prevent garbage collection. */
        private var mediaPlayer: MediaPlayer? = null
    }
}