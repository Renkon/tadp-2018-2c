package  dragonBall

sealed trait Movimiento{
  def evaluarPara(atacante: Guerrero, oponente: Option[Guerrero]/*, criterio: Criterio*/) : Int = {
    // val resultados = this.apply(atacante)(oponente)
    /* FIXME esto de arriba no lo puedo hacer si no unifico un contrato para el apply, lo que yo quiero poder hacer es
       que se de cuenta que un objeto UnItemCualquiera.apply(10) esta parcialmente aplicado y es un movimiento porque
       espera un atacante y un guerrero, entoces de esa forma puedo tratar polimorficamente una lista que tiene
       CargarKi y UnItemCualquiera.apply(10) porque ambos esperarian que les diga apply(atancante, oponente)
    */

    //criterio.evaluarPara(resultados._1, resultados._2)
    1
  }
}

/* FIXME hay alguna forma de hacer el pattern matching de las razas sin que te importen los argumentos, porque
* si no cada vez que agregue un atributo tengo que agregarlo en el patron, y se va a llegan de guiones bajos
* */

/* FIXME hay alguna forma de hacer que los items que si reciben un oponente sean polimorficos con los que no reciben?
 * o esta bien lo que hice con la monada?
* */

object CargarKi extends Movimiento {
  def apply(atacante : Guerrero) (oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    atacante match {
      case Saiyajin(_, nivelSS, _, _, _,_) => (atacante.aumentarEnergia(150 * (nivelSS + 1)), oponente)
      case Androide(_,_,_,_) => (atacante, oponente)
      case _ => (atacante.aumentarEnergia(100), oponente)
    }
  }
}

object UsarItem extends Movimiento {
  def apply(item : Item) (atacante : Guerrero) (oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    (oponente, item) match {
      case (None, ActuaSobreOponente()) => (atacante, oponente)
      case (_, _) => if (atacante.tieneItem(item)) item.apply(atacante)(oponente) else (atacante, oponente)
    }
  }
}