package dragonBall

sealed abstract class Raza(energiaMaxima: Int){
  def aumentarEnergia(energiaActual : Int, incremento: Int): Int = {
    require(incremento >= 0)
    energiaMaxima.min(energiaActual + incremento)
  }

  def disminuirEnergia(energiaActual: Int, decremento: Int): Int = {
    require(decremento >= 0)
    0.max(energiaActual - decremento)
  }
}

case class Humano(energiaMaxima : Int) extends Raza(energiaMaxima)

case class Saiyajin(tieneCola : Boolean = true, nivelSS : Int = 1, energiaMaxima : Int) extends Raza (energiaMaxima) {
  require((1 to 4).contains(nivelSS)) // TODO quizas se puede parametrizar el maximo nivelSS, pero no lo pide el enunciado

  def cambiarDeFase(guerrero : Guerrero): Guerrero = {
    guerrero.copy(raza = this.copy(nivelSS = nivelSS + 1))
  } // problema : inconsistencia ... yo puedo hacer goku.raza.cambiarDeFase(vegeta)
}

case class Androide(energiaMaxima : Int) extends Raza (energiaMaxima) {
  override def aumentarEnergia(energiaActual: Int, incremento: Int): Int = 0
}

case class Guerrero(nombre: String, raza : Raza, energia: Int){
  require(nombre.nonEmpty)

  def aumentarEnergia(incremento : Int) : Guerrero = {
    copy(energia = raza.aumentarEnergia(energia, incremento))
  }

  def disminuirEnergia(decremento : Int) : Guerrero = {
    copy(energia = raza.disminuirEnergia(energia, decremento) )
  }

}