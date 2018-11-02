package dragonBall

trait Guerrero {
  val nombre: String
  val estado : Estado
  val energia: Int
  val energiaMaxima : Int
  val items : List[Item]

  require(nombre.nonEmpty)
  require(energia >= 0)
  require(energia <= energiaMaxima)

  def aumentarEnergia(incremento : Int) : Guerrero
  def disminuirEnergia(decremento : Int) : Guerrero
  def tieneItem(item : Item) : Boolean = items.contains(item)
  def municiones(): Option[Item] = this.items.find(i => i.isInstanceOf[Municion])
  def agregarItem(item: Item) : Guerrero
}

case class Humano(nombre: String,
                  estado : Estado = Ok,
                  energia: Int,
                  energiaMaxima : Int,
                  items : List[Item] = List()) extends Guerrero{

  override def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = energiaMaxima.min(energia + incremento))
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = 0.max(energia - decremento))
  }
}

case class Namekusein (nombre: String,
                       estado : Estado = Ok,
                       energia: Int,
                       energiaMaxima : Int,
                       items : List[Item] = List()) extends Guerrero{

  override def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = energiaMaxima.min(energia + incremento))
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = 0.max(energia - decremento))
  }
}

case class Saiyajin(tieneCola : Boolean = true,
                    nivelSS : Int = 1,
                    nombre : String,
                    estado : Estado = Ok,
                    energia : Int,
                    energiaMaxima : Int,
                    items : List[Item] = List()) extends Guerrero {

  override def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  require((0 to 4).contains(nivelSS)) // TODO quizas se puede parametrizar el maximo nivelSS, pero no lo pide el enunciado

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = energiaMaxima.min(energia + incremento))
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = 0.max(energia - decremento))
  }

  def cambiarDeFase(guerrero : Guerrero): Guerrero = {
    copy(nivelSS = nivelSS + 1)
  }
}

case class Androide(nombre : String,
                    estado : Estado = Ok,
                    energia : Int = 0,
                    energiaMaxima : Int,
                    items : List[Item] = List()) extends Guerrero {

  override def agregarItem(item: Item): Guerrero = copy(items = item :: items)

  override def aumentarEnergia(incremento: Int): Guerrero = {
    copy(energia = 0)
  }

  override def disminuirEnergia(decremento: Int): Guerrero = {
    copy(energia = 0)
  }
}

