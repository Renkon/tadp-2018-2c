package Simulador

import org.scalatest.{FreeSpec, Matchers}

class ProjectSpec extends FreeSpec with Matchers {

  "DragonBall" - {

    "Test de instanciacion" - {

      "Instancia de un guerrero con un nombre conocido y de raza Saiyajin" in {
        val goku = new Guerrero(name = "Kakaroto", raza = Saiyajin(1,true), energia = 1000, energiaMaxima = 100000, items = List(SemillaErmitanio))
        goku.nombre shouldBe "Kakaroto"
      }


      "Instancia de un guerrero con un nombre conocido y de raza Humana" in {
        val krillin = new Guerrero(name = "Krillin", raza = Humano, energia = 10, energiaMaxima = 1000, items = List(SemillaErmitanio))
        krillin.nombre shouldBe "Krillin"
      }
    }
  }


}
