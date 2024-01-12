package bonusGame

import engine.GameBase
import processing.core.PApplet
import processing.event.{KeyEvent, MouseEvent}
import BonusGame._
import com.jogamp.newt.Display
import logic.BonusLogic
import structs.{Dimension, KeyPressed, KeyReleased, MouseDragged, MouseMoved, MousePressed, MouseReleased}

import java.awt.event.KeyEvent._

class BonusGame extends GameBase {
  private val updateTimer = new UpdateTimer(fps = DefaultFramesPerSeconds, ups = DefaultUpdatesPerSecond)
  var bonusLogic: BonusLogic = null
  private var currentResolution: Resolution = DefaultRes
  private var currentScale: Float = 1f
  var musicVolume: Float = 0.5f
  var SFXVolume: Float = 0.5f
  var isMusicOn = true
  var isSFXOn = true

  override def draw(): Unit = {
    if (updateTimer.timeForNextUpdate) {
      bonusLogic.update()
      updateTimer.advanceUpdate()
    }

    if (updateTimer.timeForNextFrame) {
      clear()
      bonusLogic.draw(getGraphics)
      updateTimer.advanceFrame()
    }
  }


  override def setup(): Unit = {
    updateTimer.init()
    surface.setResizable(true)
    surface.setLocation(0, 0)
    frameRate(DefaultUpdatesPerSecond + 1)
    bonusLogic = new BonusLogic(this, currentScale, DefaultUpdatesPerSecond)
  }

  override def settings(): Unit = {
    size(DefaultRes.res.width, DefaultRes.res.height)
    pixelDensity(displayDensity())
  }

  def changeResolution(newRes: Resolution): Unit = {
    bonusLogic.minim.dispose()
    bonusLogic = {
      newRes match {
        case FullScreen =>
          currentResolution = FullScreen
          currentScale = {
            displayWidth.toFloat / DefaultRes.res.width.toFloat
          }

          //if set to displaySize it goes into fullscreen and you are not able to exit fullscreen
          surface.setSize(displayWidth - 1, displayHeight - 1)
          new BonusLogic(this, currentScale, DefaultUpdatesPerSecond)
        case _ =>
          currentResolution = newRes
          currentScale = {
            newRes.res.width.toFloat / DefaultRes.res.width.toFloat
          }
          surface.setSize(newRes.res.width, newRes.res.height)
          new BonusLogic(this, currentScale, DefaultUpdatesPerSecond)
      }
    }

    setMusicVolume(musicVolume)
    setSFXVolume(SFXVolume)
    bonusLogic.setSFXOnOff(isSFXOn)
    bonusLogic.setMusicOnOff(isMusicOn)
  }



  override def keyPressed(e: KeyEvent): Unit = {

    //avoids exit() on ESCAPE
    if (key == VK_ESCAPE) key = 0

    bonusLogic.actOnKBInput(KeyPressed(e))
  }

  override def keyReleased(e: KeyEvent): Unit = {
    bonusLogic.actOnKBInput(KeyReleased(e))
  }

  override def mouseDragged(e: MouseEvent): Unit =
    bonusLogic.actOnMouseInput(MouseDragged(e))

  override def mouseMoved(e: MouseEvent): Unit =
    bonusLogic.actOnMouseInput(MouseMoved(e))

  override def mousePressed(e: MouseEvent): Unit =
    bonusLogic.actOnMouseInput(MousePressed(e))

  override def mouseReleased(e: MouseEvent): Unit =
    bonusLogic.actOnMouseInput(MouseReleased(e))

  def getWidth: Int = width
  def getHeight: Int = height

  def toggleSFX(): Unit = {
    isSFXOn = !isSFXOn
    bonusLogic.setSFXOnOff(isSFXOn)
  }

  def toggleMusic(): Unit = {
    isMusicOn = !isMusicOn
    bonusLogic.setMusicOnOff(isMusicOn)
  }

  def setMusicVolume(percentage: Float): Unit = {
    musicVolume = percentage
    bonusLogic.music.setVolume(musicVolume) //in percentage (often not working)
    bonusLogic.music.setGain(getDecibels(percentage)) //in decibels (alternative)
  }

  def setSFXVolume(percentage: Float): Unit = {
    SFXVolume = percentage
    bonusLogic.updateSFXVolume(SFXVolume)
  }

}

object BonusGame {
  sealed abstract class Resolution(val res: Dimension[Int] = null, scale: Float = 0)

  case object _832x364 extends Resolution(Dimension[Int](832, 468), scale = 1f)
  case object _1664x728 extends Resolution(Dimension[Int](1664, 936), scale = 2f)
  case object FullScreen extends Resolution

  private val MaxDb: Float = 30f
  val DefaultRes: Resolution = _832x364
  val DefaultWindowSize = Dimension[Int](832,468)
  val DefaultTileSize: Float = 32f
  val DefaultUpdateRadius: Float = DefaultRes.res.width.toFloat / 5f
  private val DefaultFramesPerSeconds: Float = 60
  val DefaultUpdatesPerSecond: Float = 120

  def getDecibels(percentage: Float): Float =
    MaxDb * percentage - 20

  def main(args: Array[String]): Unit = {
    PApplet.main("bonusGame.BonusGame")
  }
}