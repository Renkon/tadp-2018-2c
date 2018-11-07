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

  def apply (atacante : Guerrero, oponente : Guerrero) : (Guerrero, Guerrero)
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