package dragonBall

case class ResultadoDeRound(movimientoInicialAtacante : Movimiento,
                            movimientoContraataqueOponente : Option[Movimiento],
                            estadoFinalAtacante : Guerrero,
                            estadoFinalOponente : Guerrero)
