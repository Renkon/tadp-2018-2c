package  dragonBall

sealed trait Movimiento{
  def apply(atacante : Guerrero) (oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero])

  def evaluarPara(atacante: Guerrero, oponente: Option[Guerrero]/*, criterio: Criterio*/) : Int = {
    val resultados = this.apply(atacante)(oponente)
    //criterio.evaluarPara(resultados._1, resultados._2)
    1
  }
}

object CargarKi extends Movimiento {
  override def apply(atacante : Guerrero) (oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    atacante match {
      case Saiyajin(_, nivelSS, _, _, _) => (atacante.aumentarEnergia(150 * (nivelSS + 1)), oponente)
      case Androide(_,_,_) => (atacante, oponente)
      case _ => (atacante.aumentarEnergia(100), oponente)
    }
  }
}

object UsarItem extends Movimiento {
  override def apply(item : Item) (atacante : Guerrero) (oponente : Option[Guerrero]) : (Guerrero, Option[Guerrero]) = {
    (oponente, item) match {
      case (_, ActuaSobreAtacante()) => (atacante.usarItemSobreMi(item), oponente)
      case (Some(_), ActuaSobreEnemigo()) => {val resultado = atacante.usarItemSobre(oponente.get, item); (resultado._1, Some[resultado._2])}
      case (None, ActuaSobreEnemigo()) => (atacante, oponente)
    }
  }
}