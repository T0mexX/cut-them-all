package userInterface

import processing.core.{PGraphics, PImage}
import structs.{Dimension, MouseInput, MouseMoved, MousePressed, MouseReleased, Rect, Vect2D}
import utils.LoadSave._
import Button._
import ddf.minim.AudioSample
import entities.WithSFX.SFXId
import entities.{Drawable, WithSFX}
import gamestates.GameState
import processing.event.MouseEvent
import utils.LoadSave

class Button(val buttonType: ButtonType,
             val id: ButtonId,
             var pos: Vect2D[Float] = Vect2D[Float](0f, 0f),
             val dims: Dimension[Float] = Dimension[Float](1, 1),
             val gameState: GameState,
            )
            (
              val loadSFXFun: File => AudioSample = gameState.getLoadSFXFun,
              val clickFun: () => Unit = () => gameState.buttonClicked(id)
            ) extends Drawable with WithSFX {
  private val mEventToPoint: MouseEvent => Vect2D[Float] = gameState.getLogic.mouseEventToPoint
  private var hitBox = Rect[Float](pos - (dims / 2).toVect, dims)
  val states: Seq[PImage] = getStateImages()
  var spriteToDraw: PImage = null
  var currentState: ButtonState = Idle

  val SFX: Seq[AudioSample] = getSFXSeq

  def draw(g: PGraphics, scale: Float): Unit = {
    spriteToDraw = getSpriteToDraw
    super.draw(g, scale)
  }

  def actOnMouseInput(mouseInput: MouseInput): Unit = {
    mouseInput match {
      case MouseMoved(e) => setStateOrElseIdle(mEventToPoint(e), MouseOver)
      case MousePressed(e) => setStateOrElseIdle(mEventToPoint(e), Pressed)
      case MouseReleased(e) => setStateOrElseIdle(mEventToPoint(e), Released)
      case _ => ()
    }
  }

  def setStateOrElseIdle(p: Vect2D[Float], state: ButtonState): Unit =
    if (contains(p)) setState(state) else setState(Idle)

  def contains(point: Vect2D[Float]): Boolean =
    hitBox.contains(point)

  def contains(posX: Float, posY: Float): Boolean =
    hitBox.contains(Vect2D[Float](posX, posY))

  def updatePos(posX: Float, posY: Float): Unit = {
    pos = Vect2D[Float](posX, posY)
    hitBox = Rect[Float](pos - (dims / 2).toVect, dims)
  }

  def getStateImages(rowIndex: Int = buttonType.rowIndex): Seq[PImage] = {
    val atlas: PImage = gameState.getLogic.loadImgWithPApplet(buttonType.file)
    val defaultSize: Dimension[Int] = buttonType.defaultDims
    val scaleFun: PImage => PImage = gameState.getLogic.imgResizeAndScale(dims)

    (0 until 3).map(columnIndex => {
      val defaultImg: PImage = {
        atlas.get(
          columnIndex * defaultSize.width,
          rowIndex * defaultSize.height,
          defaultSize.width,
          defaultSize.height
        )
      }

      scaleFun(defaultImg)
    })
  }

  def copy(btnType: ButtonType = buttonType,
           id: ButtonId = id,
           pos: Vect2D[Float] = pos,
           dims: Dimension[Float] = dims,
           gs: GameState = gameState
          )
          (
          clickFun: () => Unit = clickFun
          ): Button = new Button(btnType, id, pos, dims, gs)(clickFun = clickFun)

  def getSpriteToDraw: PImage =
    states(currentState.index)

  private def setState(state: ButtonState): Unit = {
    state match {
      case MouseOver => if (currentState != Pressed) currentState = MouseOver
      case Released => if (currentState == Pressed) clickFun() else currentState = Idle
      case Pressed => currentState = Pressed; triggerSFX()
      case _ => currentState = state
    }
  }


  def getSFXSeq: Seq[AudioSample] = {
    Seq[AudioSample](
      loadSFXFun(ButtonSFXFile)
    )
  }
}


object Button {
  def apply(buttonType: ButtonType, id: ButtonId, outline: Rect[Float], gameState: GameState): Button =
    new Button(buttonType, id, outline.centerPos, outline.dims, gameState)()

  sealed abstract class ButtonState(val index: Int)

  case object Idle extends ButtonState(0)
  case object MouseOver extends ButtonState(1)
  case object Pressed extends ButtonState(2)
  case object Released extends ButtonState(0)


  abstract sealed class ButtonType(val defaultDims: Dimension[Int], val file: File, val rowIndex: Int = 0)
  case object PlayButton extends ButtonType(Dimension[Int](140, 56), MenuButtonsAtlas, 0)
  case object OptionsButton extends ButtonType(Dimension[Int](140, 56), MenuButtonsAtlas, 1)
  case object QuitButton extends ButtonType(Dimension[Int](140, 56), MenuButtonsAtlas, 2)

  case object ResumeButton extends ButtonType(Dimension[Int](56, 56), UrmButtonAtlas, 0)
  case object ReturnButton extends ButtonType(Dimension[Int](56, 56), UrmButtonAtlas, 1)
  case object HomeButton extends ButtonType(Dimension[Int](56, 56), UrmButtonAtlas, 2)

  case object SoundButton extends ButtonType(Dimension(42, 42), SoundButtonAtlas, 0)
  case object ScrollButton extends ButtonType(Dimension[Int](28, 44), ScrollBarAtlas)

  case object _832x364Button extends ButtonType(Dimension[Int](140, 56), ResolutionSelectionAtlas, 0)
  case object _1664x728Button extends ButtonType(Dimension[Int](140, 56), ResolutionSelectionAtlas, 1)
  case object FSButton extends ButtonType(Dimension[Int](140, 56), ResolutionSelectionAtlas, 2)

  abstract class ButtonId
}
