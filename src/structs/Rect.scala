package structs

case class Rect[T <: AnyVal](topLeftPos: Vect2D[T], dims: Dimension[T])(implicit numT: Numeric[T]) {
  import numT._

  val centerPos: Vect2D[T] = {
    topLeftPos + Vect2D[T](
      (dims.width.toFloat / 2).asInstanceOf[T],
      (dims.height.toFloat / 2).asInstanceOf[T]
    )
  }

  def contains(vectPoint: Vect2D[T]): Boolean = {
    vectPoint.x >= topLeftPos.x &&
      vectPoint.x < topLeftPos.x + dims.width &&
      vectPoint.y >= topLeftPos.y &&
      vectPoint.y < topLeftPos.y + dims.height
  }

  def getBottomLeftPos: Vect2D[T] =
    topLeftPos.move(numT.fromInt(0), dims.height)

  def getBottomRightPos: Vect2D[T] =
    topLeftPos + dims.toVect

  def expand(x: T, y: T): Rect[T] =
    Rect[T](
      topLeftPos - Vect2D[T]((x.toFloat / 2).asInstanceOf[T], (y.toFloat / 2).asInstanceOf[T]),
      dims + Dimension[T](x, y)
  )
}

object Rect {
  def apply[T <: AnyVal](topLeftX: T, topLeftY: T, dims: Dimension[T])(implicit numT: Numeric[T]): Rect[T] =
    Rect(Vect2D[T](topLeftX, topLeftY), dims)

  def apply[T <: AnyVal](topLeftX: T, topLeftY: T, width: T, height: T)(implicit numT: Numeric[T]): Rect[T] =
    Rect(Vect2D[T](topLeftX, topLeftY), Dimension(width, height))

  def apply[T <: AnyVal](topLeftPos: Vect2D[T], width: T, height: T)(implicit numT: Numeric[T]) : Rect[T] =
    Rect(topLeftPos, Dimension(width, height))
}
