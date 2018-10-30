import org.scalatest.{FreeSpec, Matchers}

class ProjectSpec extends FreeSpec with Matchers {

  "Este proyecto" - {

    "cuando está correctamente configurado" - {
      "debería resolver las dependencias y pasar este test" in {
        Prueba.materia shouldBe "tadp"
      }
    }
  }

  "DragonBall" - {

    "Un test de un guerrero" - {
      "Creo una nueva instania de guerrero" in {
        val goku = new Guerrero("Kakaroto", items = List(SemillaErmitanio), 10000)
        goku.name shouldBe "Kakaroto"
      }
    }
  }


}
