import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

class ProjectSpec extends FreeSpec with Matchers {
  val goku = new Guerrero(nombre ="goku", energia = 40, raza = Saiyajin())

  "dragonBallTests" - {

    "guerrerosTest" - {
      "cuando un guerrero saiyajin descansa su energia disminuye en 2" in {
        val energiaOriginal : Int = goku.energia
        val gokuFajado = DejarseFajar(goku)
        gokuFajado.energia shouldBe(energiaOriginal - 2)
      }
    }
  }

}
