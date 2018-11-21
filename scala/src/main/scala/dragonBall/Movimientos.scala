package dragonBall

sealed trait Movimiento {
  def andThen(segundoMovimiento: Movimiento): Movimiento = new MovimientoCompuesto(this, segundoMovimiento)

  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero)
}

class MovimientoCompuesto(primero: Movimiento, segundo: Movimiento) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    val resultadoPrimeraAplicacion = primero(atacante, oponente)
    segundo(resultadoPrimeraAplicacion._1, resultadoPrimeraAplicacion._2)
  }
}


case class AtacarCon(ataque: Ataque) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = ataque(atacante, oponente)
}

case class FusionarseCon(compañeroDeFusion: Guerrero) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante.raza, compañeroDeFusion.raza) match {
      case (razaAtacante: Fusionable, razaCompaniero: Fusionable) =>
        (atacante.copy(raza = Fusionado(atacante, compañeroDeFusion)).aumentarEnergia(compañeroDeFusion.energia), oponente)
      case (_, _) => (atacante, oponente)
    }
  }
}

case class UsarMagia(efectoSobreAtacante: EfectoMagico, efectoSobreOponente: EfectoMagico) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case (_: Namekusein | _: Monstruo) => (efectoSobreAtacante(atacante), efectoSobreOponente(oponente))
      case _ => if (atacante.tieneTodasLasEsferasDelDragon())
        (efectoSobreAtacante(atacante).esparcirEsferas(), efectoSobreOponente(oponente))
      else (atacante, oponente)
    }
  }
}


case object DejarseFajar extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = (atacante.dejarseFajar(), oponente)
}

case object CargarKi extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza: Saiyajin => (atacante.aumentarEnergia(150 * raza.nivelDeFase), oponente)
      case raza: Androide => (atacante, oponente)
      case _ => (atacante.aumentarEnergia(100), oponente)
    }
  }
}

case class UsarItem(item: Item) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    if (atacante.tieneItem(item)) atacante.usarItem(item, oponente) else (atacante, oponente)
  }
}

object ComerseAlOponente extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza: Monstruo => raza.darDeComerA(atacante, oponente)
      case _ => (atacante, oponente)
    }
  }
}

object ConvertirseEnMono extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza: Saiyajin => (raza.convertirseEnMono(atacante), oponente)
      case _ => (atacante, oponente)
    }
  }
}

object ConvertirseEnSuperSaiyajin extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza: Saiyajin if raza.fase != Mono => (raza.siguienteNivel(atacante), oponente)
      case _ => (atacante, oponente)
    }
  }
}


/* Efectos magicos */

sealed trait EfectoMagico {
  def apply(guerrero: Guerrero): Guerrero
}

/* No se me ocurre como sacarlos de aca y declararlos en lso test.. onda si se me ocurre como peroo no puedo haerlos del tipo efectomagico*/
case object NoHacerNada extends EfectoMagico {
  override def apply(guerrero: Guerrero): Guerrero = guerrero
}

case object ObtenerSemillaDelErmitanio extends EfectoMagico {
  override def apply(guerrero: Guerrero): Guerrero = guerrero.copy(items = SemillaDelHermitanio :: guerrero.items)
}

case object Matar extends EfectoMagico {
  override def apply(guerrero: Guerrero): Guerrero = guerrero.disminuirEnergia(guerrero.energia - guerrero.energia) // TODO se supone que le cambia el estado a Muerto
}

case object ConvertirEnChocolate extends EfectoMagico {
  override def apply(guerrero: Guerrero): Guerrero = guerrero.quedarInconsciente()
}

case object RevivirAKrilin extends EfectoMagico {
  override def apply(guerrero: Guerrero): Guerrero =
    if (guerrero.nombre.contains("krilin") && guerrero.estado == Muerto) guerrero.aumentarEnergia(40) else guerrero
}


sealed trait Ataque extends Movimiento

/* Ataques De distintos tipos*/

sealed trait Fisico extends Ataque

