package logic

import bonusGame.{BackGround, BonusGame}
import bonusGame.BonusGame._
import entities.Player
import gamestates._
import levels.{Level, LevelManager}
import processing.core.{PApplet, PGraphics, PImage}
import gamestates._
import processing.event.{KeyEvent, MouseEvent}
import structs.{Dimension, GenericSprite, KBInput, MouseInput, Vect2D}
import utils.LoadSave
import utils.LoadSave.{File, Music}
import ddf.minim.{AudioSample, Minim}





class BonusLogic(bonusGame: BonusGame, scale: Float, ups: Float) {
  val minim: Minim = new Minim(bonusGame)
  private val bg: BackGround = new BackGround(bonusLogic = this)
  private val lvlManager: LevelManager = new LevelManager(bonusLogic = this)
  private var currentGameState: GameState = new Menu(bonusLogic = this)
  val music = LoadSave.loadMusic(bonusGame, minim, bonusGame.musicVolume, Music)
  music.loop()

  def loadSFXFun: File => AudioSample = LoadSave.loadSFX(bonusGame, minim, bonusGame.SFXVolume)

  def update(): Unit = {
    bg.update()
    currentGameState.update()
  }

  def draw(g: PGraphics): Unit =
    currentGameState.draw(g, scale)

  def setGameState(gameStateId: GameStateId): Unit = {
    gameStateId match {
      case GameActive => currentGameState = new GameActive(lvlManager.getLvl, bonusLogic = this)
      case Menu => currentGameState = new Menu(bonusLogic = this)
      case OptionsScreen => currentGameState = new OptionsScreen(bonusLogic = this, currentGameState)
    }
  }

  def setGameState(gameState: GameState): Unit =
    currentGameState = gameState

  def setMusicOnOff(b: Boolean): Unit = {
    if (b) music.play()
    else music.pause()
  }

  def setSFXOnOff(b: Boolean): Unit =
    currentGameState.setSFXOnOff(b)

  def drawBg(g: PGraphics, scale: Float, lvlOffset: Vect2D[Float] = Vect2D(0f, 0f)): Unit =
    bg.draw(g, scale, lvlOffset)

  def nextLvl(): Unit = {
    if (lvlManager.thereIsANextLvl)
      currentGameState = new GameActive(lvlManager.nextLvl, bonusLogic = this)
    else
      currentGameState = new Menu(bonusLogic = this)
  }


  def actOnKBInput(KBInput: KBInput): Unit =
    currentGameState.actOnKBInput(KBInput)

  def actOnMouseInput(mouseInput: MouseInput): Unit =
    currentGameState.actOnMouseInput(mouseInput)

  def getBonusGame: BonusGame = bonusGame

  def updateSFXVolume(percentage: Float): Unit =
    currentGameState.updateSFXVolume(percentage)

  def imgResizeAndScale(dimension: Dimension[Float])(img: PImage): PImage = {
    img.resize(dimension.width.toInt, dimension.height.toInt)

    imgScale(img)
  }

  def imgScale(img: PImage): PImage = {
    img.resize((img.width * scale).ceil.toInt, (img.height * scale).ceil.toInt)

    img
  }

  def mouseEventToPoint(e: MouseEvent): Vect2D[Float] =
    Vect2D[Float](e.getX.toFloat, e.getY.toFloat) / scale

  def loadImgWithPApplet(file: File): PImage =
    LoadSave.getAtlas(bonusGame, file)

  def getWidth: Int = bonusGame.getWidth
  def getHeight: Int = bonusGame.getHeight
  def getUps: Float = ups
}

