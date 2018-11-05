package dragonBall

sealed trait Raza {
  var energiaMaxima: Int

  def aumentarEnergia(guerrero: Guerrero, incremento: Int): Guerrero = {
    guerrero.copy(energia = this.energiaMaxima.min(guerrero.energia + incremento))
  }

  def disminuirEnergia(guerrero: Guerrero, decremento: Int): Guerrero = {
    guerrero.copy(energia = 0.max(guerrero.energia - decremento))
  }
}

case class Saiyajin(nivelSS: Int, tieneCola: Boolean) extends Raza {
  require((0 to 4).contains(nivelSS)) // TODO quizas se puede parametrizar el maximo nivelSS, pero no lo pide el enunciado

  override var energiaMaxima = 300

  def aumentarFase(guerrero : Guerrero): Guerrero = {
    cambiarDeFase(guerrero, +1)
  }

  def disminuirFase(guerrero : Guerrero): Guerrero = {
    cambiarDeFase(guerrero, -1)
  }

  private def cambiarDeFase(guerrero: Guerrero, modificador : Int)= {
    guerrero.copy(raza = Saiyajin(nivelSS = this.nivelSS + modificador, tieneCola = tieneCola))
  }
}

case class Androide() extends Raza {
  override var energiaMaxima: Int = 200

  override def aumentarEnergia(guerrero: Guerrero, incremento: Int): Guerrero = {
    guerrero.copy(energia = 0)
  }
}

case class Namekusein() extends Raza {
  override var energiaMaxima: Int = 200
}

case class Humano() extends Raza {
  override var energiaMaxima: Int = 200
}

case class Monstruo() extends Raza {
  override var energiaMaxima: Int = 200
}