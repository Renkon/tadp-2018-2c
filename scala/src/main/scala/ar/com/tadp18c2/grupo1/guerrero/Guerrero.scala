package ar.com.tadp18c2.grupo1.guerrero

import ar.com.tadp18c2.grupo1.guerrero.others.{EstadoCola, SuperSaiyan}
import ar.com.tadp18c2.grupo1.guerrero.traits._

abstract class Guerrero(nombre: String, inventario: List[Item])
{
}

case class Humano(nombre: String, inventario: List[Item], ki: Int, kiMáximo: Int)
  extends Guerrero(nombre, inventario) with Ki with Fusionable

case class Saiyajin(nombre: String, inventario: List[Item], ki: Int, kiMáximo: Int,
                    cola: EstadoCola, superSaiyan: SuperSaiyan = SuperSaiyan(0))
  extends Guerrero(nombre, inventario) with Ki with Saiyajinable with Fusionable

case class Androide(nombre: String, inventario: List[Item], batería: Int, bateríaMáxima: Int)
  extends Guerrero(nombre, inventario) with Batería with InmunidadInconsciencia

case class Namekusein(nombre: String, inventario: List[Item], ki: Int, kiMáximo: Int)
  extends Guerrero(nombre, inventario) with Ki with Fusionable

case class Monstruo(nombre: String, inventario: List[Item], ki: Int, kiMáximo: Int)
  extends Guerrero(nombre, inventario) with Ki

case class Fusionado(nombre: String, inventario: List[Item], ki: Int, kiMáximo: Int)
  extends Guerrero(nombre, inventario) with Ki
