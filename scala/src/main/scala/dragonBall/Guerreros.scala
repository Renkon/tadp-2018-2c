package dragonBall

abstract class Guerrero(val nombre: String, val energia: Int, val energiaMaxima : Int){
    require(nombre.nonEmpty)
  require(energia >= 0)
  require(energia <= energiaMaxima)

  def aumentarEnergia(incremento : Int) : Guerrero
  def disminuirEnergia(decremento : Int) : Guerrero
}

case class Humano(override val nombre: String,
                  override val energia: Int,
                  override val energiaMaxima : Int) extends Guerrero(nombre, energia, energiaMaxima){

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = energia + incremento)
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = energia - decremento)
  }
}

case class Saiyajin(tieneCola : Boolean = true,
                    nivelSS : Int = 1,
                    override val nombre : String,
                    override val energia : Int,
                    override val energiaMaxima : Int) extends Guerrero (nombre, energia, energiaMaxima) {

  require((0 to 4).contains(nivelSS)) // TODO quizas se puede parametrizar el maximo nivelSS, pero no lo pide el enunciado

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = energia + incremento)
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = energia - decremento)
  }

  def cambiarDeFase(guerrero : Guerrero): Guerrero = {
    copy(nivelSS = nivelSS + 1)
  }
}

case class Androide(override val nombre : String,
                    override val energia : Int = 0,
                    override val energiaMaxima : Int) extends Guerrero (nombre, energia, energiaMaxima) {

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = 0)
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = 0)
  }
}

