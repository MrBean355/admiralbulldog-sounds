package com.github.mrbean355.dota2.integration

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.util.Date

enum class SoundEffect(private val fileName: String) {
    // Special
    USE_MIDAS("useyourmidas.wav"),
    WE_LOST("wefuckinglost.wav"),
    // End special
    ADMIRALC("sounds/bulldog/admiralc.mp3"),
    AHAHA4HEAD("sounds/other/ahaha4head.mp3"),
    ALLIANCE("sounds/bulldog/alliance.mp3"),
    AWOO("sounds/bulldog/awoo.mp3"),
    BABYRAGE("sounds/bulldog/babyrage.mp3"),
    BADGRILL("sounds/bulldog/badgrill.mp3"),
    BERRYDISGUSTING("sounds/bulldog/berrydisgusting.mp3"),
    BLBLBL("sounds/bulldog/blblbl.mp3"),
    BRUH("sounds/other/bruh.mp3"),
    BULLDOG4HEAD("sounds/bulldog/bulldog4head.mp3"),
    BULLDOGAAAH("sounds/bulldog/bulldogaaah.mp3"),
    BULLDOGHANDSUP("sounds/bulldog/bulldoghandsup.mp3"),
    CATERPILLAR("sounds/other/caterpillar.mp3"),
    CHEFS4("sounds/other/chefs4.mp3"),
    CLICKFORFREEPOINTS("sounds/other/clickforfreepoints.mp3"),
    COLLATERALDAMAGE("sounds/other/collateraldamage.mp3"),
    DAMNSON("sounds/other/damnson.mp3"),
    DENMARK("sounds/other/denmark.mp3"),
    DUMBSHITPLEB("sounds/bulldog/dumbshitpleb.mp3"),
    EEL("sounds/bulldog/eel.mp3"),
    ELEGIGGLE("sounds/bulldog/elegiggle.mp3"),
    EXORT("sounds/other/exort.mp3"),
    FEELSBADMAN("sounds/bulldog/feelsbadman.mp3"),
    FEELSGOODMAN("sounds/bulldog/feelsgoodman.mp3"),
    FOURHEAD("sounds/other/fourhead.mp3"),
    GODDAMNBABOONS("sounds/bulldog/goddamnbaboons.mp3"),
    GODDAMNPEPEGA("sounds/bulldog/goddamnpepega.mp3"),
    GOROSH("sounds/bulldog/gorosh.mp3"),
    HELP("sounds/bulldog/help.mp3"),
    HOBBITS("sounds/other/hobbits.mp3"),
    ICANTBELIEVEIT("sounds/bulldog/icantbelieveit.mp3"),
    ICARE("sounds/bulldog/icare.mp3"),
    IMCOMING("sounds/bulldog/imcoming.mp3"),
    IMRATTING("sounds/bulldog/imratting.mp3"),
    KREYGASM("sounds/bulldog/kreygasm.mp3"),
    LULDOG("sounds/bulldog/luldog.mp3"),
    MOTHERCOMES("sounds/bulldog/mothercomes.mp3"),
    MOVEYOURASS("sounds/bulldog/moveyourass.mp3"),
    NOTLIKETHIS("sounds/bulldog/notlikethis.mp3"),
    NUTS("sounds/bulldog/nuts.mp3"),
    OHBABY("sounds/other/ohbaby.mp3"),
    OHMYGOD("sounds/bulldog/ohmygod.mp3"),
    OHNONONO("sounds/other/ohnonono.mp3"),
    OMEGAEZ("sounds/bulldog/omegaez.mp3"),
    PERMABAN("sounds/bulldog/permaban.mp3"),
    PLEBSAREDISGUSTING("sounds/bulldog/plebsaredisgusting.mp3"),
    PRAISE("sounds/bulldog/praise.mp3"),
    QUAS("sounds/other/quas.mp3"),
    RED("sounds/bulldog/red.mp3"),
    RONNIE("sounds/bulldog/ronnie.mp3"),
    ROONS("sounds/bulldog/roons.mp3"),
    RUN("sounds/bulldog/run.mp3"),
    SAUSAGE("sounds/bulldog/sausage.mp3"),
    SEEYA("sounds/bulldog/seeya.mp3"),
    SIKE("sounds/other/sike.mp3"),
    SLOWDOWN("sounds/bulldog/slowdown.mp3"),
    SURPRISE("sounds/other/surprise.mp3"),
    SYNDLUL("sounds/bulldog/syndlul.mp3"),
    THATSPOWER("sounds/bulldog/thatspower.mp3"),
    TP("sounds/bulldog/tp.mp3"),
    UGANDA("sounds/other/uganda.mp3"),
    UNLIMITEDPOWER("sounds/bulldog/unlimitedpower.mp3"),
    VIVON("sounds/other/vivon.mp3"),
    WARRIA("sounds/other/warria.mp3"),
    WASHEDUP("sounds/other/washedup.mp3"),
    WEED("sounds/other/weed.mp3"),
    WEX("sounds/other/wex.mp3"),
    WHYDIDYOUCOME("sounds/other/whydidyoucome.mp3"),
    WTFMAN("sounds/bulldog/wtfman.mp3"),
    YEEHAW("sounds/other/yeehaw.mp3"),
    YIKES("sounds/bulldog/yikes.mp3"),
    YOUKILLEDMYPEOPLE("sounds/other/youkilledmypeople.mp3");

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