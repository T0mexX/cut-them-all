package entities

import bonusGame.BonusGame
import ddf.minim.AudioSample
import entities.WithSFX.SFXId
import processing.core.PGraphics
import structs.Vect2D

import scala.util.Random

abstract class Enemy extends Entity
                        with Physical
                        with Animated
                        with DirectionDependent
                        with HealthBar
                        with WithSFX {
  val noiseSFXId: SFXId
  val deathSFXid: SFXId
  override val invulnerabilityFrames: Int = (BonusGame.DefaultUpdatesPerSecond / 4).toInt
  private var updatesUntilNextSFX: Int = BonusGame.DefaultUpdatesPerSecond.toInt
  var movementVect: Vect2D[Float]
  val random: Random = Random

  def setDeathAnim(): Unit

  def triggerDeathSound(): Unit =
    triggerSFX(deathSFXid)

  def triggerSFXIfIsTimeTo(): Unit = {
    if (updatesUntilNextSFX <= 0) {
      triggerSFX(noiseSFXId)
      updatesUntilNextSFX =
        random.between(
          BonusGame.DefaultUpdatesPerSecond.toInt * 4,
          BonusGame.DefaultUpdatesPerSecond.toInt * 8
        )
    }
    updatesUntilNextSFX -= 1
  }

  def isRemovable: Boolean =
    this.isDead && this.isAnimFinished

  override def draw(g: PGraphics, scale: Float, lvlOffset: Vect2D[Float]): Unit = {
    drawHealthBar(g, scale, lvlOffset)
    super.draw(g, scale, lvlOffset)
  }
}