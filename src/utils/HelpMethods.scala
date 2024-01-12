package utils

import processing.core.{PApplet, PImage}
import processing.event.MouseEvent
import structs.Animation.AnimationId
import structs.{Animation, AtlasInfo, CollisionResult, Dimension, Rect, Vect2D}

import scala.collection.View.FlatMap

object HelpMethods {

  def getAnimationSeq(ups: Float)(imgScalingFun: PImage => PImage, atlasInfo: AtlasInfo, img: PImage): Seq[Animation] = {
    val spriteDims: Dimension[Int] = atlasInfo.spriteDimsInAtlas
    val animEnums: Seq[AnimationId] = atlasInfo.animEnums
    animEnums.map(animEnum => {
      val row: Int = animEnum.animIndex
      val sprites: Seq[PImage] = {
        (0 until animEnum.numOfSprites).map(column => {
          imgScalingFun(
            img.get(
              column * spriteDims.width,
              row * spriteDims.height,
              spriteDims.width,
              spriteDims.height
            )
          )
        })
      }
      val frameInterval: Int = (ups / animEnum.numOfSprites * animEnum.duration).toInt
      Animation(sprites, frameInterval, animEnum.nextAnim)
    })
  }

  def rectVsRect(rect1: Rect[Float], movementVect: Vect2D[Float], rect2: Rect[Float]): CollisionResult = {
    if (movementVect.x == 0f && movementVect.y == 0f)
      return CollisionResult.nullCollision

    val expandedTargetRect: Rect[Float] =
      rect2.expand(rect1.dims.width, rect1.dims.height)

    vectVsRect(rect1.centerPos, movementVect, expandedTargetRect).copy(hitBox = rect2)
  }

  def vectVsRect(pos: Vect2D[Float],  vect: Vect2D[Float], targetRect: Rect[Float]): CollisionResult = {
    val relativeTargetRect: Rect[Float] =
      Rect(targetRect.topLeftPos - pos, targetRect.dims)

    if (vect.x == 0 && vect.y == 0)
      return CollisionResult.nullCollision

    val bottomRightPos: Vect2D[Float] =
      relativeTargetRect.getBottomRightPos

    val collisionVect1: Vect2D[Float] = relativeTargetRect.topLeftPos / vect
    val collisionVect2: Vect2D[Float] = bottomRightPos / vect

    if (collisionVect1.x.isNaN || collisionVect1.y.isNaN || collisionVect2.x.isNaN || collisionVect2.y.isNaN)
      return CollisionResult.nullCollision


    val nearCollisionVect: Vect2D[Float] =
      Vect2D[Float](
        collisionVect1.x min collisionVect2.x,
        collisionVect1.y min collisionVect2.y
      )
    val farCollisionVect: Vect2D[Float] =
      Vect2D[Float](
        collisionVect1.x max collisionVect2.x,
        collisionVect1.y max collisionVect2.y
      )

    if (nearCollisionVect.x > farCollisionVect.y || nearCollisionVect.y > farCollisionVect.x)
      return CollisionResult.nullCollision

    val movAllowedBeforeCollision: Float =
      nearCollisionVect.x max nearCollisionVect.y

    if (movAllowedBeforeCollision < 0f || movAllowedBeforeCollision >= 1f)
      return CollisionResult.nullCollision

    if (nearCollisionVect.x > nearCollisionVect.y)
      CollisionResult(
        movAllowedBeforeCollision,
        Vect2D[Float](-vect.x * (1f - movAllowedBeforeCollision), 0),
        targetRect
      )
    else
      CollisionResult(
        movAllowedBeforeCollision,
        Vect2D[Float](0, -vect.y * (1f - movAllowedBeforeCollision)),
        targetRect
      )
  }

  def rectsAreColliding(rect1: Rect[Float], rect2: Rect[Float]): Boolean = {
    val expandedRect2: Rect[Float] =
      rect2.expand(rect1.dims.width, rect1.dims.height)

    expandedRect2.contains(rect1.centerPos)
  }

  def getDistanceVect(pos1: Vect2D[Float], pos2: Vect2D[Float]): Vect2D[Float] =
    pos2 - pos1

  private def roundFloatToFewerDecimalDigits(float: Float): Float =
    float + 1000f - 1000f

  def roundFloatVectToFewerDecimalDigits(vect: Vect2D[Float]): Vect2D[Float] =
    Vect2D(roundFloatToFewerDecimalDigits(vect.x), roundFloatToFewerDecimalDigits(vect.y))
}
