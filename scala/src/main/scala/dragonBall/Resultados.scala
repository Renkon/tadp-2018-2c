package dragonBall

case class ResultadoDeRound(movimientoInicialAtacante : Movimiento,
                            movimientoContraataqueOponente : Option[Movimiento],
                            estadoFinalAtacante : Guerrero,
                            estadoFinalOponente : Guerrero)

object ResultadoDePelea {
  def apply(resultadoDeRound: ResultadoDeRound) : ResultadoDePelea = {
    val atacante = resultadoDeRound.estadoFinalAtacante
    val oponente = resultadoDeRound.estadoFinalOponente
    (atacante.estado, oponente.estado) match {
      case (Muerto, Muerto) => Ganador(atacante)
      case (Muerto, _) => Ganador(oponente)
      case (_ , Muerto) => Ganador(atacante)
      case (_,_) => SigueElCombate(atacante, oponente)
    }
  }
}

sealed trait ResultadoDePelea

case class Ganador(guerrero : Guerrero) extends ResultadoDePelea

case class SigueElCombate(atacante : Guerrero, oponente: Guerrero) extends ResultadoDePelea