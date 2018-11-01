package dragonBall

sealed trait Item

case class Curativo() extends Item
case class Ofensivo() extends Item

object ArmaRoma extends Ofensivo
object SemillaDelHermitanio extends Curativo

