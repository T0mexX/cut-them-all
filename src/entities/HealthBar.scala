package entities

import bonusGame.BonusGame
import entities.HealthBar.DefaultHealthBarHeight
import gamestates.OptionsScreen.DefaultTabBGPosAndDims
import processing.core.{PGraphics, PImage}
import structs.{Dimension, GenericSprite, Vect2D}
import utils.LoadSave

trait HealthBar {this: Entity with Animated with Physical  =>
  private def healthBarSprite: GenericSprite = getHealthBarSprite
  def maxHealth: Float
  private var health: Float = maxHealth
  val invulnerabilityFrames: Int = (BonusGame.DefaultUpdatesPerSecond / 20).toInt
  var remainingInvFrames: Int = 0

  def isInvulnerable: Boolean =
    remainingInvFrames > 0

  def isNotInvulnerable: Boolean =
    remainingInvFrames <= 0

  def updateInvulnerability(): Unit =
    remainingInvFrames -= 1

  def damage(dmg: Float): Unit = {
    health = (health - dmg) max 0f
    remainingInvFrames = invulnerabilityFrames
  }

  def drawHealthBar(g: PGraphics, scale: Float, lvlOffset: Vect2D[Float]): Unit = {
    healthBarSprite.draw(g, scale, lvlOffset)
    g.fill(255f, 0f, 0f)
    g.rect(
      (healthBarSprite.pos.x - lvlOffset.x) * scale - healthBarSprite.spriteToDraw.width.toFloat / 2f + 1,
      (healthBarSprite.pos.y - lvlOffset.y) * scale - healthBarSprite.spriteToDraw.height.toFloat / 2f + 1,
      (healthBarSprite.spriteToDraw.width.toFloat - 2) * (health / maxHealth),
      healthBarSprite.spriteToDraw.height.toFloat - 2
    )
  }

  def isAlive: Boolean =
    health > 0

  def isDead: Boolean =
    health <= 0

  private def getHealthBarSprite: GenericSprite = {
    val healthBarDims = Dimension[Float](dims.width + dims.width * 0.2f, DefaultHealthBarHeight)
    val scaleFun: PImage => PImage = imgResizeAndScale(healthBarDims)
    val defaultImg: PImage = loadImgWithPApplet(LoadSave.HealthBarSprite)
    val healthBarPos: Vect2D[Float] = pos - Vect2D[Float](0, dims.height * 0.75f)

    GenericSprite(
      scaleFun(defaultImg),
      healthBarPos
    )
  }
}

object HealthBar {
  val DefaultHealthBarHeight: Float = 5f
}
