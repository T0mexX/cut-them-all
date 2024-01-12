package gamestates

import bonusGame.BonusGame
import gamestates.LevelCompleted.{DefaultBtnsDims, DefaultHomeBtnPos, DefaultNextLvlBtnPos, Home, NextLvl}
import gamestates.Paused.DefaultTabBGPosAndDims
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import structs.{Dimension, GenericSprite, Vect2D}
import userInterface.Button
import userInterface.Button.{ButtonId, HomeButton, ResumeButton}
import utils.LoadSave.{LvlCompletedTabBG, PauseTabBG}

final class LevelCompleted(override val bonusLogic: BonusLogic, gameActive: GameActive) extends GameState {

  private val lvlCompletedTabBG: GenericSprite = getLvlCompletedTabBG

  override val buttons: Seq[Button] = {
    Seq[Button](
      new Button(ResumeButton, id = NextLvl, DefaultNextLvlBtnPos, DefaultBtnsDims, gameState = this)(),
      new Button(HomeButton, id = Home, DefaultHomeBtnPos, DefaultBtnsDims, gameState = this)()
    )
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    gameActive.draw(g, scale)
    lvlCompletedTabBG.draw(g, scale)
    super.draw(g, scale)
  }

  override def buttonClicked(buttonId: ButtonId): Unit = {
    buttonId match {
      case NextLvl => bonusLogic.nextLvl()
      case Home => bonusLogic.setGameState(Menu)
    }
  }

  private def getLvlCompletedTabBG: GenericSprite = {
    val scaleFun: PImage => PImage = bonusLogic.imgResizeAndScale(LevelCompleted.DefaultTabBGPosAndDims._2)
    val defaultSprite = bonusLogic.loadImgWithPApplet(LvlCompletedTabBG)
    GenericSprite(
      scaleFun(defaultSprite),
      LevelCompleted.DefaultTabBGPosAndDims._1,
    )
  }
}


object LevelCompleted extends GameStateId {

  import BonusGame.DefaultRes.res.width

  private val DefaultTabBGPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](width.toFloat / 2, 225), Dimension[Float](258, 389))
  private val DefaultBtnsDims = Dimension[Float](42, 42)
  private val DefaultNextLvlBtnPos = Vect2D[Float](492, 355)
  private val DefaultHomeBtnPos = Vect2D[Float](343, 355)

  private case object NextLvl extends ButtonId
  private case object Home extends ButtonId
}