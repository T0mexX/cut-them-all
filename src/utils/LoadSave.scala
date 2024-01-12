package utils

import ddf.minim.{AudioPlayer, AudioSample, Minim}
import processing.core.{PApplet, PImage}

object LoadSave {

  def getAtlas(papplet: PApplet, file: File): PImage =
    papplet.loadImage(papplet.sketchPath() + file.path)

  def loadMusic(papplet: PApplet, minim: Minim, volumePercentage: Float, file: File): AudioPlayer = {
    val audioSample = minim.loadFile(file.path)
    audioSample.setVolume(volumePercentage)

    audioSample
  }



  def loadSFX(papplet: PApplet, minim: Minim, volumePercentage: Float)(file: File): AudioSample = {
    val audioSample = minim.loadSample(file.path)
    audioSample.setVolume(volumePercentage)

    audioSample
  }

  sealed abstract class File(val path: String)

  case object PlayerAtlasFile extends File("/src/resources/playerAnimations1_1x1_32x32.png")
  case object TilesAtlas extends File("/src/resources/defaultTileAtlas.png")
  case object MenuButtonsAtlas extends File("/src/resources/menuButtonsAtlas.png")
  case object OptionsTabBG extends File("/src/resources/optionsTabBG.png")
  case object PauseTabBG extends File("/src/resources/pauseTabBG.png")
  case object ScrollBarAtlas extends File("/src/resources/scrollBarAtlas.png")
  case object SoundButtonAtlas extends File("/src/resources/soundButtonAtlas.png")
  case object UrmButtonAtlas extends File("/src/resources/urmButtonsAtlas.png")
  case object LvlCompletedTabBG extends File("/src/resources/lvlCompletedTabBG.png")
  case object ResolutionSelectionAtlas extends File("/src/resources/resolutionSelectionAtlas.png")
  case object HealthBarSprite extends File("/src/resources/healthBarSprite.png")
  case object GolemSpriteSheet extends File("/src/resources/golemSpriteSheet.png")
  case object HitEffectSpriteSheet extends File("/src/resources/hitEffectSpriteSheet.png")
  case object SmallCloud extends File("/src/resources/smallCloud.png")
  case object GameOverTabBG extends File("/src/resources/gameOverTabBG.png")

  case object Music extends File("src/resources/SFX/music.wav")
  case object Hit1SFXFile extends File("src/resources/SFX/hit1SFX.wav")
  case object Hit2SFXFile extends File("src/resources/SFX/hit2SFX.wav")
  case object Hit3SFXFile extends File("src/resources/SFX/hit3SFX.wav")
  case object JumpSFXFile extends File("src/resources/SFX/jumpSFX.wav")
  case object GolemNoise1File extends File("src/resources/SFX/golemNoise1.wav")
  case object GolemNoise2File extends File("src/resources/SFX/golemNoise2.wav")
  case object GolemNoise3File extends File("src/resources/SFX/golemNoise3.wav")
  case object GolemDeathSFXFile extends File("src/resources/SFX/golemDeathSFX.wav")
  case object ButtonSFXFile extends File("src/resources/SFX/buttonSFX.wav")
  case object Grunt1File extends File("src/resources/SFX/grunt1.wav")
  case object Grunt2File extends File("src/resources/SFX/grunt2.wav")
  case object Grunt3File extends File("src/resources/SFX/grunt3.wav")
  case object Swing1SFXFile extends File("src/resources/SFX/swing1.wav")
  case object Swing2SFXFile extends File("src/resources/SFX/swing2.wav")
  case object Swing3SFXFile extends File("src/resources/SFX/swing3.wav")


val levelsGridsRGB: Seq[File] =
  Seq[File](
    new File("/src/resources/lvls/2.png"){},
  )
}


