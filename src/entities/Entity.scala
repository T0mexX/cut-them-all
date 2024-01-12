package entities

import bonusGame.BonusGame
import levels.Level
import processing.core.PImage
import structs._
import utils.LoadSave.File

abstract class Entity {
  var pos: Vect2D[Float]
  var forceVect: Vect2D[Float] = Vect2D[Float](0f, 0f)
  val dims: Dimension[Float]

  def setPos(newPos: Vect2D[Float]): Unit =
    pos = newPos

  def update(level: Level): Unit

  def applyForce(force: Vect2D[Float]): Unit =
    forceVect += force

  def computeUpdateRange: Rect[Float] = {
    val topLeftPos: Vect2D[Float] = pos.move(-dims.width, -dims.height)
    val rangeDims = Dimension[Float](dims.width * 3, dims.height * 3)

    Rect[Float](topLeftPos, rangeDims)
  }
}
