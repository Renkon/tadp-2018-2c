package ar.com.tadp18c2.grupo1.guerrero.traits

import ar.com.tadp18c2.grupo1.guerrero.{Fusionado, Guerrero}

trait Fusionable extends Ki {
  def fusionar(fusionable : Fusionable) : Fusionado =
    Fusionado(
      nombre = this.nombre,
      inventario = this.inventario ++ fusionable.inventario,
      ki = (this.ki + fusionable.ki) / 2,
      kiMáximo = (this.kiMáximo + fusionable.kiMáximo) / 2
    )
}
