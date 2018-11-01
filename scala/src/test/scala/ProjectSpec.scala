import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBallTests" - {

    "cargarKiTest" - {

      val goku = new Saiyajin(nombre ="goku", energia = 40, tieneCola = true, nivelSS = 0, energiaMaxima = 300)
      val androide17 = new Androide(nombre = "androide 17", energiaMaxima = 300)
      val yamcha = new Humano(nombre = "yamcha", energia = 25, energiaMaxima = 200)

      "cuando un guerrero saiyajin con 40 de energia inicial y nivelSS = 1 carga ki entonces su eneria es la original mas 150" in {
        val gokuConMasKi = CargarKi(goku)
        gokuConMasKi.energia shouldBe(goku.energia + 150)
      }

      "cuando un androide intenta cargarKi su energia permanece igual" in {
        val androideConMasKi = CargarKi(androide17)
        androideConMasKi.energia shouldBe(androide17.energia)
      }

      "cuando un humano intenta cargarKi su energia aumenta en 100" in {
        val yamchaConMasKi = CargarKi(yamcha)
        yamchaConMasKi.energia shouldBe(yamcha.energia + 100)
      }
    }
  }

}
