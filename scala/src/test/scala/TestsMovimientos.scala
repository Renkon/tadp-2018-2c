package Simulador

import Simulador.MovimientosContainer._

import org.scalatest.{FreeSpec, Matchers}

class TestsMovimientos extends FreeSpec with Matchers {

  "Movimientos" - {

    "Tests de Movimientos" - {

        val goku = new Guerrero(name = "Kakaroto", raza = Saiyajin(1, false), energia = 1000, energiaMaxima = 100000, items = List(SemillaErmitanio))
        val krillin = new Guerrero(name = "Krillin", raza = Humano, energia = 1, energiaMaxima = 150, items = List(SemillaErmitanio))
        val picoro = new Guerrero(name = "Picoro", raza = Namekusein, energia = 2500, energiaMaxima = 10000, items = List(SemillaErmitanio))
        val viejito = new Guerrero(name = "20", raza = Androide, energia = 10, energiaMaxima = 1000, items = List(SemillaErmitanio))
        val boo = new Guerrero(name = "MajinBoo", raza = Monstruo, energia = 10, energiaMaxima = 1000, items = List(SemillaErmitanio))

      "Cargar KI con un humano aumenta en 100 unidades su energia" in {

        val guerrerosCargados = CargarKi(krillin,goku)
        val energiaCargada = guerrerosCargados._1.energia

        energiaCargada shouldBe 101
      }

      "Cargar KI con un androide no altera su energia" in {

        val guerrerosCargados = CargarKi(viejito,goku)
        val energiaCargada = guerrerosCargados._1.energia

        energiaCargada shouldBe viejito.energia
      }

      "Cargar KI con un saiyain aumenta su energia en 100 mas su nivel de SS" in {

        val guerrerosCargados = CargarKi(goku,boo)
        val energiaCargada = guerrerosCargados._1.energia

        energiaCargada shouldBe (goku.energia + 1 + 200)
      }

    }
  }
}
