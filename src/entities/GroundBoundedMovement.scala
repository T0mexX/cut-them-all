package entities

import levels.Level
import processing.core.PGraphics
import structs.Vect2D
import utils.Constants.Physics
import utils.HelpMethods.roundFloatVectToFewerDecimalDigits

trait GroundBoundedMovement {this: Enemy =>

  override def update(lvl: Level): Unit = {
    if (this.isDead) {
      nextFrame()
      return
    }
    updateInvulnerability()
    def isThereNotFloorOnTheRight: Boolean =
      lvl.isTileEmptyAtPos(getHitBoxRect.getBottomRightPos.move(0, 1))

    def isThereNotFloorOnTheLeft: Boolean =
      lvl.isTileEmptyAtPos(getHitBoxRect.getBottomLeftPos.move(0, 1))

    forceVect += Physics.Gravity

    val attemptedMovement: Vect2D[Float] = movementVect

    val attemptedMovWithFApplied: Vect2D[Float] = attemptedMovement + forceVect

    val inelasticRebound: Vect2D[Float] =
      computeResultingCollisionVectors(attemptedMovWithFApplied, lvl)

    val resultingMovement: Vect2D[Float] = attemptedMovWithFApplied + inelasticRebound

    setPos(pos + resultingMovement)

    val resultingMovRounded = roundFloatVectToFewerDecimalDigits(resultingMovement)

    resetForceVectIfWallWasHit(attemptedMovWithFApplied, resultingMovRounded)
    updateFacingDirection(attemptedMovement)
    setFlyingIfNeeded(resultingMovRounded)
    mitigateHorizontalForceVect()
    triggerSFXIfIsTimeTo()

    if (hitWallOnTheRight(attemptedMovWithFApplied, resultingMovRounded) || hitWallOnTheLeft(attemptedMovWithFApplied, resultingMovRounded))
      movementVect = movementVect.copy(x = -movementVect.x)

    if (!flying && (isThereNotFloorOnTheLeft || isThereNotFloorOnTheRight))
      movementVect = movementVect.copy(x = -movementVect.x)

    nextFrame()
  }
}
