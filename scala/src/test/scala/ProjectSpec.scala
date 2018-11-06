package Simulador

import org.scalatest.{FreeSpec, Matchers}

class ProjectSpec extends FreeSpec with Matchers {

  "DragonBall" - {

    "Tests de instanciacion" - {

      "Instancia de un guerrero con un nombre conocido y de raza Saiyajin" in {
        val goku = new Guerrero(name = "Kakaroto", raza = Saiyajin(1, false), energia = 1000, energiaMaxima = 100000, items = List(SemillaErmitanio))
        goku.nombre shouldBe "Kakaroto"
      }

      "Instancia de un guerrero con un nombre conocido y de raza Humana" in {
        val krillin = new Guerrero(name = "Krillin", raza = Humano, energia = 1, energiaMaxima = 150, items = List(SemillaErmitanio))
        krillin.nombre shouldBe "Krillin"
      }

      "Instancia de un guerrero con un nombre conocido y de raza Namekusein" in {
        val picoro = new Guerrero(name = "Picoro", raza = Namekusein, energia = 2500, energiaMaxima = 10000, items = List(SemillaErmitanio))
        picoro.nombre shouldBe "Picoro"
        picoro.raza shouldBe Namekusein
      }

      "Instancia de un guerrero con un nombre conocido y de raza Androide" in {
        val viejito = new Guerrero(name = "20", raza = Androide, energia = 10, energiaMaxima = 1000, items = List(SemillaErmitanio))
        viejito.nombre shouldBe "20"
        viejito.energiaMaxima shouldBe 1000
      }

      "Instancia de un guerrero con un nombre conocido y de raza Monstruo" in {
        val boo = new Guerrero(name = "MajinBoo", raza = Monstruo, energia = 10, energiaMaxima = 1000, items = List(SemillaErmitanio))
        boo.nombre shouldBe "MajinBoo"
        boo.raza shouldBe Monstruo
      }
    }

  }
}
