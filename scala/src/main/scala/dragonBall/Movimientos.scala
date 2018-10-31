package  dragonBall

object DejarseFajar {
  def apply(guerrero : Guerrero) : Guerrero = {
    guerrero.raza match {
      case Humano() => guerrero.copy(energia = guerrero.energia - 10)
      case Saiyajin(_) => guerrero.copy(energia = guerrero.energia - 2)
    }
  }
}
