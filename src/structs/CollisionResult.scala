package structs

case class CollisionResult(movAllowedBeforeCollision: Float,
                           collisionVect: Vect2D[Float] = Vect2D(0f, 0f),
                           hitBox: Rect[Float] = null
                          ) {
  def isOnYAxis: Boolean =
    collisionVect.x == 0 && collisionVect.y != 0

  def isOnXAxis: Boolean =
    collisionVect.x != 0 && collisionVect.y == 0
}

object CollisionResult {
  def nullCollision: CollisionResult =
    CollisionResult(0f)
}
