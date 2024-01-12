package gamestates

import bonusGame.BonusGame
import logic.BonusLogic
import processing.core.{PApplet, PGraphics}
import processing.event.{KeyEvent, MouseEvent}
import structs.{Dimension, Rect, Vect2D}
import userInterface.Button._
import userInterface._

final class Menu(override val bonusLogic: BonusLogic) extends GameState {
  import gamestates.Menu._
  override val buttons: Seq[Button] = {
    Seq[Button](
      new Button(buttonType = PlayButton, id = Play, PlayBtnDefaultPos, BtnsDefaultDims, this)(),
      new Button(buttonType = OptionsButton, id = Options, OptionsBtnDefaultPos, BtnsDefaultDims, this)(),
      new Button(buttonType = QuitButton, id = Quit, QuitBtnDefaultPos, BtnsDefaultDims, this)()
    )
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    bonusLogic.drawBg(g, scale)
    super.draw(g, scale)
  }

  override def buttonClicked(id: ButtonId): Unit = {
    id match {
      case Play => bonusLogic.setGameState(GameActive)
      case Options => bonusLogic.setGameState(OptionsScreen)
      case Quit => bonusLogic.getBonusGame.exit()
    }
  }
}



object Menu extends GameStateId {
  import BonusGame.DefaultRes.res.width

  val BtnsDefaultDims = Dimension[Float](140, 56)
  val PlayBtnDefaultPos = Vect2D[Float](width.toFloat / 2, 175)
  val OptionsBtnDefaultPos = Vect2D[Float](width.toFloat / 2, 245)
  val QuitBtnDefaultPos = Vect2D[Float](width.toFloat / 2, 315)

  case object Play extends ButtonId
  case object Options extends ButtonId
  case object Quit extends ButtonId
}
