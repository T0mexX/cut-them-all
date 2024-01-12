package entities

import bonusGame.BonusGame
import structs.{Dimension, Vect2D}
import DirectionDependent._
import processing.core.{PApplet, PGraphics}

trait DirectionDependent extends Drawable {
  this: Drawable with Entity =>
  var facingDirection: FacingDirection = FacingWest

  override def draw(g: PGraphics, scale : Float, offset: Vect2D[Float] = Vect2D(0f, 0f)): Unit = {
    val spriteWidth: Float = spriteToDraw.width.toFloat
    val spriteHeight: Float = spriteToDraw.height.toFloat
    if (facingDirection == FacingEast)
      drawReversed(g, offset, scale)
    else {
      g.image(
        spriteToDraw,
        (pos.x - offset.x) * scale - spriteWidth / 2 ,
        (pos.y - offset.y)  * scale - spriteHeight / 2,
      )
    }

    def drawReversed(g: PGraphics, offset: Vect2D[Float], scale: Float): Unit = {
      g.pushMatrix()
      g.scale(-1, 1)
      g.image(
        spriteToDraw,
        ((-pos.x + offset.x) * scale - spriteWidth / 2).ceil,
        ((pos.y - offset.y) * scale - spriteHeight / 2).ceil,
      )
      g.popMatrix()
    }
  }

  def updateFacingDirection(attemptedMovement: Vect2D[Float]): Unit = {
    attemptedMovement.x match {
      case 0f => ()
      case x if x < 0 => facingDirection = FacingEast
      case _ => facingDirection = FacingWest
    }
  }
}

object DirectionDependent {
  sealed abstract class FacingDirection {
    def convertPosAndDims(pos: Vect2D[Float], dims: Dimension[Float]): (Vect2D[Float], Dimension[Float])
  }

  case object FacingEast extends FacingDirection {
    override def convertPosAndDims(pos: Vect2D[Float], dims: Dimension[Float]): (Vect2D[Float], Dimension[Float]) =
      (pos + Vect2D[Float](dims.width, 0), Dimension[Float](-dims.width, dims.height))
  }

  case object FacingWest extends FacingDirection {
    override def convertPosAndDims(pos: Vect2D[Float], dims: Dimension[Float]): (Vect2D[Float], Dimension[Float]) =
      (pos, dims)
  }
}