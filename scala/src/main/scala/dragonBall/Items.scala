package dragonBall

sealed trait Item {
  def apply(atacante: Guerrero)(oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero])
}

/*
* FIXME hay alguna forma de lograr una estructura similar que sirva para el pattern matching que hago
* en movimientos, pero sin esta repeticion de firma que esta al dope?
* */

abstract case class ActuaSobreAtacante() extends Item {
  def apply(atacante: Guerrero)(oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero])
}

abstract case class ActuaSobreOponente() extends Item {
  def apply(atacante: Guerrero)(oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero])
}

//object ArmaRoma extends ActuaSobreEnemigo

object SemillaDelHermitanio extends ActuaSobreAtacante(){
  def apply(atacante: Guerrero)(oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    (atacante.aumentarEnergia(atacante.energiaMaxima - atacante.energia), oponente)// FIXME Esto asi como esta en los androides no funicona
  }
}

