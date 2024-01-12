package gamestates

import logic.BonusLogic
import bonusGame.BonusGame
import ddf.minim.AudioSample
import processing.core.PGraphics
import processing.event.KeyEvent
import processing.event.MouseEvent
import structs.{KBInput, MouseInput, Vect2D}
import userInterface.{Button, ScrollBar}
import userInterface.Button.{ButtonId, ButtonState, Idle, MouseOver, Pressed, Released}
import utils.LoadSave.File

abstract class GameState {
  val bonusLogic: BonusLogic
  val buttons: Seq[Button] = Seq[Button]()
  val scrollBars: Map[ButtonId, ScrollBar] = Map[ButtonId, ScrollBar]()

  def getLoadSFXFun: File => AudioSample =
    bonusLogic.loadSFXFun

  def update(): Unit = ()

  def buttonClicked(buttonId: ButtonId): Unit

  def draw(g: PGraphics, scale: Float): Unit = {
    buttons.foreach(_.draw(g, scale))
    scrollBars.values.foreach(_.draw(g, scale))
  }

  def actOnMouseInput(mouseInput: MouseInput): Unit = {
    buttons.foreach(_.actOnMouseInput(mouseInput))
    scrollBars.values.foreach(_.actOnMouseInput(mouseInput))
  }

  def updateSFXVolume(percentage: Float): Unit = {
    buttons.foreach(_.updateSFXVolume(percentage))
    scrollBars.values.foreach(_.updateSFXVolume(percentage))
  }

  def setSFXOnOff(b: Boolean): Unit = {
    buttons.foreach(_.setSFXOnOff(b))
    scrollBars.values.foreach(_.setSFXOnOff(b))
  }

  def actOnKBInput(KBInput: KBInput): Unit = ()

  def getLogic: BonusLogic = bonusLogic
}

abstract class GameStateId
