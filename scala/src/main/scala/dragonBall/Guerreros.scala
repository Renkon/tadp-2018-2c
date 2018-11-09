package dragonBall



case class Guerrero(nombre : String,
                    estado: Estado = Ok,
                    energia: Int, raza : Raza,
                    items : List[Item] = List(),
                    movimientos : List[Movimiento] = List(),
                    roundsQueSeDejoFajar : Int = 0) {

  require(nombre.nonEmpty)
  require(energia >= 0)
  require(roundsQueSeDejoFajar >= 0)

  def seDejoFajar() : Guerrero = this.copy(roundsQueSeDejoFajar = roundsQueSeDejoFajar + 1)

  def aumentarEnergia(incremento: Int): Guerrero = {
    require(incremento >= 0)
    val nuevoGuerrero = this.copy(energia = this.raza.energiaMaxima.min(this.energia + incremento))
    if(nuevoGuerrero.estado == Muerto && nuevoGuerrero.energia > 0) nuevoGuerrero.copy(estado = Ok) else nuevoGuerrero
  }

  def disminuirEnergia(decremento: Int): Guerrero = {
    require(decremento >= 0)
    val nuevoGuerrero = this.copy(energia = 0.max(this.energia - decremento))
    if(nuevoGuerrero.energia == 0) nuevoGuerrero.murio() else nuevoGuerrero
  }

  def murio() : Guerrero = {
    raza.murio(this.copy(roundsQueSeDejoFajar = 0))
  }

  def quedoInconsciente() : Guerrero = {
    raza.quedoInconsciente(this.copy(roundsQueSeDejoFajar = 0))
  }

  def realizarMovimientoContra(movimiento : Movimiento, oponente : Guerrero) : (Guerrero, Guerrero) = {
    if(tieneMovimiento(movimiento)) (this.estado, movimiento) match {
      case (Ok, nuevoMovimiento) =>
        if(this.roundsQueSeDejoFajar != 0 && nuevoMovimiento != DejarseFajar)
          movimiento(this.copy(roundsQueSeDejoFajar = 0), oponente)
        else  movimiento(this, oponente)
      case (_, UsarItem(SemillaDelHermitanio)) => movimiento(this.copy(roundsQueSeDejoFajar = 0), oponente)
      case (_, _) => (this, oponente)
    }
    else (this.copy(roundsQueSeDejoFajar = 0), oponente) // en el caso de que el movimiento no haga efecto, igual le descuento las veces que se dejo fajar.
  }

  def tieneMovimiento(movimiento: Movimiento) : Boolean = movimientos.contains(movimiento)

  def tieneItem(item : Item) : Boolean = items.contains(item)

  def municion(): Option[Item] = this.items.find(i => i.isInstanceOf[Municion])

  def agregarItem(item: Item) : Guerrero = copy(items = item :: items)

  def usarItem(item: Item, oponente : Guerrero) :(Guerrero, Guerrero) = {
    (item, estado) match {
      case (SemillaDelHermitanio, _) => item.apply(this, oponente)
      case (_, Inconsciente) => (this, oponente)
      case(_,_) => item.apply(this, oponente)
    }
  }

  def tieneTodasLasEsferasDelDragon() : Boolean = EsferasDelDragon.todasLasEsferas.forall(items.contains)

  def esparcirEsferas() : Guerrero = this.copy(items = items.filter(i => !i.isInstanceOf[EsferaDelDragon]))

}
