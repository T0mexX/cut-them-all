package effects
import effects.HitEffect.{DefaultDims, atlasInfo}
import levels.Level
import processing.core.PImage
import structs.Animation.AnimationId
import structs.{Animation, AtlasInfo, Dimension, Vect2D}
import utils.LoadSave
import utils.LoadSave.{File, HitEffectSpriteSheet}

case class HitEffect(override var pos: Vect2D[Float],
                override val dims: Dimension[Float] = DefaultDims,
                override val loadImgWithPApplet: File => PImage,
                override val imgResizeAndScale: Dimension[Float] => PImage => PImage,
                override val getAnimationSeq: (PImage => PImage, AtlasInfo, PImage) => Seq[Animation],
               ) extends Effect {
  override def getAtlasInfo: AtlasInfo = atlasInfo

  override def update(level: Level): Unit =
    nextFrame()
}

object HitEffect {
  private val DefaultDims = Dimension[Float](42, 106)
  private val atlasInfo = new AtlasInfo {
    override val numOfColumns: Int = 5
    override val spriteDimsInAtlas: Dimension[Int] = Dimension[Int](42, 106)
    override val animEnums: Seq[AnimationId] = Seq[AnimationId](new AnimationId(numOfSprites = 5, duration = 0.25f){})
    override val atlasFile: File = HitEffectSpriteSheet
  }
}