case object MuchosGolpesNinja extends Fisico {
  override def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante.raza, oponente.raza) match {
      case (razaAtacante: Humano, razaOponente: Androide) => (atacante.disminuirEnergia(10), oponente)
      case (_, _) => List(atacante, oponente).maxBy(g => g.energia) match {
        case `atacante` => (atacante, oponente.disminuirEnergia(20))
        case `oponente` => (atacante.disminuirEnergia(20), oponente)
      }
    }
  }
}

case object Explotar extends Fisico {
  override def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza: Androide => disminuirEnergiaPorExplosion(atacante, oponente, atacante.energia * 3)
      case raza: Monstruo => disminuirEnergiaPorExplosion(atacante, oponente, atacante.energia * 2)
      case _ => (atacante, oponente)
    }
  }

  private def disminuirEnergiaPorExplosion(atacante: Guerrero, oponente: Guerrero, energiaImpacto: Int): (Guerrero, Guerrero) = {
    val atacanteMuerto = atacante.disminuirEnergia(atacante.energia)
    oponente.raza match {
      case raza: Namekusein => (atacanteMuerto, oponente.disminuirEnergia((oponente.energia - 1).min(energiaImpacto)))
      case _ => (atacanteMuerto, oponente.disminuirEnergia(energiaImpacto))
    }
  }
}


/* Ataques de ondas */

// fixme
sealed trait DeEnergia extends Ataque {

  override def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    if (puedeRealizarla(atacante))
      this.realizarAtaque(atacante, oponente)
    else
      (atacante, oponente)
  }

  def realizarAtaque(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero)

  def puedeRealizarla(guerrero: Guerrero): Boolean
}

abstract class Onda() extends DeEnergia {
  override def puedeRealizarla(atacante: Guerrero): Boolean = atacante.energia >= this.energiaDelAtaquePara(atacante) // si es igual queda muerto

  def energiaDelAtaquePara(guerrero: Guerrero): Int

  override def realizarAtaque(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza: Monstruo => (consumirEnergia(atacante), laEnergiaLoDania(modificadorMonstruo(energiaDelAtaquePara(atacante)), oponente))
      case raza: Androide => (consumirEnergia(atacante), consumeLaEnergia(modificadorOtraRaza(energiaDelAtaquePara(atacante)), oponente))
      case _ => (consumirEnergia(atacante), laEnergiaLoDania(modificadorOtraRaza(energiaDelAtaquePara(atacante)), oponente))
    }
  }

  def laEnergiaLoDania(danio: Int, oponente: Guerrero): Guerrero = oponente.disminuirEnergia(danio)

  def consumeLaEnergia(aumento: Int, oponente: Guerrero): Guerrero = oponente.aumentarEnergia(aumento)

  protected def modificadorMonstruo(energiaOriginal: Int): Int = energiaOriginal / 2

  protected def modificadorOtraRaza(energiaOriginal: Int): Int = energiaOriginal * 2

  def consumirEnergia(guerrero: Guerrero): Guerrero = guerrero.disminuirEnergia(this.energiaDelAtaquePara(guerrero))
}

case object Kamehameha extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero): Int = 80
}

case object Kienzan extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero): Int = 60
}

case object Finalflash extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero): Int = 70
}

case object Genkidama extends Onda {
  override def modificadorMonstruo(energiaOriginal: Int): Int = energiaOriginal

  override def modificadorOtraRaza(energiaOriginal: Int): Int = energiaOriginal

  override def puedeRealizarla(atacante: Guerrero): Boolean = atacante.roundsQueSeDejoFajar > 0

  override def energiaDelAtaquePara(atacante: Guerrero): Int = math.pow(10, atacante.roundsQueSeDejoFajar).toInt

  override def realizarAtaque(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    val (nuevoAtacante, nuevoOponente) = super.realizarAtaque(atacante, oponente)
    (nuevoAtacante.copy(roundsQueSeDejoFajar = 0), nuevoOponente)
  }

  override def consumirEnergia(guerrero: Guerrero): Guerrero = guerrero
}