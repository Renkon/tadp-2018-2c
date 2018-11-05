package dragonBall

case class Guerrero(nombre : String, estado: Estado = Ok, energia: Int, raza : Raza, items : List[Item] = List()) {
  require(nombre.nonEmpty)
  require(energia >= 0)

  def aumentarEnergia(incremento : Int) : Guerrero = raza.aumentarEnergia(this, incremento)

  def disminuirEnergia(decremento : Int) : Guerrero = raza.disminuirEnergia(this, decremento)

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
}
