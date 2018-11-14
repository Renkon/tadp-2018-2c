package  dragonBall

sealed trait Movimiento{
  def andThen(segundoMovimiento: Movimiento): Movimiento = new MovimientoCompuesto(this, segundoMovimiento)

  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero)
}

class MovimientoCompuesto(primero:Movimiento, segundo:Movimiento) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    val resultadoPrimeraAplicacion = primero(atacante, oponente)
    segundo(resultadoPrimeraAplicacion._1, resultadoPrimeraAplicacion._2)
  }
}


 object DejarseFajar extends Movimiento {
   def apply(atacante:Guerrero, oponente:Guerrero) : (Guerrero, Guerrero) = (atacante.seDejoFajar(), oponente)
 }

object CargarKi extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Saiyajin => (atacante.aumentarEnergia(150 * raza.nivelDeFase), oponente)
      case raza:Androide => (atacante, oponente)
      case _ => (atacante.aumentarEnergia(100), oponente)
    }
  }
}

case class UsarItem(item:Item) extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    if (atacante.tieneItem(item)) atacante.usarItem(item, oponente) else (atacante, oponente)
  }
}

object ComerseAlOponente extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Monstruo => raza.darDeComerA(atacante, oponente)
      case _ => (atacante, oponente)
    }
  }
}

object ConvertirseEnMono extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Saiyajin => (raza.convertirseEnMono(atacante), oponente)
      case _ => (atacante, oponente)
    }
  }
}

object ConvertirseEnSuperSaiyajin extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Saiyajin if(raza.fase != Mono) => (raza.siguienteNivel(atacante), oponente)
      case _ => (atacante, oponente)
    }
  }
}

case class FusionarseCon(compañeroDeFusion : Guerrero) extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    (atacante.raza, compañeroDeFusion.raza) match {
      case (razaAtacante:Fusionable, razaCompaniero:Fusionable) =>
        (atacante.copy(raza = Fusionado(atacante, compañeroDeFusion)).aumentarEnergia(compañeroDeFusion.energia), oponente)
      case (_,_) => (atacante, oponente)
    }
  }
}

case class UsarMagia(efectoSobreAtacante : EfectoMagico, efectoSobreOponente : EfectoMagico) extends Movimiento {
  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Namekusein => (efectoSobreAtacante(atacante), efectoSobreOponente(oponente))
      case raza:Monstruo => (efectoSobreAtacante(atacante), efectoSobreOponente(oponente))
      case _  => if (atacante.tieneTodasLasEsferasDelDragon())
                    (efectoSobreAtacante(atacante).esparcirEsferas(), efectoSobreOponente(oponente))
                 else (atacante, oponente)
    }
  }
}

sealed trait EfectoMagico {
  def apply(guerrero : Guerrero) : Guerrero
}

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
  override def apply(guerrero: Guerrero): Guerrero = guerrero.quedoInconsciente()
}

case object RevivirAKrilin extends EfectoMagico {
  override def apply(guerrero: Guerrero) =
    if(guerrero.nombre.contains("krilin") && guerrero.estado == Muerto) guerrero.aumentarEnergia(40) else guerrero
}

case class AtacarCon(ataque : Ataque) extends Movimiento {
  def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = ataque(atacante, oponente)
}

sealed trait Ataque extends Movimiento

sealed trait Fisico extends Ataque

sealed trait DeEnergia extends Ataque {
  override def apply(atacante : Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    if(puedeRealizarla(atacante))
      if(oponente.raza.isInstanceOf[Androide])
      this.realizarAtaque(atacante, oponente, consumeLaEnergia)
      else this.realizarAtaque(atacante, oponente, laEnergiaLoDania)
    else (atacante, oponente)
  }

  def laEnergiaLoDania(danio : Int , oponente : Guerrero) : Guerrero = oponente.disminuirEnergia(danio)

  def consumeLaEnergia(aumento : Int, oponente: Guerrero) : Guerrero = oponente.aumentarEnergia(aumento)

  def realizarAtaque(atacante: Guerrero, oponente : Guerrero, efectoEnElOponente: (Int, Guerrero) => Guerrero) : (Guerrero, Guerrero)

  def puedeRealizarla(guerrero: Guerrero) : Boolean
}

case object MuchosGolpesNinja extends Fisico {
  override def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante.raza, oponente.raza) match {
      case (razaAtacante:Humano, razaOponente:Androide) => (atacante.disminuirEnergia(10), oponente)
      case (_,_) => List(atacante, oponente).maxBy(g => g.energia) match {
        case `atacante` => (atacante, oponente.disminuirEnergia(20))
        case `oponente` => (atacante.disminuirEnergia(20), oponente)
      }
    }
  }
}

case object Explotar extends Fisico {
  override def apply(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    atacante.raza match {
      case raza:Androide => disminuirEnergiaPorExplosion(atacante, oponente, atacante.energia*3)
      case raza:Monstruo => disminuirEnergiaPorExplosion(atacante, oponente, atacante.energia*2)
      case _ => (atacante, oponente)
    }
  }

  private def disminuirEnergiaPorExplosion(atacante : Guerrero, oponente : Guerrero, energiaImpacto: Int) : (Guerrero, Guerrero) = {
    val atacanteMuerto = atacante.disminuirEnergia(atacante.energia)
    oponente.raza match {
      case raza:Namekusein => (atacanteMuerto, oponente.disminuirEnergia((oponente.energia - 1).min(energiaImpacto)))
      case _ => (atacanteMuerto, oponente.disminuirEnergia(energiaImpacto))
    }
  }
}

abstract class Onda() extends DeEnergia {
  override def puedeRealizarla(atacante: Guerrero): Boolean = atacante.energia >= this.energiaDelAtaquePara(atacante) // si es igual queda muerto

  def energiaDelAtaquePara(guerrero : Guerrero) : Int

  override def realizarAtaque(atacante: Guerrero, oponente : Guerrero, efectoEnElOponente: (Int, Guerrero) => Guerrero) : (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza:Monstruo => (consumirEnergia(atacante), efectoEnElOponente(energiaDelAtaquePara(atacante) / 2, oponente))
      case _ => (consumirEnergia(atacante), efectoEnElOponente(energiaDelAtaquePara(atacante) * 2, oponente))
    }
  }

  def consumirEnergia(guerrero: Guerrero) : Guerrero = guerrero.disminuirEnergia(this.energiaDelAtaquePara(guerrero))
}

case object Kamehameha extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero) : Int = 80
}

case object Kienzan extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero): Int = 60
}

case object Dodonpa extends Onda {
  override def energiaDelAtaquePara(atacante: Guerrero): Int = 30
}

case object Genkidama extends Onda {
  override def puedeRealizarla(atacante: Guerrero): Boolean = atacante.roundsQueSeDejoFajar > 0

  override def energiaDelAtaquePara(atacante : Guerrero): Int = math.pow(10, atacante.roundsQueSeDejoFajar).toInt

  override def realizarAtaque(atacante: Guerrero, oponente: Guerrero, efectoEnElOponente: (Int, Guerrero) => Guerrero): (Guerrero, Guerrero) = {
    val (nuevoAtacante, nuevoOponente) = super.realizarAtaque(atacante, oponente, efectoEnElOponente)
    (nuevoAtacante.copy(roundsQueSeDejoFajar = 0), nuevoOponente)
  }

  override def consumirEnergia(guerrero: Guerrero): Guerrero = guerrero // porque la energia es externa
}