package utils

import bonusGame.BonusGame
import structs.Vect2D

object Constants {
  object Physics {
    final val Gravity: Vect2D[Float] = Vect2D[Float](0f, 3f) / BonusGame.DefaultUpdatesPerSecond
    final val HorizontalForceVectReduction: Float = 10f / BonusGame.DefaultUpdatesPerSecond
  }
}
