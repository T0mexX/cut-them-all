package entities

import bonusGame.BonusGame
import ddf.minim.AudioSample
import entities.Golem.{DeathSFX, DefaultATK, DefaultATKKnockBack, DefaultDims, DefaultHitBoxDimsOnDimsRatio, DefaultMaxHealth, Dying, Noise}
import entities.WithSFX.SFXId
import levels.Level
import levels.LevelManager.EntityId
import logic.BonusLogic
import processing.core.PImage
import structs.Animation.AnimationId
import structs.{Animation, AtlasInfo, Dimension, Vect2D}
import utils.LoadSave
import utils.LoadSave.{File, GolemDeathSFXFile, GolemNoise1File, GolemNoise2File, GolemNoise3File, GolemSpriteSheet, loadSFX}

import scala.util.Random

case class Golem(override var pos: Vect2D[Float],
                 override val dims: Dimension[Float] = DefaultDims,
                 override val loadImgWithPApplet: File => PImage,
                 override val imgResizeAndScale: Dimension[Float] => PImage => PImage,
                 override val getAnimationSeq: (PImage => PImage, AtlasInfo, PImage) => Seq[Animation],
                 override val loadSFXFun: File => AudioSample,
                 override val maxHealth: Float = DefaultMaxHealth
                ) extends Enemy with GroundBoundedMovement {

  override var movementVect: Vect2D[Float] = Vect2D[Float](0.8f, 0f)
  override val hitBoxDims: Dimension[Float] = dims * DefaultHitBoxDimsOnDimsRatio

  override val ATK: Float = DefaultATK
  override val ATKKnockBack: Float = DefaultATKKnockBack

  override def getAtlasInfo: AtlasInfo = Golem.atlasInfo

  override val SFX: Seq[AudioSample] = getSFXSeq
  override val noiseSFXId: SFXId = Noise
  override val deathSFXid: SFXId = DeathSFX

  override def setDeathAnim(): Unit =
    setAnimation(Dying)

  override def getSFXSeq: Seq[AudioSample] =
    Seq[AudioSample](
      loadSFXFun(GolemNoise1File),
      loadSFXFun(GolemNoise2File),
      loadSFXFun(GolemNoise3File),
      loadSFXFun(GolemDeathSFXFile)
    )

}

object Golem extends EntityId {
  val Id: Int = 48
  val DefaultDims: Dimension[Float] = Dimension[Float](56, 56)
  val DefaultMaxHealth: Float = 200
  val DefaultATK: Float = 40
  val DefaultATKKnockBack: Float = 500 / BonusGame.DefaultUpdatesPerSecond
  val DefaultHitBoxDimsOnDimsRatio: Vect2D[Float] = Vect2D[Float](1f, 1f)
  val atlasInfo: AtlasInfo = new AtlasInfo {
    override val numOfColumns: Int = 5
    override val spriteDimsInAtlas: Dimension[Int] = Dimension[Int](56, 56)
    override val atlasFile: File = GolemSpriteSheet
    override val animEnums: Seq[AnimationId] =
      Seq[AnimationId](
        Walking,
        Dying
      )
  }

  private case object Walking extends AnimationId(animIndex = 0, numOfSprites = 4, duration = 1f)
  private case object Dying extends AnimationId(animIndex = 1, numOfSprites = 5, duration = 1f)

  private val random = Random
  private case object Noise extends SFXId { def index: Int = random.between(0, 3)}
  private case object DeathSFX extends SFXId { val index: Int = 3}
}