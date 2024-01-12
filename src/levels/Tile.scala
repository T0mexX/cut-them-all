package levels

import entities.Drawable
import processing.core.PImage
import structs.{Dimension, Rect, Vect2D}

case class Tile(tileId: Int, var spriteToDraw: PImage, outline: Rect[Float]) extends Drawable{
  val spriteDims: Dimension[Float] = outline.dims
  override val pos: Vect2D[Float] = outline.centerPos
}
