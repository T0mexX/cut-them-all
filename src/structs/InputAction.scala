package structs

import processing.event.{KeyEvent, MouseEvent}

sealed abstract class InputAction

sealed abstract class MouseInput
case class MouseMoved(e: MouseEvent) extends MouseInput
case class MousePressed(e: MouseEvent) extends MouseInput
case class MouseReleased(e: MouseEvent) extends MouseInput
case class MouseDragged(e: MouseEvent) extends MouseInput


sealed abstract class KBInput
case class KeyPressed(e: KeyEvent) extends KBInput
case class KeyReleased(e: KeyEvent) extends KBInput
