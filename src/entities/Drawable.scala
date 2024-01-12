package entities

import bonusGame.BonusGame
import processing.core.{PApplet, PGraphics, PImage}
import structs._
import utils.LoadSave.File

trait Drawable {
  def pos: Vect2D[Float]
  def spriteToDraw: PImage


  def draw(g: PGraphics, scale: Float, offset: Vect2D[Float] = Vect2D(0f, 0f)): Unit = {
    val spriteWidth: Float = spriteToDraw.width.toFloat
    val spriteHeight: Float = spriteToDraw.height.toFloat
    g.image(
      spriteToDraw,
      ((pos.x - offset.x) * scale - spriteWidth / 2).ceil,
      ((pos.y - offset.y) * scale - spriteHeight / 2).ceil
    )
  }
}
