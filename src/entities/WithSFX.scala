package entities

import bonusGame.BonusGame
import ddf.minim.AudioSample
import entities.WithSFX.{SFXId, nullSFXId}
import utils.LoadSave.File

trait WithSFX {
  val loadSFXFun: File => AudioSample
  val SFX: Seq[AudioSample]
  private var SFXOn: Boolean = true

  def getSFXSeq: Seq[AudioSample]

  def triggerSFX(sfxId: SFXId = nullSFXId): Unit = {
    if (SFXOn) SFX(sfxId.index).trigger()
  }

  def updateSFXVolume(percentage: Float): Unit = {
    SFX.foreach(_.setVolume(percentage)) //in percentage (often not working)
    SFX.foreach(_.setGain(BonusGame.getDecibels(percentage))) //in db
  }

  def setSFXOnOff(b: Boolean): Unit =
    SFXOn = b
}

object WithSFX {
  abstract class SFXId{
    def index: Int
  }
  private case object nullSFXId extends SFXId{override val index: Int = 0}
}
