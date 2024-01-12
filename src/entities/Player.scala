package entities


import bonusGame.BonusGame
import ddf.minim.AudioSample
import entities.DirectionDependent._
import entities.WithSFX.SFXId
import levels.Level
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import processing.event.{KeyEvent, MouseEvent}
import structs.Animation.AnimationId
import structs._
import utils.Constants.Physics
import utils.LoadSave.{File, Grunt1File, Grunt2File, Grunt3File, JumpSFXFile, PlayerAtlasFile, Swing1SFXFile, Swing2SFXFile, Swing3SFXFile, getAtlas}
import utils.HelpMethods.getAnimationSeq
import utils.HelpMethods.roundFloatVectToFewerDecimalDigits

import java.awt.event.KeyEvent._
import java.awt.event.MouseEvent._
import scala.util.Random

case class Player(override var pos: Vect2D[Float],
                  override val dims: Dimension[Float],
                  override val loadImgWithPApplet: File => PImage,
                  override val imgResizeAndScale: Dimension[Float] => PImage => PImage,
                  override val getAnimationSeq: (PImage => PImage, AtlasInfo, PImage) => Seq[Animation],
                  override val loadSFXFun: File => AudioSample,
                  override val maxHealth: Float = 100
                 )  extends Entity
                    with Physical with Animated
                    with DirectionDependent
                    with HealthBar
                    with WithSFX {
  import Player._

  override val SFX: Seq[AudioSample] = getSFXSeq

  override val ATK: Float = DefaultATK
  override val ATKKnockBack: Float = DefaultATKKnockBack
  private val ATKHitBoxDims: Dimension[Float] = dims * ATKHitBoxDimsToPlayerDimsRatio
  private val ATKPosOffset: Vect2D[Float] = dims.toVect * ATKHitBoxOffsetToPlayerDimsRatio

  override val hitBoxDims: Dimension[Float] = Dimension[Float](13f, 32f)
  private var (leftBtn, rightBtn) = (false, false)
  private var crouchBtn = false
  setAnimation(Standing)

  override def getAtlasInfo: AtlasInfo = Player.atlasInfo

  override def damage(dmg: Float): Unit = {
    if (remainingInvFrames <= 0) {
      remainingInvFrames = invulnerabilityFrames
      super.damage(dmg)
      triggerSFX(DamagedSFX)
    }
  }

  override def draw(g: PGraphics, scale: Float, lvlOffset: Vect2D[Float]): Unit = {
    super.draw(g, scale, lvlOffset)
    drawHealthBar(g, scale, lvlOffset)
  }

  def update(level: Level): Unit = {
    updateInvulnerability()
    forceVect += Physics.Gravity
    val previousAnimId: AnimationId = currentAnimId

    updateAnimAndVectors()

    val attemptedMovement: Vect2D[Float] = {
      whichMovementDirection match {
        case Left => Vect2D[Float](-GroundMovementSpeed, 0)
        case Right => Vect2D[Float](GroundMovementSpeed, 0)
        case _ => Vect2D(0f, 0f)
      }
    }

    val attemptedMovWithFApplied: Vect2D[Float] = attemptedMovement + forceVect

    val inelasticRebound: Vect2D[Float] =
      computeResultingCollisionVectors(attemptedMovWithFApplied, level)

    val resultingMovement: Vect2D[Float] = attemptedMovWithFApplied + inelasticRebound

    pos += resultingMovement

    val resultingMovRounded = roundFloatVectToFewerDecimalDigits(resultingMovement)

    resetForceVectIfWallWasHit(attemptedMovWithFApplied, resultingMovRounded)
    mitigateHorizontalForceVect()
    updateFacingDirection(attemptedMovement)
    setFlyingIfNeeded(resultingMovRounded)

    if (currentAnimId == previousAnimId)
      nextFrame()
  }

  def isAttacking: Boolean =
    currentAnimId.isInstanceOf[Swing]

  def getAttackHitBox: Rect[Float] = {
    val topLeftPos: Vect2D[Float] =  {
      facingDirection match {
        case FacingEast => pos - ATKPosOffset - ATKHitBoxDims.toVect / 2
        case FacingWest =>  pos + ATKPosOffset - ATKHitBoxDims.toVect / 2
      }

    }
    Rect[Float](topLeftPos, ATKHitBoxDims)
  }


  def actOnKBInput(kBInput: KBInput): Unit = {
    kBInput match {
      case KeyPressed(e) => keyPressed(e)
      case KeyReleased(e) => keyReleased(e)
    }
  }

  def actOnMouseInput(mouseInput: MouseInput): Unit = {
    mouseInput match {
      case MousePressed(e) => mousePressed(e)
      case _ => ()
    }
  }

  private def keyPressed(e: KeyEvent): Unit = {
    e.getKeyCode match {
      case VK_A => leftBtn = true
      case VK_D => rightBtn = true
      case VK_SPACE => jumpPressed()
      case VK_CONTROL => crouchBtn = true
      case _ => ()
    }
  }

  private def keyReleased(e: KeyEvent): Unit = {
    e.getKeyCode match {
      case VK_A => leftBtn = false
      case VK_D => rightBtn = false
      case VK_CONTROL => crouchBtn = false
      case _ => ()
    }
  }

  private def mousePressed(e: MouseEvent): Unit = {
     e.getButton match {
      case VK_LEFT => attackPressed()
      case _ => ()
    }
  }

  private def jumpPressed(): Unit = {
    if (flying) return

    currentAnimId match {
      case Crouching =>
        forceVect += JumpForce * ChargedJumpMultiplier * getAnimationCompletionRate
      case Crouched => forceVect += JumpForce * ChargedJumpMultiplier
      case _ => forceVect += JumpForce
    }

    flying = true
    triggerSFX(JumpSFX)
    if (currentAnimId.isInstanceOf[Swing])
      setAnimation(FallingSwing, getCurrentAnimFrame)
    else
      setAnimation(Jumping)
  }

  private def attackPressed(): Unit = {
    def setSwingIfNotSwinging(animId: AnimationId): Unit = {
      if (!currentAnimId.isInstanceOf[Swing]) {
        setAnimation(animId)
        triggerSFX(SwingSFX)
      }
    }

    (flying, whichMovementDirection) match {
      case (true, _) => setSwingIfNotSwinging(FallingSwing)
      case (_, NoDirection) => setSwingIfNotSwinging(StandingSwing)
      case _ => setSwingIfNotSwinging(RunningSwing)
    }
  }

  private def updateAnimAndVectors(): Unit = {
    def setSwingSpecificAnimation(customizable: SwingCustomizable): Unit = {
      (customizable, currentAnimId) match {
        case (Standing, _: Swing) => setAnimation(StandingSwing, getCurrentAnimFrame)
        case (Running, _: Swing) => setAnimation(RunningSwing, getCurrentAnimFrame)
        case (Falling, _: Swing) => setAnimation(FallingSwing, getCurrentAnimFrame)
        case _ => setAnimation(customizable.getId)

      }
    }

    val direction: Direction = whichMovementDirection

    (flying, crouchBtn) match {
      case (true, _) =>
        forceVect += Physics.Gravity
        setSwingSpecificAnimation(Falling)
      case (false,  true) if whichMovementDirection == NoDirection =>
        if (currentAnimId != Crouched)
          setAnimation(Crouching)
      case _ if currentAnimId.isInstanceOf[Swing] =>

      case _ =>
        if (direction == NoDirection)
          setSwingSpecificAnimation(Standing)
        else
          setSwingSpecificAnimation(Running)
    }
  }

  private def whichMovementDirection: Direction = {
    (leftBtn, rightBtn) match {
      case (true, false) => Left
      case (false, true) => Right
      case _ => NoDirection
    }
  }

  override def getSFXSeq: Seq[AudioSample] = {
    Seq[AudioSample](
      loadSFXFun(JumpSFXFile),
      loadSFXFun(Grunt1File),
      loadSFXFun(Grunt2File),
      loadSFXFun(Grunt3File),
      loadSFXFun(Swing1SFXFile),
      loadSFXFun(Swing2SFXFile),
      loadSFXFun(Swing3SFXFile)
    )
  }
}

