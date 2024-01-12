package userInterface

import entities.Drawable
import gamestates.GameState
import processing.core.{PGraphics, PImage}
import processing.event.MouseEvent
import structs.{Dimension, MouseInput, Vect2D}
import userInterface.Button.ButtonId
import userInterface.DropDownMenu.MainButton

case class DropDownMenu(pos: Vect2D[Float],
                        dims: Dimension[Float],
                        private var buttons: Seq[Button],
                        gameState: GameState) {
  resizeButtons()
  private var isOpen: Boolean = false
  private var mainButton: Button = new Button(buttons.head.buttonType, MainButton, pos, dims, gameState)(clickFun = () => btnClicked(MainButton))

  def draw(g: PGraphics, scale: Float): Unit = {
    mainButton.draw(g, scale)
    if (isOpen) {
      buttons.foreach(_.draw(g, scale))
    }
  }

  def actOnMouseInput(mouseInput: MouseInput): Unit = {
    mainButton.actOnMouseInput(mouseInput)
    if (isOpen)
      buttons.foreach(_.actOnMouseInput(mouseInput))
  }

  private def btnClicked(buttonId: ButtonId): Unit = {
    buttonId match {
      case MainButton => isOpen = !isOpen
      case _ if isOpen =>
        isOpen = !isOpen
        mainButton = getNewMainBtnFromId(buttonId)
        gameState.buttonClicked(buttonId)
      case _ => ()
    }
  }

  private def resizeButtons(): Unit = {
    val distanceBetweenElems = Vect2D[Float](0f, dims.height - 1)
    var posAcc = pos

    buttons = buttons.map( b  => {
      posAcc += distanceBetweenElems

      if (b.dims != dims)
        b.copy(dims = this.dims, pos = posAcc)(() => btnClicked(b.id))
      else
        b.copy(pos = posAcc)(() => btnClicked(b.id))
    })
  }

  private def getNewMainBtnFromId(buttonId: ButtonId): Button =
    buttons.find(_.id == buttonId).get.copy(pos = pos)(() => btnClicked(MainButton))

}

object DropDownMenu {
  case object MainButton extends ButtonId

}
