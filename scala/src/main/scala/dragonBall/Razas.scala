package dragonBall

class CambioDeFaseException(msg : String) extends RuntimeException(msg)

sealed trait Raza {
  val energiaMaxima: Int

  def aumentarEnergia(guerrero: Guerrero, incremento: Int): Guerrero = {
    guerrero.copy(energia = this.energiaMaxima.min(guerrero.energia + incremento))
  }

  def disminuirEnergia(guerrero: Guerrero, decremento: Int): Guerrero = {
    guerrero.copy(energia = 0.max(guerrero.energia - decremento))
  }
}

case class Saiyajin(fase: Fase = Normal, tieneCola: Boolean = false) extends Raza {
  override val energiaMaxima: Int = fase.energiaMaxima

  private def EstadoMono() : Saiyajin = {
    val razaMono = Saiyajin(fase = Mono(fase), tieneCola = true)
    razaMono
  }

  def nivelDeFase() : Int = fase.nivel()

  def siguienteNivel(guerrero : Guerrero) : Guerrero = {
    if(guerrero.energia >= this.energiaMaxima / 2)
      cambiarDeFase(guerrero, fase.siguienteNivel)
    else guerrero
  }

  def suColaFueCortada(guerrero : Guerrero) : Guerrero = {
    //case (Fases.Mono) => (atacante, oponente.copy(raza = Saiyajin(fase = Fases.Normal, tieneCola = false)).disminuirEnergia(oponente.energia - 1))
    //case (faseActual) => (atacante, oponente.copy(raza = Saiyajin(fase = faseActual, tieneCola = false)).disminuirEnergia(oponente.energia - 1))
    this.fase match {
      case faseActual: Mono => guerrero.copy(energia = 1, fase = Saiyajin(fase = faseActual.faseBase, tieneCola = false))
    }

    
  }

  def cambiarDeFase(guerrero: Guerrero, nuevaFase : Fase) : Guerrero = {
    (fase, nuevaFase, tieneCola) match {
      case (_, faseActual:Mono, true) =>
        guerrero.copy(raza = EstadoMono()).aumentarEnergia(EstadoMono().energiaMaxima - guerrero.energia)
      case (_, faseActual:Mono, false) => guerrero
      case (faseActual:Mono, nuevaFase, _) if(nuevaFase != Normal) =>
        throw new CambioDeFaseException("El estado Mono no se puede combinar con otra fase Saiyajin")
      case (_,_,_) => guerrero.copy(raza = Saiyajin(fase = nuevaFase, tieneCola = tieneCola))
    } // TODO no se si la restriccion que dice "no puede combinarse con SuperSaiyajin" se refiere a una excepcion o que lo dejas igual...
  }
} // FIME tengo cambiarDeFase y siguienteNivel porque entiendo que la secuencia de evolucion
  // puede ser Normal -> SSJ1 -> SSJ2 -> SSJ3, pero si queres ir a Mono, lo podes hacer
  // estando en cualquiera de estas transformaciones, y si estas en mono tambien podes
  // querer bajar a la que tenias originalmente

sealed trait Fase {
  val siguienteNivel : Fase
  val nivel : Int
  val energiaMaxima : Int
  val modificadorEnergia = 5
}

case object Normal extends Fase {
  override val siguienteNivel = SSJFase1
  override val nivel = 1
  override val energiaMaxima = 300
}
case object SSJFase1 extends Fase {
  override val siguienteNivel = SSJFase2
  override val nivel = 2
  override val energiaMaxima = Normal.energiaMaxima * modificadorEnergia
}
case object SSJFase2 extends Fase {
  override val siguienteNivel = SSJFase3
  override val nivel = 3
  override val energiaMaxima = SSJFase1.energiaMaxima * modificadorEnergia
}
case object SSJFase3 extends Fase {
  override val siguienteNivel = SSJFase3
  override val nivel = 4
  override val energiaMaxima = SSJFase3.energiaMaxima * modificadorEnergia
}
case class Mono(faseBase: Fase) extends Fase {
  override val siguienteNivel = Mono(faseBase)
  override val nivel = 0
  override val energiaMaxima: Int = faseBase.energiaMaxima * 3
}

case class Androide() extends Raza {
  override val energiaMaxima: Int = 200

  override def aumentarEnergia(guerrero: Guerrero, incremento: Int): Guerrero = {
    guerrero.copy(energia = 0)
  }
}

case class Namekusein() extends Raza {
  override val energiaMaxima: Int = 200
}

case class Humano() extends Raza {
  override val energiaMaxima: Int = 200
}

case class Monstruo(digestion : Digestion) extends Raza {
  override val energiaMaxima: Int = 200

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


