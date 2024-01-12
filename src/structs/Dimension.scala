package structs

import jdk.incubator.vector.VectorOperators.Conversion

case class Dimension[T <: AnyVal](width: T, height: T)(implicit numT: Numeric[T]) {
  import numT._

  def toVect: Vect2D[T] =
    Vect2D[T](width, height)
  def toIntDims: Dimension[Int] =
    Dimension[Int](width.toInt, height.toInt)

  def contains(x: T, y: T): Boolean =
    x < width && x >= numT.fromInt(0) && y < height && y >= numT.fromInt(0)

  // operations
  def +(that: Dimension[T]): Dimension[T] =
    Dimension(this.width + that.width, this.height + that.height)
  def -(that: Dimension[T]): Dimension[T] =
    Dimension(this.width - that.width, this.height - that.height)
  def *[N <: AnyVal](scalar: N)(implicit conv: N => T): Dimension[T] =
    Dimension(width * scalar, height * scalar)
  def *(vect2D: Vect2D[T]): Dimension[T] =
    Dimension(width * vect2D.x, height * vect2D.y)
  def /(scalar: T)(implicit fract: Fractional[T]): Dimension[T] = {
    import fract._
    Dimension(width / scalar, height / scalar)
  }
}
