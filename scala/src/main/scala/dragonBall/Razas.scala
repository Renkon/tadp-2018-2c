package dragonBall

class CambioDeFaseException(msg : String) extends RuntimeException(msg)

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

  private def EstadoMono() : Saiyajin = {
    val razaMono = Saiyajin(fase = Fases.Mono, tieneCola = true)
    razaMono.energiaMaxima = this.energiaMaxima * 3
    razaMono
  }

  def nivelDeFase() : Int = fase

  def cambiarDeFase(guerrero: Guerrero, nuevaFase : Int) : Guerrero = {
    (nivelDeFase(), nuevaFase, tieneCola) match {
      case (_, Fases.Mono, true) => guerrero.copy(raza = EstadoMono()).aumentarEnergia(EstadoMono().energiaMaxima - guerrero.energia)
      case (_, Fases.Mono, false) => guerrero
      case (Fases.Mono, nuevaFase, _) if(nuevaFase != Fases.Normal) => throw new CambioDeFaseException("El estado Mono no se puede combinar con otra fase Saiyajin")
      case (_,_,_) => guerrero.copy(raza = Saiyajin(fase = nuevaFase, tieneCola = tieneCola))
    } // TODO no se si la restriccion que dice "no puede combinarse con SuperSaiyajin" se refiere a una excepcion o que lo dejas igual...
  }
}

object Fases {
  val Normal = 1

  val SSFase1 = 2

  val SSFase2 = 3

  val SSFase3 = 4

  val SSFase4 = 5

  val Mono = 0 // FIXME que onda esto cuando CargaKi ??
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


