package dragonBall

import scala.util.{Failure, Success, Try}


case class Guerrero(nombre : String,
                    estado: Estado = Ok,
                    energia: Int, raza : Raza,
                    items : List[Item] = List(),
                    movimientos : List[Movimiento] = List(),
                    roundsQueSeDejoFajar : Int = 0) {

  require(nombre.nonEmpty)
  require(energia >= 0)
  require(roundsQueSeDejoFajar >= 0)

  def seDejoFajar(): Guerrero = this.copy(roundsQueSeDejoFajar = roundsQueSeDejoFajar + 1)

  def aumentarEnergia(incremento: Int): Guerrero = {
    require(incremento >= 0)
    val nuevoGuerrero = this.copy(energia = this.raza.energiaMaxima.min(this.energia + incremento))
    if (nuevoGuerrero.estado == Muerto && nuevoGuerrero.energia > 0) nuevoGuerrero.copy(estado = Ok) else nuevoGuerrero
  }

  def disminuirEnergia(decremento: Int): Guerrero = {
    require(decremento >= 0)
    val nuevoGuerrero = this.copy(energia = 0.max(this.energia - decremento))
    if (nuevoGuerrero.energia == 0) nuevoGuerrero.murio() else nuevoGuerrero
  }

  def murio(): Guerrero = {
    raza.murio(this.copy(roundsQueSeDejoFajar = 0))
  }

  def quedoInconsciente(): Guerrero = {
    raza.quedoInconsciente(this.copy(roundsQueSeDejoFajar = 0))
  }

  def realizarMovimientoContra(movimiento: Movimiento, oponente: Guerrero): (Guerrero, Guerrero) = {
    if (tieneMovimiento(movimiento)) (this.estado, movimiento) match {
      case (Ok, _) => movimiento(this, oponente) // la Genkidama se encarga de limpiar las veces que fue fajado
      case (_, UsarItem(SemillaDelHermitanio)) => movimiento(this.copy(roundsQueSeDejoFajar = 0), oponente)
      case (_, _) => (this, oponente)
    }
    else (this.copy(roundsQueSeDejoFajar = 0), oponente) // en el caso de que el movimiento no haga efecto, igual le descuento las veces que se dejo fajar.
  }

  def tieneMovimiento(movimiento: Movimiento): Boolean = movimientos.contains(movimiento)

  def tieneItem(item: Item): Boolean = items.contains(item)

  def municion(): Option[Item] = this.items.find(i => i.isInstanceOf[Municion])

  def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  def usarItem(item: Item, oponente: Guerrero): (Guerrero, Guerrero) = {
    (item, estado) match {
      case (SemillaDelHermitanio, _) => item.apply(this, oponente)
      case (_, Inconsciente) => (this, oponente)
      case (_, _) => item.apply(this, oponente)
    }
  }

  def eliminarItem(item : Item) : Guerrero = {
    this.copy(items = this.items.filter(i => !i.eq(item)))
  }

  def tieneTodasLasEsferasDelDragon(): Boolean = EsferasDelDragon.todasLasEsferas.forall(items.contains)

  def esparcirEsferas(): Guerrero = this.copy(items = items.filter(i => !i.isInstanceOf[EsferaDelDragon]))

  def cantidadDeItems(): Int = items.size + {
    if(this.municion().isDefined) this.municion().get.asInstanceOf[Municion].cantidadActual - 1 else 0
  } // el -1 es para que el objeto Municion(1) no cuente 2 veces (una por el objeto y otra por la cantidad de municion)

  // Punto 1 -----------------------------------------------------------------------
  def movimientoMasEfectivoContra(oponente: Guerrero, unCriterio: CriterioSeleccionDeMovimiento): Option[Movimiento] = {
   Try(this.movimientos.maxBy(unCriterio(this, oponente))) match {
     case Success(mejorMovimiento) if (unCriterio(this, oponente)(mejorMovimiento) > 0) => Some(mejorMovimiento)
     case _ => None
    }
  }

  // Punto 2 -----------------------------------------------------------------------
  def pelearUnRound(movimientoElegido: Movimiento, oponente: Guerrero): ResultadoDeRound = {
    val (atacanteLuegoDelMovimiento, oponenteAfectado) = this.realizarMovimientoContra(movimientoElegido, oponente)
    val contraataQueMasEfectivo = oponenteAfectado.movimientoMasEfectivoContra(atacanteLuegoDelMovimiento, LoDejaConMayorVentajaEnKi)
    val (atacanteResultante, oponenteResultante) = oponenteAfectado.contraAtacar(atacanteLuegoDelMovimiento, contraataQueMasEfectivo).swap // swapeo al final porque en el contraataque se invirtieron los roles
    ResultadoDeRound(movimientoInicialAtacante = movimientoElegido,
                     movimientoContraataqueOponente = contraataQueMasEfectivo,
                     estadoFinalAtacante = atacanteResultante,
                     estadoFinalOponente = oponenteResultante)
  }

  private def contraAtacar(oponente: Guerrero, contraataQueMasEfectivo: Option[Movimiento]) : (Guerrero, Guerrero) = {
    contraataQueMasEfectivo match {
      case Some(mov) => this.realizarMovimientoContra(mov, oponente)
      case None if(movimientos.size > 0) => this.realizarMovimientoContra(movimientos.head, oponente) // si no tiene uno mas efectivo, realiza el primero
      case _ => (this, oponente)
    }
  }

  // Punto 3 -----------------------------------------------------------------------
  def planDeAtaqueContra(oponente : Guerrero, criterioSeleccionDeMovimiento: CriterioSeleccionDeMovimiento, cantidadDeRounds : Int) : Option[List[Movimiento]] = {
    Some(List.fill(cantidadDeRounds - 1)(1).foldLeft(List(pelearUnRound(movimientoMasEfectivoContra(oponente, criterioSeleccionDeMovimiento).getOrElse(return None), oponente)))((listaDeResultados, _) => {
      listaDeResultados.head.estadoFinalAtacante.pelearUnRound(listaDeResultados.head.estadoFinalAtacante.movimientoMasEfectivoContra(listaDeResultados.head.estadoFinalOponente, criterioSeleccionDeMovimiento).getOrElse(return None), listaDeResultados.head.estadoFinalOponente) :: listaDeResultados
    }).map(unResultado => unResultado.movimientoInicialAtacante).reverse)

    // version con prints :
    //val movimientoEfectivoPrimerRound = movimientoMasEfectivoContra(oponente, criterioSeleccionDeMovimiento).getOrElse(return None)
    //val resultadoPrimerRound = pelearUnRound(movimientoEfectivoPrimerRound, oponente)
    //println(s"Round: 1, estado inicial : $this, estado inicial oponente: $oponente, prox mov atacante: $movimientoEfectivoPrimerRound")
    //Some(List.fill(cantidadDeRounds - 1)(1).foldLeft(List(resultadoPrimerRound))((listaDeResultados, nroRound) => {
    //  val estadoFinalAtacante = listaDeResultados.head.estadoFinalAtacante
    //  val estadoFinalOponente = listaDeResultados.head.estadoFinalOponente
    //  val proximoMov = listaDeResultados.head.estadoFinalAtacante.movimientoMasEfectivoContra(estadoFinalOponente, criterioSeleccionDeMovimiento).getOrElse(return None)
    //  println(s"Round: ${nroRound+1}, estado inicial : $estadoFinalAtacante, estado inicial oponente: $estadoFinalOponente, prox mov atacante: $proximoMov")
    //  estadoFinalAtacante.pelearUnRound(proximoMov, estadoFinalOponente) :: listaDeResultados
    //}).map(unResultado => unResultado.movimientoInicialAtacante).reverse)
  }
}