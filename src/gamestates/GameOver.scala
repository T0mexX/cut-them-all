package gamestates

import bonusGame.BonusGame
import gamestates.GameOver.{DefaultBtnsDims, DefaultHomeBtnPos, DefaultReplayBtnPos, DefaultTabBGPosAndDims, Home, Replay}
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import structs.{Dimension, GenericSprite, Vect2D}
import userInterface.Button
import userInterface.Button.{ButtonId, HomeButton, ResumeButton}
import utils.LoadSave.{GameOverTabBG, PauseTabBG}

final class GameOver(override val bonusLogic: BonusLogic, gameActive: GameActive) extends GameState {

  private val gameOverTabBG: GenericSprite = getGameOverTabBG

  override val buttons: Seq[Button] = {
    Seq[Button](
      new Button(ResumeButton, Replay, DefaultReplayBtnPos, DefaultBtnsDims, this)(),
      new Button(HomeButton, Home, DefaultHomeBtnPos, DefaultBtnsDims, this)()
    )
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    gameActive.draw(g, scale)
    gameOverTabBG.draw(g, scale)
    super.draw(g, scale)
  }

  override def buttonClicked(buttonId: ButtonId): Unit = {
    buttonId match {
      case Replay => bonusLogic.setGameState(GameActive)
      case Home => bonusLogic.setGameState(Menu)
    }
  }

  private def getGameOverTabBG: GenericSprite = {
    val scaleFun: PImage => PImage = bonusLogic.imgResizeAndScale(DefaultTabBGPosAndDims._2)
    val defaultSprite = bonusLogic.loadImgWithPApplet(GameOverTabBG)
    GenericSprite(
      scaleFun(defaultSprite),
      DefaultTabBGPosAndDims._1,
    )
  }
}

object GameOver extends GameStateId {
  import BonusGame.DefaultRes.res.width
  private val DefaultTabBGPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](width.toFloat / 2, 225), Dimension[Float](235, 225))
  private val DefaultBtnsDims = Dimension[Float](42, 42)
  private val DefaultHomeBtnPos = Vect2D[Float](363, 240)
  private val DefaultReplayBtnPos = Vect2D[Float](472, 240)

  private case object Replay extends ButtonId
  private case object Home extends ButtonId
}