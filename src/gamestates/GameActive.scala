package gamestates

import bonusGame.BonusGame
import ddf.minim.AudioSample
import effects.HitEffect
import entities.WithSFX.SFXId
import entities.{Enemy, Golem, Player}
import gamestates.GameActive.{HitSFX, KnockBackMinYComponent}
import levels.{Level, LevelManager}
import logic.BonusLogic
import processing.core.{PApplet, PGraphics, PImage}
import processing.event.KeyEvent

import java.awt.event.KeyEvent._
import structs.{Animation, AtlasInfo, Dimension, GenericSprite, KBInput, KeyPressed, KeyReleased, MouseInput, Rect, Vect2D}
import userInterface.Button
import utils.HelpMethods
import utils.HelpMethods.{getDistanceVect, rectsAreColliding}
import utils.LoadSave.{Hit1SFXFile, Hit2SFXFile, Hit3SFXFile}

import scala.util.Random

final class GameActive(lvl: Level, override val bonusLogic: BonusLogic) extends GameState {
  private var lvlOffset = Vect2D[Float](0f, 0f)
  private val getAnimationSeqFun: (PImage => PImage, AtlasInfo, PImage) => Seq[Animation] =
    HelpMethods.getAnimationSeq(bonusLogic.getUps)

  val SFX: Seq[AudioSample] = getSFXSeq

  private val player: Player = {
    new Player(
      pos = Vect2D[Float](300, 200),
      dims = Dimension[Float](32, 32),
      bonusLogic.loadImgWithPApplet,
      bonusLogic.imgResizeAndScale,
      getAnimationSeqFun,
      getLoadSFXFun
    )
  }

  override def update(): Unit = {
    lvl.update()
    player.update(lvl)
    updateLvlOffset()
    solveEntityCollisions()
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    bonusLogic.drawBg(g, scale, lvlOffset)
    lvl.draw(g, getDisplayRange, scale, lvlOffset)
    player.draw(g, scale, lvlOffset)
  }

  override def buttonClicked(buttonId: Button.ButtonId): Unit = ???

  override def actOnKBInput(KBInput: KBInput): Unit = {
    KBInput match {
      case KeyPressed(e) =>
        if (e.getKeyCode == VK_ESCAPE)
          bonusLogic.setGameState(new Paused(bonusLogic, previousGameState = this))
        else
          player.actOnKBInput(KBInput)
      case _ => player.actOnKBInput(KBInput)
    }
  }

  override def actOnMouseInput(mouseInput: MouseInput): Unit =
    player.actOnMouseInput(mouseInput)

  override def updateSFXVolume(percentage: Float): Unit = {
    player.updateSFXVolume(percentage)
    lvl.getEntitiesWithSFX.foreach(_.updateSFXVolume(percentage))
  }

  override def setSFXOnOff(b: Boolean): Unit = {
    player.setSFXOnOff(b)
    lvl.getEntitiesWithSFX.foreach(_.setSFXOnOff(b))
  }

  private def updateLvlOffset(): Unit = {
    val leftCameraBound: Float = lvlOffset.x + BonusGame.DefaultWindowSize.width / 3
    val rightCameraBound: Float = leftCameraBound + BonusGame.DefaultWindowSize.width / 3
    val newYLvlOffset: Float = (player.pos.y - BonusGame.DefaultWindowSize.height / 2)
      .min(lvl.dimsInTiles.height * BonusGame.DefaultTileSize - BonusGame.DefaultWindowSize.height)
      .max(0)

    val newXLvlOffset: Float = {
      if (player.pos.x < leftCameraBound) {
        (lvlOffset.x + player.pos.x - leftCameraBound) max 0
      }
      else if (player.pos.x > rightCameraBound) {
        (lvlOffset.x + player.pos.x - rightCameraBound)
          .min(lvl.dimsInTiles.width * BonusGame.DefaultTileSize - BonusGame.DefaultWindowSize.width)
      }
      else lvlOffset.x
    }
    lvlOffset = Vect2D[Float](newXLvlOffset, newYLvlOffset)
  }

