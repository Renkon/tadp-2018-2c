package  dragonBall

object DejarseFajar { // TODO corregir, esta no es la implementacion posta de DejarseFajar, no es esto lo que tiene que hacer
  def apply(guerrero : Guerrero) : Guerrero = {
    guerrero.raza match {
      case Humano(_) => guerrero.disminuirEnergia(10)
      case Saiyajin(_,_,_) => guerrero.disminuirEnergia(2)
    }
  }
}

object CargarKi {
  def apply(guerrero : Guerrero): Guerrero = {
    guerrero.raza match {
      case Saiyajin(_, nivelSS, _) => guerrero.aumentarEnergia(guerrero.energia * nivelSS)
      case Androide(_) => guerrero
      case _ => guerrero.aumentarEnergia(150)
    }
  }
}