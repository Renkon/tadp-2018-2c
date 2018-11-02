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

object ArmaDeFuego extends ActuaSobreOponente() {
  def apply(atacante: Guerrero) (oponente: Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    val municion = atacante.municiones()
    (municion, oponente) match {
      case (Some(_), None) => (atacante, oponente)
      case (None,_) => (atacante, oponente)
      case (Some(_), Some(_)) => municion.get.apply(atacante)(oponente)
    }
  }
}

case class Municion(var cantidadActual : Int) extends Item {
  def usar() = {cantidadActual = 0.max(cantidadActual - 1)}
  def cantidad() : Int = cantidadActual

  def apply(atacante: Guerrero) (oponente: Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    val op = oponente.get
    (op, op.estado) match {
      case (Humano(_,_,_,_,_), _)=> {this.usar(); (atacante, Some(op.disminuirEnergia(20)))}
      case (Namekusein(_, _, _ ,_ ,_), Inconsciente) =>  {this.usar(); (atacante, Some(op.disminuirEnergia(10)))}
      case (_, _) => (atacante, oponente)
    }
  }
} // aca si hay efecto a proposito, aunque si me la rebusco un poco podria hacerse inmutable, aunque no estoy seguro de que ganaria.