  private def getDisplayRange: Rect[Float] = {
    val topLeftPos: Vect2D[Float] = lvlOffset.move(-50f, -50f)
    val rangeDims = Dimension[Float](bonusLogic.getWidth + 100f, bonusLogic.getHeight + 100f)

    Rect[Float](topLeftPos, rangeDims)
  }

private def getPlayerCollisionRange: Rect[Float] = {
    val windWidth: Int = bonusLogic.getWidth
    val topLeftPos: Vect2D[Float] = player.pos.move(-windWidth * 0.2f, -windWidth * 0.1f)
    val rangeDims = Dimension[Float](windWidth * 0.3f, windWidth * 0.15f)

    Rect[Float](topLeftPos, rangeDims)
}

  private def solveEntityCollisions(): Unit = {
    if (lvl.noEnemyLeft) {
      bonusLogic.setGameState(new LevelCompleted(bonusLogic, gameActive = this))
      return
    }

    val enemies: Seq[Enemy] = lvl.getEnemies(getPlayerCollisionRange).filter(_.isAlive)

    def solvePlayerATKvsEnemies(): Unit = {
      val ATKHitBox: Rect[Float] = player.getAttackHitBox
      enemies.foreach(e => {
        if (rectsAreColliding(ATKHitBox, e.getHitBoxRect) && e.isNotInvulnerable) {
          lvl.addEntity(
            HitEffect(
              pos = e.pos,
              loadImgWithPApplet = bonusLogic.loadImgWithPApplet,
              imgResizeAndScale = bonusLogic.imgResizeAndScale,
              getAnimationSeq = getAnimationSeqFun
            )
          )
          triggerSFX(HitSFX)
          e.damage(player.ATK)
          if (e.isDead) {
            e.setDeathAnim()
            e.triggerDeathSound()
          }
          else {
            val normDistVect = getDistanceVect(player.pos, e.pos).normalized
            e.applyForce(
              normDistVect.copy(
                x = normDistVect.x * player.ATKKnockBack,
                y = (normDistVect.y * player.ATKKnockBack) max KnockBackMinYComponent)
            )
          }
        }
      })
    }

    if (player.isAttacking)
      solvePlayerATKvsEnemies()

    val playerHitBox: Rect[Float] = player.getHitBoxRect
    enemies.foreach(e => {
      if (rectsAreColliding(playerHitBox, e.getHitBoxRect) && player.isNotInvulnerable) {
        player.damage(e.ATK)
        if (player.isDead)
          bonusLogic.setGameState(new GameOver(bonusLogic, gameActive = this))

        val normDistanceVect = getDistanceVect(e.pos, player.pos).normalized // * Vect2D(e.ATKKnockBack, e.ATKKnockBack / 2)//Vect2D[Float](getDistanceVect(e.pos, player.pos).normalized.x * e.ATKKnockBack, KnockBackVectorY)
        player.applyForce(
          normDistanceVect.copy(
            normDistanceVect.x * e.ATKKnockBack,
            (normDistanceVect.y * e.ATKKnockBack) min KnockBackMinYComponent
          )
        )
      }
    })
  }

  private def triggerSFX(sfxId: SFXId): Unit =
    SFX(sfxId.index).trigger()

  private def getSFXSeq: Seq[AudioSample] = {
    Seq[AudioSample](
      getLoadSFXFun(Hit1SFXFile),
      getLoadSFXFun(Hit2SFXFile),
      getLoadSFXFun(Hit3SFXFile)
    )
  }
}

object GameActive extends GameStateId {
  private val KnockBackMinYComponent: Float = -250 / BonusGame.DefaultUpdatesPerSecond
  private val random: Random = Random
  private case object HitSFX extends SFXId {def index: Int = random.between(0, 3)}
}
