package  dragonBall

sealed trait Movimiento{
  def evaluarPara(atacante: Guerrero, oponente: Guerrero/*, criterio: Criterio*/) : Int = {
    // val resultados = this.apply(atacante)(oponente)
    /* FIXME esto de arriba no lo puedo hacer si no unifico un contrato para el apply, lo que yo quiero poder hacer es
       que se de cuenta que un objeto UnItemCualquiera.apply(10) esta parcialmente aplicado y es un movimiento porque
       espera un atacante y un guerrero, entoces de esa forma puedo tratar polimorficamente una lista que tiene
       CargarKi y UnItemCualquiera.apply(10) porque ambos esperarian que les diga apply(atancante, oponente)
    */

    //criterio.evaluarPara(resultados._1, resultados._2)
    1
  }

  def apply(atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero)
} /* Todo esto esta planteado asi porque la idea para resolver el primer requerimiento es :
  * >> en Guerrero
  * def movimientoMasEfectivoContra(oponente)(unCriterio) {
  * this.movimientos.fold(primerMovimiento)((semilla, otro) =>
  *   if(semilla.evaluarPara(this, oponente, criterio) > otro.evaluarPara(this, oponente, criterio) semilla else otro))
  * }
  */

/* FIXME hay alguna forma de hacer el pattern matching de las razas sin que te importen los argumentos, porque
* si no cada vez que agregue un atributo tengo que agregarlo en el patron, y se va a llegan de guiones bajos
* */

/* FIXME hay alguna forma de hacer que los items que si reciben un oponente sean polimorficos con los que no reciben?
 * o esta bien lo que hice con la monada?
* */

// Explicacion de juan sobre aplicacion parcial
//type Movimiento = combatientes => combatientes
//cargarki :: movimiento
//cargarki combatientes = case combatientes of
//  ...
//  ...
//  ...
//
//usaritem :: item -> movimiento
//usaritem item combatientes = case (combatientes, item) of
//  ...
//
//semilladelermitanio :: item
//
//usaritem semilladelermitanio :: movimiento
//


// object DejarseFajar extends Movimiento // TODO

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
    if(oponente.raza.isInstanceOf[Androide])
      (atacante, oponente.aumentarEnergia(this.energiaDelAtaque(atacante, oponente)))
    else (atacante, oponente)
  }

  def energiaDelAtaque(atacante: Guerrero, oponente : Guerrero) : Int
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