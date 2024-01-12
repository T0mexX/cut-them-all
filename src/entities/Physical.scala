package entities

import bonusGame.BonusGame
import levels.Level
import structs._
import utils.Constants.Physics
import utils.HelpMethods.rectVsRect

trait Physical extends Drawable { this: Entity =>
  val hitBoxDims: Dimension[Float]
  var flying: Boolean = true
  val ATK: Float
  val ATKKnockBack: Float


  def getHitBoxRect: Rect[Float] =
    Rect[Float](pos - (hitBoxDims.toVect / 2), hitBoxDims)

  def computeResultingCollisionVectors(xyDelta: Vect2D[Float], level: Level): Vect2D[Float] = {
    val tilesHitBoxes: Seq[Rect[Float]] = level.tileHitBoxesInRange(computeUpdateRange)
    var inelasticCollisionVect: Vect2D[Float] = Vect2D[Float](0f, 0f)
    var (xDone, yDone) = (false, false)

    val hitBoxRect: Rect[Float] = getHitBoxRect

    val collisionSeq: Seq[CollisionResult] = {
      tilesHitBoxes.map(tileHitBox => {
          rectVsRect(hitBoxRect, xyDelta, tileHitBox)
        }).filterNot(_.collisionVect == Vect2D[Float](0, 0))
        .sortWith(_.movAllowedBeforeCollision <= _.movAllowedBeforeCollision)
    }

    var xyDeltaCopy: Vect2D[Float] = xyDelta.copy()

    collisionSeq.foreach(c => {
      val finalColl: CollisionResult =
        rectVsRect(hitBoxRect, xyDeltaCopy, c.hitBox)

        applyNonElasticCollision(finalColl)
    })

    def applyNonElasticCollision(finalCollision: CollisionResult): Unit = {
      if (finalCollision.isOnXAxis) {
        if (xDone)
          return
        inelasticCollisionVect += finalCollision.collisionVect
        xyDeltaCopy += finalCollision.collisionVect
        xDone = true
      }
      else if (finalCollision.isOnYAxis){
        if (yDone)
          return
        inelasticCollisionVect += finalCollision.collisionVect
        xyDeltaCopy += finalCollision.collisionVect
        yDone = true
      }
    }

    inelasticCollisionVect
  }

  def hitWallOnTheRight(attemptedMovement: Vect2D[Float], movementAfterCollisions: Vect2D[Float]): Boolean =
    attemptedMovement.x > 0 && movementAfterCollisions.x <= 0

  def hitWallOnTheLeft(attemptedMovement: Vect2D[Float], movementAfterCollisions: Vect2D[Float]): Boolean =
    attemptedMovement.x < 0 && movementAfterCollisions.x >= 0f

  def hitCeiling(attemptedMovement: Vect2D[Float], movementAfterCollisions: Vect2D[Float]): Boolean =
    attemptedMovement.y < 0 && movementAfterCollisions.y >= 0

  def hitFloor(attemptedMovement: Vect2D[Float], movementAfterCollisions: Vect2D[Float]): Boolean =
    attemptedMovement.y > 0 && movementAfterCollisions.y <= 0

  def resetForceVectIfWallWasHit(attemptedMovement: Vect2D[Float], movementAfterCollisions: Vect2D[Float]): Unit = {
    if (hitWallOnTheRight(attemptedMovement, movementAfterCollisions))
      forceVect = forceVect.copy(x = 0f min forceVect.x)
    else if (hitWallOnTheLeft(attemptedMovement, movementAfterCollisions))
      forceVect = forceVect.copy(x = 0f max forceVect.x)

    if (hitCeiling(attemptedMovement, movementAfterCollisions))
      forceVect = forceVect.copy(y = 0f max forceVect.y)
    else if (hitFloor(attemptedMovement, movementAfterCollisions)) {
      forceVect = forceVect.copy(y = 0f min forceVect.y)
      flying = false
    }
  }

  def mitigateHorizontalForceVect(): Unit = {
    if (forceVect.x > 0)
      forceVect = Vect2D[Float]((forceVect.x - Physics.HorizontalForceVectReduction) max 0f, forceVect.y)
    else if (forceVect.x < 0)
      forceVect = Vect2D[Float]((forceVect.x + Physics.HorizontalForceVectReduction) min 0f, forceVect.y)
  }

  def setFlyingIfNeeded(resultingMovement: Vect2D[Float]): Unit =
    if (resultingMovement.y != 0f) flying = true
}
