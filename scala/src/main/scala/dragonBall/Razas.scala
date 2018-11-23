package dragonBall

sealed trait Raza {
  def energiaMaxima: Int

  def quedoInconsciente(guerrero: Guerrero): Guerrero = guerrero.copy(estado = Inconsciente)

  def murio(guerrero: Guerrero): Guerrero = guerrero.copy(estado = Muerto)
}

sealed trait Fusionable

case class Fusionado(guerreroOriginal: Guerrero, companieroDeFusion: Guerrero) extends Raza {
  // la idea es que no puedas instanciar a goku pasandole su raza Fusionado(goku),
  // que si o si lo tengas que fusionar luego de haberlo instanciado
  // Eso si, existe inconsistencia si haces goku = ... raza = Fusionado(krilin) ...
  // entonces cuando la fusion quede inconsciente te devuelve a krilin ... ¿ tengo en cuenta esos casos ?

  override def energiaMaxima: Int = guerreroOriginal.energiaMaxima + companieroDeFusion.energiaMaxima

  override def quedoInconsciente(guerrero: Guerrero): Guerrero = super.quedoInconsciente(guerreroOriginal.copy(energia = guerrero.energia))

  // asumo que la energia de la fusion, en alguno de estos dos estados, es siempre menor al maximo y cercana a 0
  override def murio(guerrero: Guerrero): Guerrero = super.murio(guerreroOriginal.copy(energia = guerrero.energia))
}


/* Inicio Saiyajin */
case class Saiyajin(fase: Fase = Normal, tieneCola: Boolean = false) extends Raza with Fusionable {

  override def energiaMaxima: Int = 350 * fase.energiaBonusPorFase

  def nivelDeFase(): Int = {
    this.fase match {
      case Mono => 0
      case SuperSaiyan(nivel) => nivel
      case Normal => 1
    }
  }

  def siguienteNivel(guerrero: Guerrero): Guerrero = {
    require(this.fase != Mono, "Los Saiyains Monos no pueden cambiar de fase")

    if (guerrero.energia >= (this.energiaMaxima / 2))
      guerrero.copy(raza = Saiyajin(fase = fase.siguienteNivel, tieneCola = tieneCola))
    else
      guerrero
  }

  def suColaFueCortada(guerrero: Guerrero): Guerrero = {
    this.fase match {
      case Mono => guerrero.copy(raza = Saiyajin(fase = Normal, tieneCola = false)).disminuirEnergia(guerrero.energia - 1)
      case _ => guerrero.copy(raza = this.copy(tieneCola = false)).disminuirEnergia(guerrero.energia - 1)
    }
  }

  def convertirseEnMono(guerrero: Guerrero): Guerrero = {
    if (tieneCola && guerrero.tieneItem(FotoDeLaLuna))
    {
      val ozaru = guerrero.copy(raza = Saiyajin(fase = Mono, tieneCola = true))
      val energiaARecuperar = ozaru.energiaMaxima() - ozaru.energia
      ozaru.aumentarEnergia(energiaARecuperar)
    }
    else
      guerrero
  }

  override def quedoInconsciente(guerrero: Guerrero): Guerrero = {
    val guerreroInconsciente = super.quedoInconsciente(guerrero)

    this.fase match {
      case SuperSaiyan(_) => guerreroInconsciente.copy(raza = this.copy(fase=Normal))
      case _ => guerreroInconsciente
    }
  }
}

sealed trait Fase {
  val modificadorEnergia: Int = 5

  def siguienteNivel: Fase

  def energiaBonusPorFase: Int
}

case object Normal extends Fase {
  override def siguienteNivel: Fase = SuperSaiyan(1)

  override def energiaBonusPorFase: Int = modificadorEnergia * 1
}

case class SuperSaiyan(nivel: Int) extends Fase {
  require(nivel > 0 && nivel < 5, "Solo hay SuperSaiyans de nivel 1, 2, 3 o 4.")

  override def siguienteNivel: Fase = SuperSaiyan(nivel + 1)

  override def energiaBonusPorFase: Int = modificadorEnergia * nivel
}

case object Mono extends Fase {
  override def siguienteNivel: Fase = null

  override def energiaBonusPorFase: Int = modificadorEnergia * 3
}

/* Fin Saiyajin */


/* Inicio Adnroide */
case class Androide() extends Raza {
  override def energiaMaxima: Int = 400

  override def quedoInconsciente(guerrero: Guerrero): Guerrero = guerrero
}

/* Fin Adnroide */


/* Inicio Nemekusein */
case class Namekusein() extends Raza with Fusionable {
  override def energiaMaxima: Int = 330
}

/* Fin Nemekusein */


/* Inicio Humano */
case class Humano() extends Raza with Fusionable {
  override def energiaMaxima: Int = 300
}

/* Fin Humano */


/* Inicio Monstruo */

case class Monstruo(digestion: Digestion) extends Raza {
  override def energiaMaxima: Int = 450

  def darDeComerA(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    if (atacante.energia > oponente.energia) digestion.hacerDigerirA(atacante, oponente) else (atacante, oponente)
    // para evitar el codigo repetido del estado muerto parece que se puede poner aca,
    // PERO, si cell se quiere comer a mrSatan y yo lo doy por muerto antes de evaluar a que raza pertenece, el test fallaria
  }
}

sealed trait Digestion {
  def hacerDigerirA(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero)
}

case object DigestionMajinBuu extends Digestion {
  def hacerDigerirA(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    (atacante.copy(movimientos = oponente.movimientos), oponente.disminuirEnergia(oponente.energia))
  }
}

case object DigestionCell extends Digestion {
  def hacerDigerirA(atacante: Guerrero, oponente: Guerrero): (Guerrero, Guerrero) = {
    oponente.raza match {
      case raza: Androide => (atacante.copy(movimientos = oponente.movimientos ::: atacante.movimientos), oponente.disminuirEnergia(oponente.energia))
      case _ => (atacante, oponente)
    }
  }
}

/* Fin Monstruo */


/* Interpretaciones que Asumimos:
*
*   1) Interpretamos que la unica forma de perder el nivel actual de SSJ que tenes es quedar incosnciente, y que uno no puede ir hacia atras en las fases.
*
*   2) Interpretamos que la unica forma de salir de estado Mono es perder la cola.
*
*   3)
*
* */