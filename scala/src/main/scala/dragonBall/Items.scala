package dragonBall

sealed trait Item {
  def apply(atacante: Guerrero, oponente : Guerrero) : (Guerrero, Guerrero)
}

object SemillaDelHermitanio extends Item {
  def apply(atacante: Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    (atacante.eliminarItem(this).aumentarEnergia(atacante.raza.energiaMaxima - atacante.energia), oponente)// FIXME Esto asi como esta en los androides no funicona
  }
}

object ArmaRoma extends Item {
  def apply(atacante: Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza:Androide => (atacante, oponente)
      case _ => (atacante, if(oponente.energia < 300) oponente.quedoInconsciente() else oponente)
    }
  }
}

object ArmaFilosa extends Item {
  def apply(atacante: Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza:Saiyajin if(raza.tieneCola) => (atacante, raza.suColaFueCortada(oponente))
      case _ => reducirEnergia(atacante, oponente)
    }
  }

  private def reducirEnergia(atacante : Guerrero, oponente : Guerrero): (Guerrero, Guerrero) = {
    (atacante, oponente.disminuirEnergia(atacante.energia/100))
  }

}

object ArmaDeFuego extends Item {
  def apply(atacante: Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    val maybeMunicion = atacante.municion()
    (maybeMunicion, oponente.raza, oponente.estado) match {
      case (Some(municion), raza:Humano, _)=> municion.apply(atacante, oponente.disminuirEnergia(20))
      case (Some(municion), raza:Namekusein, Inconsciente) =>  municion.apply(atacante, oponente.disminuirEnergia(10))
      case (_, _, _) => (atacante, oponente)
    }
  }
}

case class Municion(var cantidadActual : Int) extends Item {
  require(cantidadActual >= 1)

  def apply(atacante: Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    val maybeMunicion = atacante.municion()
    maybeMunicion match {
      case Some(_) => (atacante.copy(items = itemsConMunicionModificada(atacante.items)), oponente)
      case None => (atacante, oponente)
    }
  }

  private def itemsConMunicionModificada(items : List[Item]) : List[Item] = {
    val listaSinMuniciones = items.filter(item => !item.isInstanceOf[Municion])
    if(cantidadActual == 1) listaSinMuniciones else Municion(cantidadActual -1) :: listaSinMuniciones
  }
}

case object FotoDeLaLuna extends Item {
  def apply(atacante: Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = (atacante, oponente)
}

case class EsferaDelDragon(numero : Int) extends Item {
  require(numero > 0 && numero < 8)

  def apply(atacante: Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = (atacante, oponente)
} // No me gasto en hacer que haya unicamente 7 esferas y controlar que dos guerreros
// no pueden tener la primerEsfera porque no lo pide el enunciado

package object EsferasDelDragon {
  val primera = EsferaDelDragon(1)
  val segunda = EsferaDelDragon(2)
  val tercera = EsferaDelDragon(3)
  val cuarta = EsferaDelDragon(4)
  val quinta =  EsferaDelDragon(5)
  val sexta = EsferaDelDragon(6)
  val septima = EsferaDelDragon(7)

  val todasLasEsferas = List(primera, segunda,tercera, cuarta, quinta, sexta, septima)
}


// Al final quedamos con juan que usar el item municion significa gastarla, no importa para que,
// y usar el item arma de fuego significa que si tenes municion te gasta una Y aparte con ella produce el daño al enemigo

// Dos opciones para modelar la municion:
// Un objeto municion que cuando llega a cero lo dropeas de la lista de items, inmutable => MAS DIVERTIDO, voy con esta
// Cada bala es una instancia de la clase municion, y la sacas de la lista de items del guerrero a medida que la usa => MAS FACIL