package dragonBall

sealed trait Item {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero)
}

/* Inicio  semillas*/

object SemillaDelHermitanio extends Item {

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante.eliminarItem(this).aumentarEnergia(atacante.energiaMaxima - atacante.energia), oponente)
  }

}

/* Fin semillas */


/* Inicio Armas */

object ArmaRoma extends Item {

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza: Androide => (atacante, oponente)
      case _ => (atacante, if (oponente.energia < 300) oponente.quedoInconsciente() else oponente)
    }
  }

}

object ArmaFilosa extends Item {

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza: Saiyajin if raza.tieneCola => (atacante, raza.suColaFueCortada(oponente))
      case _ => reducirEnergia(atacante, oponente)
    }
  }

  private def reducirEnergia(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante, oponente.disminuirEnergia(atacante.energia / 100))
  }

}

object ArmaDeFuego extends Item {

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    val maybeMunicion = atacante.municion()
    (maybeMunicion, oponente.raza, oponente.estado) match {
      case (Some(municion), raza: Humano, _) => municion.apply(atacante, oponente.disminuirEnergia(20))
      case (Some(municion), raza: Namekusein, Inconsciente) => municion.apply(atacante, oponente.disminuirEnergia(10))
      case (_, _, _) => (atacante, oponente)
    }
  }
}

case class Municion(var cantidadActual: Int) extends Item {
  require(cantidadActual >= 1, "Debe haber al menos una municion")

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    val maybeMunicion = atacante.municion()
    maybeMunicion match {
      case Some(_) => (atacante.copy(items = gastarUnaMunicion(atacante.items)), oponente)
      case None => (atacante, oponente)
    }
  }

  private def gastarUnaMunicion(items: List[Item]): List[Item] = {
    val listaSinMuniciones = items.filter(item => item != this)

    if (this.esUltimaMunicion)
      listaSinMuniciones
    else
      Municion(cantidadActual - 1) :: listaSinMuniciones
  }

  private def esUltimaMunicion(): Boolean = {
    return cantidadActual == 1
  }
}

/* Fin armas */



/* Inicio Foto */
case object FotoDeLaLuna extends Item {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = (atacante, oponente)
}
/* Fin Foto */


/* Inicio Esferas */
case class EsferaDelDragon(numero: Int) extends Item {
  require(numero > 0 && numero < 8, "El numero de esfera debe ser entre 1 y 7")
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = (atacante, oponente)
}

package object EsferasDelDragon {
  val primera = EsferaDelDragon(1)
  val segunda = EsferaDelDragon(2)
  val tercera = EsferaDelDragon(3)
  val cuarta = EsferaDelDragon(4)
  val quinta = EsferaDelDragon(5)
  val sexta = EsferaDelDragon(6)
  val septima = EsferaDelDragon(7)

  val todasLasEsferas = List(primera, segunda, tercera, cuarta, quinta, sexta, septima)
}
/* Fin Esferas */


/* Notas de Correcciones:
*
*  1) Al final quedamos con Juan que usar el item municion significa gastarla, no importa para que,
*     y usar el item arma de fuego significa que si tenes municion te gasta una Y aparte con ella produce el daño al enemigo.
*
* 2)  Dos opciones para modelar la municion:
*     Un objeto municion que cuando llega a cero lo dropeas de la lista de items, inmutable => MAS DIVERTIDO.
*     Cada bala es una instancia de la clase municion, y la sacas de la lista de items del guerrero a medida que la usa => MAS FACIL.
*
*     Se elije la opcion mas divertida
* */