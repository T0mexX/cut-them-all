package structs

import processing.core.PImage
import structs.Animation.AnimationId

case class Animation(sprites: Seq[PImage], frameInterval: Int, nextAnim: AnimationId) {
  private var currentSpriteIndex: Int = 0

  def nextSprite(): Unit = {
    currentSpriteIndex = {
      if (currentSpriteIndex >= sprites.length - 1)
        0
      else
        currentSpriteIndex + 1
    }
  }

  def getSprite: PImage =
    sprites(currentSpriteIndex)

  def reset(): Unit =
    currentSpriteIndex = 0

  def setFrame(i: Int): Unit =
    if (i < sprites.length) currentSpriteIndex = i

  def ended: Boolean =
    currentSpriteIndex == sprites.length - 1

  def getCompletionRate: Float =
    currentSpriteIndex.toFloat / sprites.length

  def getCurrentFrame: Int =
    currentSpriteIndex

  def isAnimationFinished: Boolean =
    currentSpriteIndex == sprites.length - 1
}


object Animation {
  abstract class AnimationId(val animIndex: Int = 0,
                               val numOfSprites: Int,
                               val duration: Float,
                               var nextAnimOption: Option[AnimationId] = None) {
    val nextAnim: AnimationId = nextAnimOption.getOrElse(this)
  }
}
