package userInterface

import ddf.minim.AudioSample
import gamestates.GameState
import processing.core.{PGraphics, PImage}
import structs.{Dimension, Vect2D}
import userInterface.Button.{ButtonId, ButtonType}
import utils.LoadSave.File

import scala.language.implicitConversions


case class ToggleButton(btnType: ButtonType,
                        btnId: ButtonId,
                        position: Vect2D[Float],
                        dimensions: Dimension[Float],
                        gs: GameState,
                        var isOff: Boolean,
                       ) extends Button(btnType, btnId, position, dimensions, gs)() {


  override val clickFun: () => Unit = () => {
    isOff = !isOff
    gameState.buttonClicked(id)
  }

  override val states: Seq[PImage] =
    getStateImages() ++ getStateImages(buttonType.rowIndex + 1)

  override def getSpriteToDraw: PImage =
    states(currentState.index + (3 * isOff))

  implicit def boolToInt(b: Boolean): Int = if (b) 1 else 0
}
