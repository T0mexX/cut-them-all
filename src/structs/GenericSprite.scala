package structs

import entities.Drawable
import processing.core.PImage

case class GenericSprite(override val spriteToDraw: PImage,
                         var pos: Vect2D[Float]
                        ) extends Drawable
