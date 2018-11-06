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

case class Monstruo(digestion : Digestion) extends Raza {
  override var energiaMaxima: Int = 200

  def darDeComerA(atacante : Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    if(atacante.energia > oponente.energia) digestion.hacerDigerirA(atacante, oponente) else (atacante, oponente)
    // para evitar el codigo repetido del estado muerto parece que se puede poner aca,
    // PERO, si cell se quiere comer a mrSatan y yo lo doy por muerto antes de evaluar a que raza pertenece, el test fallaria
  }
}

// type Digestion = Guerrero => Guerrero => (Guerrero, Guerrero) // FIXME por que esto no me anda ?
sealed trait Digestion {
  def hacerDigerirA(atacante : Guerrero, oponente: Guerrero) : (Guerrero, Guerrero)
}

case object DigestionMajinBuu extends Digestion{
  def hacerDigerirA(atacante : Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    (atacante.copy(movimientos = oponente.movimientos), oponente.copy(estado = Muerto))
  }
}

case object DigestionCell extends Digestion{
  def hacerDigerirA(atacante : Guerrero, oponente: Guerrero) : (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza:Androide => (atacante.copy(movimientos = oponente.movimientos ::: atacante.movimientos), oponente.copy(estado = Muerto))
      case _ => (atacante, oponente)
    }
  }
}


