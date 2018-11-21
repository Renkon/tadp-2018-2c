package dragonBall

import scala.util.{Success, Try}


case class Guerrero(nombre: String,
                    estado: Estado = Ok,
                    energia: Int, raza: Raza,
                    items: List[Item] = List(),
                    movimientos: List[Movimiento] = List(),
                    roundsQueSeDejoFajar: Int = 0) {

  /* Validaciones */
  require(nombre.nonEmpty, "El guerrero debe poseer un nombre")
  require(energia >= 0, "La energia del guerrero no puede ser un numero negativo")
  require(roundsQueSeDejoFajar >= 0, "El numero de veces que se fajo al guerrero no puede ser un nuemero negativo")


  /* "Accesors" */
  def energiaMaxima(): Int = this.raza.energiaMaxima

  def tieneMovimiento(movimiento: Movimiento): Boolean = movimientos.contains(movimiento)

  def tieneItem(item: Item): Boolean = items.contains(item)

  def municion(): Option[Municion] = this.items.collect { case municion@Municion(_) => municion }.headOption

  def tieneTodasLasEsferasDelDragon(): Boolean = EsferasDelDragon.todasLasEsferas.forall(items.contains)

  def cantidadDeItems(): Int = items.size + {
    if (this.municion().isDefined) Try(this.municion().get.cantidadActual).get - 1 else 0
  } // el -1 es para que el objeto Municion(1) no cuente 2 veces (una por el objeto y otra por la cantidad de municion)


  /* Copys */
  def dejarseFajar(): Guerrero = this.copy(roundsQueSeDejoFajar = roundsQueSeDejoFajar + 1)

  def aumentarEnergia(incremento: Int): Guerrero = {
    require(incremento >= 0)

    val nuevoGuerrero = this.copy(energia = this.energiaMaxima.min(this.energia + incremento))

    if (nuevoGuerrero.estado == Muerto && nuevoGuerrero.energia > 0)
      nuevoGuerrero.copy(estado = Ok)
    else
      nuevoGuerrero
  }

  def disminuirEnergia(decremento: Int): Guerrero = {
    require(decremento >= 0)

    val nuevoGuerrero = this.copy(energia = 0.max(this.energia - decremento))

    if (nuevoGuerrero.energia == 0)
      nuevoGuerrero.morir()
    else
      nuevoGuerrero
  }

  def morir(): Guerrero = raza.murio(this.copy(roundsQueSeDejoFajar = 0))

  def quedarInconsciente(): Guerrero = raza.quedoInconsciente(this.copy(roundsQueSeDejoFajar = 0))


  /* Manejo de Items */
  def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  def eliminarItem(item: Item): Guerrero = {
    this.copy(items = this.items.filter(i => !i.eq(item)))
  }

  def esparcirEsferas(): Guerrero = this.copy(items = items.filter { case EsferaDelDragon(_) => false })

  def usarItem(item: Item, oponente: Guerrero): (Guerrero, Guerrero) = item.apply(this, oponente)


  /* Movimientos */
  def realizarMovimientoContra(movimiento: Movimiento, oponente: Guerrero): (Guerrero, Guerrero) = {
    if (tieneMovimiento(movimiento)) (this.estado, movimiento) match {
      case (Ok, DejarseFajar) | (Ok, AtacarCon(Genkidama)) => movimiento(this, oponente)
      case (Ok, _) | (_, UsarItem(SemillaDelHermitanio)) => movimiento(this.copy(roundsQueSeDejoFajar = 0), oponente)
      case (_, _) => (this, oponente)
    }
    else
      (this.copy(roundsQueSeDejoFajar = 0), oponente)
  }


  // Punto 1 -----------------------------------------------------------------------
  def movimientoMasEfectivoContra(oponente: Guerrero, unCriterio: CriterioSeleccionDeMovimiento): Option[Movimiento] = {
    Try(this.movimientos.filter(esMovimientoBuenoContra(oponente, unCriterio)).maxBy(unCriterio(this, oponente))).toOption
  }

  private def esMovimientoBuenoContra(oponente: Guerrero, unCriterio: CriterioSeleccionDeMovimiento)(movimiento: Movimiento): Boolean = {
    unCriterio(this, oponente)(movimiento) > 0
  }

  // Punto 2 -----------------------------------------------------------------------
  def pelearUnRound(movimientoElegido: Movimiento, oponente: Guerrero): ResultadoDeRound = {
    val (atacanteLuegoDelMovimiento, oponenteAfectado) = this.realizarMovimientoContra(movimientoElegido, oponente)
    val (oponenteResultante, atacanteResultante, contraataqueMasEfectivo) = oponenteAfectado.contraAtacar(atacanteLuegoDelMovimiento) // swapeo al final porque en el contraataque se invirtieron los roles
    ResultadoDeRound(movimientoInicialAtacante = movimientoElegido,
      movimientoContraataqueOponente = contraataqueMasEfectivo,
      estadoFinalAtacante = atacanteResultante,
      estadoFinalOponente = oponenteResultante)
  }

  private def contraAtacar(oponente: Guerrero): (Guerrero, Guerrero, Option[Movimiento]) = {
    val contraataqueMasEfectivo = this.movimientoMasEfectivoContra(oponente, LoDejaConMayorVentajaEnKi)
    val (atacanteResultante, oponenteResultante) = contraataqueMasEfectivo match {
      case Some(mov) => this.realizarMovimientoContra(mov, oponente)
      case None if movimientos.nonEmpty => this.realizarMovimientoContra(movimientos.head, oponente) // si no tiene uno mas efectivo, realiza el primero
      case _ => (this, oponente)
    }
    (atacanteResultante, oponenteResultante, contraataqueMasEfectivo)
  }

  // Punto 3 -----------------------------------------------------------------------
  def planDeAtaqueContra(oponente: Guerrero, criterioSeleccionDeMovimiento: CriterioSeleccionDeMovimiento, cantidadDeRounds: Int): Option[List[Movimiento]] = {
    Some(List.fill(cantidadDeRounds - 1)(1).foldLeft(List(pelearUnRound(movimientoMasEfectivoContra(oponente, criterioSeleccionDeMovimiento).getOrElse(return None), oponente)))((listaDeResultados, _) => {
      listaDeResultados :+ listaDeResultados.head.estadoFinalAtacante.pelearUnRound(listaDeResultados.head.estadoFinalAtacante.movimientoMasEfectivoContra(listaDeResultados.head.estadoFinalOponente, criterioSeleccionDeMovimiento).getOrElse(return None), listaDeResultados.head.estadoFinalOponente)
    }).map(unResultado => unResultado.movimientoInicialAtacante))
  }

  // Punto 4 -----------------------------------------------------------------------
  def pelearContra(oponente: Guerrero, planDeAtaque: List[Movimiento]): ResultadoDePelea = {
    planDeAtaque.tail.foldLeft(ResultadoDePelea(pelearUnRound(planDeAtaque.head, oponente)))((resultadoDePelea, movimientoDelRound) => {
      resultadoDePelea match {
        case huboGanador@Ganador(_) => huboGanador
        case SigueElCombate(atacanteProximoRound, oponenteProximoRound) =>
          ResultadoDePelea(atacanteProximoRound.pelearUnRound(movimientoDelRound, oponenteProximoRound))
      }
    })
  }
}