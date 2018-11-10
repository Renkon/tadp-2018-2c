package dragonBall

sealed trait CriterioSeleccionDeMovimiento {
  def apply(atacante: Guerrero, oponente: Guerrero) (movimientoAEvaluar : Movimiento) : Int
}

case object LoHaceBosta extends CriterioSeleccionDeMovimiento {
  override def apply(atacante: Guerrero, oponente: Guerrero)(movimientoAEvaluar: Movimiento): Int = {
    val (_, oponenteResultante) = atacante.realizarMovimientoContra(movimientoAEvaluar, oponente)
    oponente.energia - oponenteResultante.energia // retorna que tan grande fue el daño que le hizo
  }
}

case object DisfrutarCombate extends CriterioSeleccionDeMovimiento {
  override def apply(atacante: Guerrero, oponente: Guerrero)(movimientoAEvaluar: Movimiento): Int = {
    val (_, oponenteResultante) = atacante.realizarMovimientoContra(movimientoAEvaluar, oponente)
    val danio = oponente.energia - oponenteResultante.energia
    oponente.energia - danio // cuanto menos danio le hace mas grande es el numero retornado
  }
}

case object Tacanio extends CriterioSeleccionDeMovimiento {
  override def apply(atacante: Guerrero, oponente: Guerrero)(movimientoAEvaluar: Movimiento): Int = {
    val (atacanteResultante, _) = atacante.realizarMovimientoContra(movimientoAEvaluar, oponente)
    val cantidadPerdida = atacante.cantidadDeItems() - atacanteResultante.cantidadDeItems()
    atacante.cantidadDeItems() - cantidadPerdida // cuanto mas items perdio mas chico el numero
  }
}

case object Supervivencia extends CriterioSeleccionDeMovimiento {
  override def apply(atacante: Guerrero, oponente: Guerrero)(movimientoAEvaluar: Movimiento): Int = {
    val (atacanteResultante, _) = atacante.realizarMovimientoContra(movimientoAEvaluar, oponente)
    if(atacanteResultante.estado != Muerto) 1 else 0
  }
}