object Player {
  private val GroundMovementSpeed: Float = 120 / BonusGame.DefaultUpdatesPerSecond
  private val JumpForce: Vect2D[Float] = Vect2D[Float](0, -380f) / BonusGame.DefaultUpdatesPerSecond
  private val ChargedJumpMultiplier: Float = 1.45f
  private val DefaultATK: Float = 45f
  private val ATKHitBoxOffsetToPlayerDimsRatio = Vect2D[Float](0.5f, 0f)
  private val ATKHitBoxDimsToPlayerDimsRatio = Vect2D[Float](1f, 1f)
  private val DefaultATKKnockBack: Float = 400 / BonusGame.DefaultUpdatesPerSecond

  val atlasInfo: AtlasInfo = new AtlasInfo {
    override val atlasFile: File = PlayerAtlasFile
    override val numOfColumns: Int = 4
    override val spriteDimsInAtlas: Dimension[Int] = Dimension[Int](32, 32)
    override val animEnums: Seq[AnimationId] =
      Seq[AnimationId](
        Standing,
        Running,
        Jumping,
        Crouching,
        Crouched,
        Falling,
        StandingSwing,
        RunningSwing,
        FallingSwing
      )
  }
  private trait Swing {this: AnimationId => def getId: AnimationId = this}
  private trait SwingCustomizable{this: AnimationId => def getId: AnimationId = this}

  private case object Standing extends AnimationId(animIndex = 0, numOfSprites = 2, duration = 0.5f) with SwingCustomizable
  private case object Running extends AnimationId(animIndex = 1, numOfSprites = 4, duration = 0.66f) with SwingCustomizable
  private case object Jumping extends AnimationId(animIndex = 2, numOfSprites = 4, duration = 0.66f, nextAnimOption = Some(Falling))
  private case object Crouching extends AnimationId(animIndex = 3, numOfSprites = 4, duration = 0.5f, nextAnimOption = Some(Crouched))
  private case object Crouched extends AnimationId(animIndex = 4, numOfSprites = 4, duration = 1f)
  private case object Falling extends AnimationId(animIndex = 5, numOfSprites = 2, duration = 0.5f) with SwingCustomizable

  private case object StandingSwing extends AnimationId(animIndex = 6, numOfSprites = 4, duration = 0.5f, nextAnimOption = Some(Standing)) with Swing
  private case object RunningSwing extends AnimationId(animIndex = 7, numOfSprites = 4, duration = 0.5f, nextAnimOption = Some(Running)) with Swing
  private case object FallingSwing extends AnimationId(animIndex = 8, numOfSprites = 4, duration = 0.5f, nextAnimOption = Some(Falling)) with Swing

  private val random = Random
  private case object JumpSFX extends SFXId {val index = 0}
  private case object DamagedSFX extends SFXId {def index: Int = random.between(1, 4)}
  private case object SwingSFX extends SFXId {def index: Int = random.between(4, 7)}
}


