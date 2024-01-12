package userInterface

import gamestates.GameState
import processing.core.{PGraphics, PImage}
import processing.event.MouseEvent
import structs.{Dimension, GenericSprite, MouseDragged, MouseInput, MouseMoved, MousePressed, MouseReleased, Vect2D}
import userInterface.Button.{ButtonId, Idle, MouseOver, Pressed, Released, ScrollButton}
import userInterface.ScrollBar.{BtnImgDims, BtnToBarScale}
import utils.LoadSave.ScrollBarAtlas

case class ScrollBar(id: ButtonId,
                     pos: Vect2D[Float],
                     dims: Dimension[Float],
                     gameState: GameState,
                     var percentage: Float
                    ) {
  private val mEventToPoint: MouseEvent => Vect2D[Float] = gameState.getLogic.mouseEventToPoint
  private val (leftScrollBound, rightScrollBound): (Float, Float) =
    (pos.x - dims.width * 0.45f, pos.x + dims.width * 0.45f)
  private val barSprite: GenericSprite = getBarSprite

  private val scrollBtn = new Button(ScrollButton, id = null, pos, dims * BtnToBarScale, gameState)(clickFun = btnClicked) {
    override def actOnMouseInput(mouseInput: MouseInput): Unit = {
      mouseInput match {
        case MouseMoved(e) => setStateOrElseIdle(mEventToPoint(e), MouseOver)
        case MousePressed(e) => setStateOrElseIdle(mEventToPoint(e), Pressed)
        case MouseReleased(e) => setStateOrElseIdle(mEventToPoint(e), Released)
        case MouseDragged(e) =>
          if (currentState == Pressed) {
            updatePos(leftScrollBound max mEventToPoint(e).x min rightScrollBound, pos.y)
          }
      }
    }
  }

  def draw(g: PGraphics, scale: Float): Unit = {
    barSprite.draw(g, scale)
    scrollBtn.draw(g, scale)
  }

  def actOnMouseInput(mouseInput: MouseInput): Unit =
    scrollBtn.actOnMouseInput(mouseInput)

  def updateSFXVolume(percentage: Float): Unit =
    scrollBtn.updateSFXVolume(percentage)

  def setSFXOnOff(b: Boolean): Unit =
    scrollBtn.setSFXOnOff(b)

  private def btnClicked(): Unit = {
    percentage = (scrollBtn.pos.x - leftScrollBound) / (rightScrollBound - leftScrollBound)
    gameState.buttonClicked(id)
  }

  private def getBarSprite: GenericSprite = {
    val atlas: PImage = gameState.getLogic.loadImgWithPApplet(ScrollBarAtlas)
    val widthOccupiedByButton: Int = BtnImgDims.width * 3
    val barImg: PImage = atlas.get(widthOccupiedByButton, 0, atlas.width - widthOccupiedByButton, atlas.height)

    GenericSprite(
      gameState.getLogic.imgResizeAndScale(dims)(barImg),
      pos
    )
  }
}

object ScrollBar {
  val BtnImgDims = Dimension[Int](28, 44)
  val BtnToBarScale = Vect2D[Float](28f / 215f, 0.8f)
}

