package entities

import processing.core.PImage
import structs.{Animation, AtlasInfo, Dimension}
import structs.Animation.AnimationId
import utils.LoadSave.File

trait Animated extends Drawable {this: Entity =>
  def getAnimationSeq: (PImage => PImage, AtlasInfo, PImage) => Seq[Animation]
  def loadImgWithPApplet: File => PImage
  def imgResizeAndScale: Dimension[Float] => PImage => PImage
  def getAtlasInfo: AtlasInfo

  private val animations: Seq[Animation] = {
    val atlasInfo = getAtlasInfo
    getAnimationSeq(imgResizeAndScale(dims), atlasInfo, loadImgWithPApplet(atlasInfo.atlasFile))
  }

  var currentAnimId: AnimationId = null
  private var currentAnim: Animation = animations.head
  var spriteToDraw: PImage = currentAnim.getSprite
  private var frameCounter: Int = 0

  def setAnimation(AnimId: AnimationId, startingFrame: Int = 0): Unit = {
    if (AnimId != currentAnimId) {
      currentAnimId = AnimId
      currentAnim = animations(AnimId.animIndex)
      currentAnim.setFrame(startingFrame)
      spriteToDraw = currentAnim.getSprite
    }
  }

  def nextFrame(): Unit = {
    frameCounter += 1
    if (frameCounter >= currentAnim.frameInterval) {
      if (currentAnim.ended) {
        setAnimation(currentAnim.nextAnim)
        currentAnim.reset()
      } else
        currentAnim.nextSprite()
      spriteToDraw = currentAnim.getSprite
      frameCounter = 0
    }
  }

  def getAnimationCompletionRate: Float =
    currentAnim.getCompletionRate

  def getCurrentAnimFrame: Int =
    currentAnim.getCurrentFrame

  def isAnimFinished: Boolean =
    currentAnim.isAnimationFinished
}
