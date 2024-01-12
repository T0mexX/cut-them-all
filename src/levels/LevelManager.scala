package levels

import bonusGame.BonusGame
import entities.{Enemy, Entity, Golem}
import levels.LevelManager.{NullTileId, isEntity}
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import structs.{Dimension, Rect, Vect2D}
import utils.{HelpMethods, LoadSave}
import utils.LoadSave.{File, levelsGridsRGB}


class LevelManager(bonusLogic: BonusLogic) {
  private var currentLvlNum: Int = 0
  private val tileSpritesById: Seq[PImage] = computeTileSpritesById


  def nextLvl: Level = {
    currentLvlNum += 1
    buildAndGetLvl
  }

  def getLvl(lvlNum: Int): Level = {
    currentLvlNum = lvlNum
    buildAndGetLvl
  }

  def getLvl: Level =
    buildAndGetLvl

  def setLvl(newLvlNum: Int): Unit =
    currentLvlNum = newLvlNum

  def thereIsANextLvl: Boolean =
    currentLvlNum < levelsGridsRGB.length - 1


  private def buildAndGetLvl: Level = {
    val (tileGrid: Seq[Seq[Tile]], entities: Seq[Entity]) = computeTileGridAndEntities(currentLvlNum)
    Level(tileGrid, entities, tileSpritesById, NullTileId)
  }

  private def computeTileSpritesById: Seq[PImage] = {
    val imgScaleFun: PImage => PImage =
      bonusLogic.imgScale

    val tilesAtlas: PImage =
      bonusLogic.loadImgWithPApplet(LoadSave.TilesAtlas)

    val tileSize: Int = BonusGame.DefaultTileSize.toInt

    val atlasWidthInTiles: Int = tilesAtlas.width / BonusGame.DefaultTileSize.toInt
    val atlasHeightInTiles: Int = tilesAtlas.height / BonusGame.DefaultTileSize.toInt

    val xTileIterable: Iterable[Int] =
      0 until atlasWidthInTiles

    val yTileIterable: Iterable[Int] =
      0 until atlasHeightInTiles

    val tileSpriteArr: Array[PImage] = Array.ofDim[PImage](atlasWidthInTiles * atlasHeightInTiles)

    for (xIndex <- xTileIterable; yIndex <- yTileIterable) {
      val tileId: Int = xIndex + yIndex * atlasWidthInTiles

      val tileSprite: PImage = {
        imgScaleFun(tilesAtlas.get(xIndex * tileSize, yIndex * tileSize, tileSize, tileSize))

      }
      tileSpriteArr(tileId) = tileSprite
    }

    tileSpriteArr.toSeq
  }

  private def computeTileGridAndEntities(lvlNum: Int): (Seq[Seq[Tile]], Seq[Entity]) = {
    val tileDataImg: PImage =
      bonusLogic.loadImgWithPApplet(levelsGridsRGB(lvlNum))

    val tileArr: Array[Array[Tile]] =
      Array.ofDim[Tile](tileDataImg.height, tileDataImg.width)
    var entitiesSeq = Seq[Entity]()

    val tileSize: Float = BonusGame.DefaultTileSize

    for (x <- 0 until tileDataImg.width; y <- 0 until tileDataImg.height) {
      val id: Int = bonusLogic.getBonusGame.getGraphics.red(tileDataImg.get(x, y)).toInt
      val tileId = {
        if (isEntity(id)) {
          entitiesSeq = entitiesSeq :+ getNewEntity(id, Vect2D(x * tileSize + tileSize/2, y * tileSize + tileSize/2))
          NullTileId
        } else id
      }

      tileArr(y)(x) =
        Tile(
          tileId = tileId,
          spriteToDraw = tileSpritesById(tileId),
          outline = Rect(x * tileSize, y * tileSize, tileSize, tileSize)
        )
    }

    (tileArr.map(_.toSeq).toSeq, entitiesSeq)
  }

  private def getNewEntity(id: Int, pos: Vect2D[Float]): Entity = {
    id match {
      case Golem.Id => getNewGolem(pos)
      case _ => throw new RuntimeException("MEOW")
    }
  }

  private def getNewGolem(pos: Vect2D[Float]): Golem = {
    Golem(
      pos = pos,
      dims = Golem.DefaultDims,
      bonusLogic.loadImgWithPApplet,
      bonusLogic.imgResizeAndScale,
      HelpMethods.getAnimationSeq(bonusLogic.getUps),
      loadSFXFun = bonusLogic.loadSFXFun
    )
  }
}

object LevelManager {
  private val EntityIds = Seq[EntityId](Golem)
  private val NullTileId: Int = 11

  abstract class EntityId {def Id: Int}

  private def isEntity(id: Int): Boolean =
    EntityIds.exists(_.Id == id)
}