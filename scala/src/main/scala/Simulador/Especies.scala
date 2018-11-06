package Simulador

trait Raza{
  def bonificacionEnergia(carga:Int):Int = 0
}

case object Humano extends Raza
{

}

case class Saiyajin(nivelSaiyain:Int, tieneCola:Boolean) extends Raza
{
  override def bonificacionEnergia(carga : Int):Int = nivelSaiyain + carga
}

case object Androide extends Raza
case object Namekusein extends Raza
case object Monstruo extends Raza