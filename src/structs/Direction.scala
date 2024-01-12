package structs

abstract sealed class Direction

case object Left extends Direction
case object Right extends Direction
case object Up extends Direction
case object Down extends Direction
case object NoDirection extends Direction

