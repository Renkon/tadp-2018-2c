package dragonBall



case class Guerrero(nombre : String,
                    estado: Estado = Ok,
                    energia: Int, raza : Raza,
                    items : List[Item] = List(),
                    movimientos : List[Movimiento] = List()) {

  require(nombre.nonEmpty)
  require(energia >= 0)

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

  private def murio() : Guerrero = raza.murio(this)

  def quedoInconsciente() : Guerrero = raza.quedoInconsciente(this)

  def realizarMovimientoContra(movimiento : Movimiento, oponente : Guerrero) : (Guerrero, Guerrero) = {
    if(tieneMovimiento(movimiento)) (this.estado, movimiento) match {
      case (Ok, _) => movimiento(this, oponente)
      case (_, UsarItem(SemillaDelHermitanio)) => movimiento(this, oponente)
      case (_, _) => (this, oponente)
    }
    else (this, oponente)
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
