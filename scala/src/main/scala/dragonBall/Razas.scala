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

  def quedoInconsciente(guerrero : Guerrero): Guerrero = guerrero
}

case class Saiyajin(fase: Fase = Normal, tieneCola: Boolean = false) extends Raza {
  override val energiaMaxima: Int = fase.energiaMaxima

  def nivelDeFase : Int = fase.nivel

  def siguienteNivel(guerrero : Guerrero) : Guerrero = {
    if(guerrero.energia >= this.energiaMaxima / 2)
      guerrero.copy(raza = Saiyajin(fase = fase.siguienteNivel, tieneCola = tieneCola))
    else guerrero
  } // interpreto que la unica forma de perder el nivel actual de SSJ que tenes es quedar incosnciente, y que uno no puede ir hacia atras en las fases

  def suColaFueCortada(guerrero : Guerrero) : Guerrero = {
    this.fase match {
      case Mono => guerrero.copy(raza = Saiyajin(fase = Normal, tieneCola = false)).disminuirEnergia(guerrero.energia - 1)
      case _ => guerrero.copy(raza = this.copy(tieneCola = false)).disminuirEnergia(guerrero.energia - 1)
    }
  } // interpreto que esta es la unica forma de salir de estado Mono

  def convertirseEnMono(guerrero: Guerrero) : Guerrero = {
    if(tieneCola && guerrero.tieneItem(FotoDeLaLuna))
      guerrero.copy(raza = Saiyajin(fase = Mono, tieneCola = true)).aumentarEnergia(Mono.energiaMaxima - guerrero.energia)
    else
      guerrero
 // TODO no se si la restriccion que dice "no puede combinarse con SuperSaiyajin" se refiere a una excepcion o que lo dejas igual...
  }

  override def quedoInconsciente(guerrero: Guerrero): Guerrero = {
    guerrero.raza match {
      case raza:Saiyajin => guerrero.copy(raza = raza.copy(fase = Normal))
      case _ => guerrero
    }
  }
}

sealed trait Fase {
  val siguienteNivel : Fase
  val nivel : Int
  def energiaMaxima : Int
  val modificadorEnergia = 5
}

case object Normal extends Fase {
  override val siguienteNivel = SSJFase1
  override val nivel = 1
  override def energiaMaxima = 350
}
case object SSJFase1 extends Fase {
  override val siguienteNivel = SSJFase2
  override val nivel = 2
  override def energiaMaxima = Normal.energiaMaxima * modificadorEnergia
}
case object SSJFase2 extends Fase {
  override val siguienteNivel = SSJFase3
  override val nivel = 3
  override def energiaMaxima = SSJFase1.energiaMaxima * modificadorEnergia
}
case object SSJFase3 extends Fase {
  override val siguienteNivel = SSJFase3
  override val nivel = 4
  override def energiaMaxima = SSJFase2.energiaMaxima * modificadorEnergia
}
case object Mono extends Fase { // antes el mono era case class y recibia una faseBase para que pudiera volver a ella cuando se corta la cola, pero el enunciado dice que cuando sos mono "SE PIERDE" el estado SSJ
  override val siguienteNivel = Mono
  override val nivel = 0
  override def energiaMaxima: Int = Normal.energiaMaxima * 3
}

case class Androide() extends Raza {
  override val energiaMaxima: Int = 400

  override def aumentarEnergia(guerrero: Guerrero, incremento: Int): Guerrero = {
    guerrero.copy(energia = 0)
  }
}

case class Namekusein() extends Raza {
  override val energiaMaxima: Int = 330
}

case class Humano() extends Raza {
  override val energiaMaxima: Int = 300
}

case class Monstruo(digestion : Digestion) extends Raza {
  override val energiaMaxima: Int = 450

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


