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

case class Saiyajin(fase : Int = Fases.Normal, tieneCola: Boolean = false) extends Raza {
  override var energiaMaxima = 300

  def nivelDeFase() : Int = fase

  def cambiarDeFase(guerrero: Guerrero, nuevaFase : Int) : Guerrero = {
    guerrero.copy(raza = Saiyajin(fase = nuevaFase, tieneCola = tieneCola))
  }
}

object Fases {
  val Normal = 1

  val SSFase1 = 2

  val SSFase2 = 3

  val SSFase3 = 4

  val SSFase4 = 5

  val Mono = 0
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