package structs

import structs.Animation.AnimationId
import utils.LoadSave.File

abstract class AtlasInfo {
  val numOfColumns: Int
  val spriteDimsInAtlas: Dimension[Int]
  val animEnums: Seq[AnimationId]
  val atlasFile: File

  def numOfRows: Int = animEnums.length
}
