import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBallTests" - {

    "cargarKiTest" - {

      val goku = new Saiyajin(nombre ="goku", energia = 40, tieneCola = true, nivelSS = 0, energiaMaxima = 300)
      val androide17 = new Androide(nombre = "androide 17", energiaMaxima = 300)

      "cuando un guerrero saiyajin con 40 de energia inicial y nivelSS = 1 carga ki entonces su eneria es la original mas 150" in {
        val energiaOriginal : Int = goku.energia
        val gokuConMasKi = CargarKi(goku)
        gokuConMasKi.energia shouldBe(energiaOriginal + 150)
      }

      "cuando un androide intenta cargarKi su energia permanece igual" in {
        val androideConMasKi = CargarKi(androide17)
        androideConMasKi.energia shouldBe(androide17.energia)
      }
    }
  }

}
