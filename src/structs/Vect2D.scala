package structs

case class Vect2D[T <: AnyVal](x: T, y: T)(implicit numT: Numeric[T]) {

  import numT._

  def move(deltaX: T = numT.fromInt(0), deltaY: T = numT.fromInt(0)): Vect2D[T] =
    Vect2D[T](x + deltaX, y + deltaY)

  def toDims: Dimension[T] =
    Dimension[T](x, y)

  def size: Float = {
    import scala.math.{pow, sqrt}
    sqrt(  (pow(x.toDouble, 2) + pow(y.toDouble, 2))  ).toFloat
  }

  def normalized: Vect2D[T] =
    Vect2D[T](
      (x.toFloat / size).asInstanceOf[T],
      (y.toFloat / size).asInstanceOf[T]
    )


  //operations
  def +(that: Vect2D[T]): Vect2D[T] =
    Vect2D[T](this.x + that.x, this.y + that.y)

  def -(that: Vect2D[T]): Vect2D[T] =
    Vect2D[T](this.x - that.x, this.y - that.y)

  def *(that: Vect2D[T]): Vect2D[T] =
    Vect2D[T](this.x * that.x, this.y * that.y)

  def *(scalar: T): Vect2D[T] =
    Vect2D[T](x * scalar, y * scalar)

  def /(that: Vect2D[T])(implicit fract: Fractional[T]): Vect2D[T] = {
    import fract._
    Vect2D[T](
       x / that.x,
      y / that.y
    )
  }

  def /[N <: AnyVal](num: T)(implicit fractT: Fractional[T]): Vect2D[T] = {
    import fractT._
    Vect2D[T](this.x / num, this.y / num)
  }


}


