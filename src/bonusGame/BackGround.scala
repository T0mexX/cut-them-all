package bonusGame

import bonusGame.BackGround.{CloudMovementSpeed, DefaultDims, DefaultOffsetPercentage}
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import structs.{Dimension, GenericSprite, Vect2D}
import utils.LoadSave.{OptionsTabBG, SmallCloud}

case class BackGround(bonusLogic: BonusLogic) {

  private val clouds: Seq[GenericSprite] = {
    val rand = scala.util.Random
    val xRange = 0 to bonusLogic.getWidth by 400
    val yRange = 0 to bonusLogic.getHeight by 200
    val scaleFun: PImage => PImage = bonusLogic.imgResizeAndScale(DefaultDims)
    val defaultSprite = bonusLogic.loadImgWithPApplet(SmallCloud)
    xRange.flatMap(x => {
      yRange.map(y => {
        val randX = x + rand.between(-200, 200)
        val randY = y + rand.between(-100, 100)
        GenericSprite(
          scaleFun(defaultSprite),
          Vect2D(randX.toFloat, randY.toFloat)
        )
      })
    })
  }

  def update(): Unit =
    clouds.foreach(c => c.pos = c.pos.move(deltaX = CloudMovementSpeed))

  def draw(g: PGraphics, scale: Float, lvlOffset: Vect2D[Float]): Unit = {
    g.fill(135f, 206f, 235f)
    g.rect(0, 0, bonusLogic.getWidth.toFloat, bonusLogic.getHeight.toFloat)
    val winWidth = bonusLogic.getWidth.toFloat
    val winHeight = bonusLogic.getHeight.toFloat

    val translatedClouds: Seq[GenericSprite] = {
      clouds.map(c => {
        c.copy(
          pos = Vect2D[Float](
            x = (c.pos.x - lvlOffset.x / 2) % (winWidth * 1.1f) - winWidth * DefaultOffsetPercentage,
            y = (c.pos.y - lvlOffset.y) % (winHeight * 1.1f)
          )
        )
      })
    }

    translatedClouds.foreach(_.draw(g, scale))
  }
}

object BackGround {
  private val DefaultDims = Dimension[Float](74, 26)
  private val DefaultOffsetPercentage = 0.05f
  private val CloudMovementSpeed: Float = 30 / BonusGame.DefaultUpdatesPerSecond
}
