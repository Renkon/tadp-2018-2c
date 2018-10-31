package dragonBall

case class Guerrero(nombre: String, energia : Int, raza : Raza){
  require(energia >= 0)
  require(nombre.nonEmpty)

  def esUn(raza : Raza) : Boolean = this.raza == raza
}

sealed trait Raza

case class Humano() extends Raza // TODO decidir si debe ser un well known object o una instancia para cada guerrero, va a depender del estado que deleguemos en la raza.
case class Saiyajin(tieneCola : Boolean = true) extends Raza // TODO decidir si debe ser un well known object o una instancia para cada guerrero, va a depender del estado que deleguemos en la raza.