package gamestates

import bonusGame.BonusGame
import logic.BonusLogic
import processing.core.{PGraphics, PImage}
import processing.event.{KeyEvent, MouseEvent}
import structs.{Dimension, GenericSprite, MouseInput, Rect, Vect2D}
import userInterface.{Button, DropDownMenu, ScrollBar, ToggleButton}
import userInterface.Button.{ButtonId, FSButton, HomeButton, SoundButton, _1664x728Button, _832x364Button}
import utils.LoadSave
import utils.LoadSave.OptionsTabBG

final class OptionsScreen(override val bonusLogic: BonusLogic, previousGameState: GameState) extends GameState {
  import gamestates.OptionsScreen._

  private val bonusGame = bonusLogic.getBonusGame
  private val optionsTabBG: GenericSprite = getOptionsTabBG
  val resolutionSelection: DropDownMenu = getResolutionSelection

  override val scrollBars: Map[ButtonId, ScrollBar] = {
    Map[ButtonId, ScrollBar](
      SFXScrollBar -> ScrollBar(id = SFXScrollBar, DefaultSFXBarPosAndDims._1, DefaultSFXBarPosAndDims._2, this, bonusGame.SFXVolume),
      MusicScrollBar -> ScrollBar(id = MusicScrollBar, DefaultMusicBarPosAndDims._1, DefaultMusicBarPosAndDims._2, this, bonusGame.musicVolume)
    )
  }

  override val buttons: Seq[Button] = {
    Seq[Button](
      new Button(HomeButton, id = Home, DefaultHomeBtnPosAndDims._1, DefaultHomeBtnPosAndDims._2, this)(),
      ToggleButton(SoundButton, btnId = MusicToggle, DefaultMusicBtnPosAndDims._1, DefaultMusicBtnPosAndDims._2, this, !bonusGame.isMusicOn),
      ToggleButton(SoundButton, btnId = SFXToggle, DefaultSFXBtnPosAndDims._1, DefaultSFXBtnPosAndDims._2, this, !bonusGame.isSFXOn)
    )
  }


  override def actOnMouseInput(mouseInput: MouseInput): Unit = {
    resolutionSelection.actOnMouseInput(mouseInput)
    super.actOnMouseInput(mouseInput)
  }

  override def draw(g: PGraphics, scale: Float): Unit = {
    bonusLogic.drawBg(g, scale)
    optionsTabBG.draw(g, scale)
    super.draw(g, scale)
    resolutionSelection.draw(g, scale)
  }

  override def buttonClicked(buttonId: ButtonId): Unit = {
    buttonId match {
      case Home => bonusLogic.setGameState(previousGameState)
      case SFXToggle => bonusGame.toggleSFX()
      case MusicToggle => bonusGame.toggleMusic()
      case SFXScrollBar => bonusGame.setSFXVolume(scrollBars(SFXScrollBar).percentage)
      case MusicScrollBar => bonusGame.setMusicVolume(scrollBars(MusicScrollBar).percentage)
      case Id_832x364 => bonusGame.changeResolution(BonusGame._832x364)
      case Id_1664x728 => bonusGame.changeResolution(BonusGame._1664x728)
      case IdFullScreen => bonusGame.changeResolution(BonusGame.FullScreen)
    }
  }

  private def getOptionsTabBG: GenericSprite = {
    val scaleFun: PImage => PImage = bonusLogic.imgResizeAndScale(DefaultTabBGPosAndDims._2)
    val defaultSprite = bonusLogic.loadImgWithPApplet(OptionsTabBG)
    GenericSprite(
      scaleFun(defaultSprite),
      DefaultTabBGPosAndDims._1,
    )
  }

  private def getResolutionSelection: DropDownMenu = {
    //buttons pos and dims are going to be changed inside DropDownMenu in order to fit the drop down selection
    val buttonsForSelection =
      Seq[Button](
        new Button(_832x364Button, Id_832x364, gameState = this)(),
        new Button(_1664x728Button, Id_1664x728, gameState = this)(),
        new Button(FSButton, IdFullScreen, gameState = this)()
      )

    DropDownMenu(
      DefaultResSelectionPosAndDims._1,
      DefaultResSelectionPosAndDims._2,
      buttonsForSelection,
      this
    )
  }
}

object OptionsScreen extends GameStateId {
  private val DefaultTabBGPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](BonusGame.DefaultRes.res.width.toFloat / 2, 236), Dimension[Float](598, 365))
  private val DefaultHomeBtnPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](650, 350), Dimension[Float](56, 56))
  private val DefaultMusicBtnPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](290, 163), Dimension[Float](42, 42))
  private val DefaultMusicBarPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](430, 163), Dimension[Float](215, 44))
  private val DefaultSFXBtnPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](290, 208), Dimension[Float](42, 42))
  private val DefaultSFXBarPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](430, 208), Dimension[Float](215, 44))
  private val DefaultResSelectionPosAndDims: (Vect2D[Float], Dimension[Float]) =
    (Vect2D[Float](615, 185.5f), Dimension[Float](126, 50.4f))

  case object Home extends ButtonId
  case object SFXToggle extends ButtonId
  case object MusicToggle extends ButtonId

  case object SFXScrollBar extends ButtonId
  case object MusicScrollBar extends ButtonId

  case object Id_832x364 extends ButtonId
  case object Id_1664x728 extends ButtonId
  case object IdFullScreen extends ButtonId
}
