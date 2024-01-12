package gamestates

import bonusGame.BonusGame
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import structs.{Dimension, GenericSprite, KBInput, KeyPressed, KeyReleased, Vect2D}
import userInterface.{Button, ToggleButton}
import userInterface.Button.{ButtonId, HomeButton, ResumeButton, ReturnButton, SoundButton}
import utils.LoadSave.{OptionsTabBG, PauseTabBG}

import java.awt.event.KeyEvent._

class Paused(override val bonusLogic: BonusLogic, previousGameState: GameState) extends GameState {
import Paused._

  private val bonusGame: BonusGame = bonusLogic.getBonusGame
  private val pausedTabBG: GenericSprite = getPausedTabBG

  override val buttons: Seq[Button] = {
    Seq[Button](
      new Button(ResumeButton, Resume, DefaultResumeBtnPos, DefaultBtnsDims, this)(),
      new Button(ReturnButton, Replay, DefaultReplayBtnPos, DefaultBtnsDims, this)(),
      new Button(HomeButton, Home, DefaultHomeBtnPos, DefaultBtnsDims, this)(),
      ToggleButton(SoundButton, btnId = MusicToggle, DefaultMusicBtnPosAndDims._1, DefaultMusicBtnPosAndDims._2, this, !bonusGame.isMusicOn),
      ToggleButton(SoundButton, btnId = SFXToggle, DefaultSFXBtnPosAndDims._1, DefaultSFXBtnPosAndDims._2, this, !bonusGame.isSFXOn)
    )
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    previousGameState.draw(g, scale)
    pausedTabBG.draw(g, scale)
    super.draw(g, scale)
  }

  override def buttonClicked(buttonId: Button.ButtonId): Unit = {
    buttonId match {
      case Resume => bonusLogic.setGameState(previousGameState)
      case Home => bonusLogic.setGameState(Menu)
      case Replay => bonusLogic.setGameState(GameActive)
      case SFXToggle => bonusGame.toggleSFX()
      case MusicToggle => bonusGame.toggleMusic()
    }
  }

  override def setSFXOnOff(b: Boolean): Unit = {
    previousGameState.setSFXOnOff(b)
    super.setSFXOnOff(b)
  }

  override def updateSFXVolume(percentage: Float): Unit = {
    previousGameState.updateSFXVolume(percentage)
    super.updateSFXVolume(percentage)
  }

  private def getPausedTabBG: GenericSprite = {
    val scaleFun: PImage => PImage = bonusLogic.imgResizeAndScale(DefaultTabBGPosAndDims._2)
    val defaultSprite = bonusLogic.loadImgWithPApplet(PauseTabBG)
    GenericSprite(
      scaleFun(defaultSprite),
      DefaultTabBGPosAndDims._1,
    )
  }

  override def actOnKBInput(KBInput: KBInput): Unit = {
    KBInput match {
      case KeyPressed(e) if e.getKeyCode == VK_ESCAPE => bonusLogic.setGameState(previousGameState)
      case _ => ()
    }
  }
}

object Paused extends GameStateId {
  import BonusGame.DefaultRes.res.width
  private val DefaultTabBGPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](width.toFloat / 2, 225), Dimension[Float](258, 389))
  private val DefaultBtnsDims = Dimension[Float](42, 42)
  private val DefaultHomeBtnPos = Vect2D[Float](343, 355)
  private val DefaultReplayBtnPos = Vect2D[Float](417, 355)
  private val DefaultResumeBtnPos = Vect2D[Float](492, 355)

  private val DefaultMusicBtnPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](470, 168), Dimension[Float](42, 42))
  private val DefaultSFXBtnPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](470, 213), Dimension[Float](42, 42))

  private case object Home extends ButtonId
  private case object Replay extends ButtonId
  private case object Resume extends ButtonId

  case object SFXToggle extends ButtonId
  case object MusicToggle extends ButtonId
}
