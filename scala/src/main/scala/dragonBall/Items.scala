package dragonBall

sealed trait Item

case class ActuaSobreAtacante() extends Item
case class ActuaSobreEnemigo() extends Item

object ArmaRoma extends ActuaSobreEnemigo

object SemillaDelHermitanio extends ActuaSobreAtacante {
  def aplicarSobre(guerrero : Guerrero) : Guerrero = {
    guerrero.aumentarEnergia(guerrero.energiaMaxima - guerrero.energia) // FIXME Esto asi como esta en los androides no funicona
  }
}

