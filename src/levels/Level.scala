package levels

import bonusGame.BonusGame
import effects.Effect
import entities.{Drawable, Enemy, Entity, Golem, WithSFX}
import processing.core.{PGraphics, PImage}
import structs.{Dimension, Rect, Vect2D}


case class Level(tileGrid: Seq[Seq[Tile]], var entities: Seq[Entity], tileById: Seq[PImage], emptyTileId: Int) {

  val dimsInTiles: Dimension[Int] = Dimension[Int](tileGrid.head.length, tileGrid.length)
  private val tileDims = tileGrid.head.head.outline.dims

  def update(): Unit = {
    removeRemovableEntities()
    entities.foreach(_.update(this))
  }

  def draw(g: PGraphics, range: Rect[Float], scale: Float, lvlOffset: Vect2D[Float]): Unit = {
    tilesInRange(range).filterNot(isTileEmpty).foreach(_.draw(g, scale, lvlOffset))
    getDrawables(range).foreach(_.draw(g, scale, lvlOffset))
  }

  def getEnemies(range: Rect[Float]): Seq[Enemy] =
    entities.filter(e => e.isInstanceOf[Enemy] && range.contains(e.pos)).asInstanceOf[Seq[Enemy]]

  private def getDrawables(range: Rect[Float]): Seq[Drawable] =
    entities.filter(e => e.isInstanceOf[Drawable] && range.contains(e.pos)).asInstanceOf[Seq[Drawable]]

  def getEntitiesWithSFX: Seq[WithSFX] =
    entities.filter(_.isInstanceOf[WithSFX]).asInstanceOf[Seq[WithSFX]]

  def tileHitBoxesInRange(range: Rect[Float]): Seq[Rect[Float]] =
    tilesInRange(range).filter(_.tileId != emptyTileId).map(_.outline)

  def isTileEmptyAtPos(pos: Vect2D[Float]): Boolean = {
    val xIndex = (pos.x / tileDims.width).floor.toInt
    val yIndex = (pos.y / tileDims.height).floor.toInt

    if (dimsInTiles.contains(xIndex, yIndex))
      tileGrid(yIndex)(xIndex).tileId == emptyTileId
    else
      true
  }

  def addEntity(e: Entity): Unit =
    entities = entities :+ e

  def noEnemyLeft: Boolean =
    entities.forall(!_.isInstanceOf[Enemy])

  private def tilesInRange(range: Rect[Float]): Seq[Tile] = {
    val xRangeInTiles: Range =
      (range.topLeftPos.x / BonusGame.DefaultTileSize).toInt to (range.getBottomRightPos.x / BonusGame.DefaultTileSize).toInt
    val yRangeInTiles: Range =
      (range.topLeftPos.y / BonusGame.DefaultTileSize).toInt to (range.getBottomRightPos.y / BonusGame.DefaultTileSize).toInt

    tileGrid.slice(yRangeInTiles.start, yRangeInTiles.end)
      .flatMap(_.slice(xRangeInTiles.start, xRangeInTiles.end))
  }

  private def isTileEmpty(tile: Tile): Boolean =
    tile.tileId == emptyTileId

  private def removeRemovableEntities(): Unit =
    entities = entities.filterNot {
      case e: Enemy => e.isRemovable
      case a: Effect => a.isAnimFinished
    }
}
