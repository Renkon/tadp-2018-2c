package Simulador

case class Guerrero(name:String, raza:Raza, energia:Int, energiaMaxima:Int, items:List[Item]) {

  def nombre():String = this.name

  def aumentarEnergia(cargaDeEnergia:Int): Guerrero = this.copy( energia = energiaMaxima.min(energia+cargaDeEnergia+raza.bonificacionEnergia(cargaDeEnergia)) )


}
