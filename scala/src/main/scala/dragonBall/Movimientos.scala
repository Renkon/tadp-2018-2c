package  dragonBall

object CargarKi {
  def apply(guerrero : Guerrero): Guerrero = {
    guerrero match {
      case Saiyajin(_, nivelSS, _, _, _) => guerrero.aumentarEnergia(150 * (nivelSS + 1))
      case Androide(_,_,_) => guerrero
      case _ => guerrero.aumentarEnergia(100)
    }
  }
}