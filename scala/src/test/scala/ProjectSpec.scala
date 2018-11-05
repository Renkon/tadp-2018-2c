import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

import scala.None

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBall tests" - {

    "CargarKi test" - {

      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(nivelSS = 0,tieneCola = true))
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide())
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())

      "cuando un guerrero saiyajin con 40 de energia inicial y nivelSS = 1 carga ki entonces su eneria es la original mas 150, y el oponente no debe verse afetado" in {
        val (gokuConMasKi, yamchaIgual) = CargarKi(goku, yamcha)
        gokuConMasKi.energia shouldBe(goku.energia + 150)
        yamchaIgual shouldBe(yamcha)
      }

      "cuando un androide intenta cargarKi su energia permanece igual, y el oponente debe permanecer igual" in {
        val (androideConMasKi, yamchaIgual) = CargarKi(androide17, yamcha)
        androideConMasKi.energia shouldBe(androide17.energia)
        yamchaIgual shouldBe(yamcha)
      }

      "cuando un humano intenta cargarKi su energia aumenta en 100, y el oponente debe permanecer igual" in {
        val (yamchaConMasKi, gokuIgual) = CargarKi(yamcha, goku)
        yamchaConMasKi.energia shouldBe(yamcha.energia + 100)
        gokuIgual shouldBe(goku)
      }
    }

    "UsarItem test" - {
      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(nivelSS = 0,tieneCola = true), items = List(SemillaDelHermitanio))
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), items = List(SemillaDelHermitanio))
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Humano())
      val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano())

      "cuando un guerrero saiyajin con 50 puntos menos que su maximo se come una semilla del hermitanio, su valor de energia se restaura al maximo, y su oponente queda igual" in {
        val gokuDaniado = goku.aumentarEnergia(goku.raza.energiaMaxima - goku.energia).disminuirEnergia(50)
        val (gokuRecuperado, yamchaIgual) = UsarItem(SemillaDelHermitanio)(gokuDaniado, yamcha)
        gokuRecuperado.energia shouldBe(goku.raza.energiaMaxima)
        yamchaIgual shouldBe(yamcha)
      }

      "cuando un androide come una semilla del hermitanio, su energia sigue siendo cero, y su oponente queda igual" in {
        val (androide17Recuperado, yamchaIgual) = UsarItem(SemillaDelHermitanio)(androide17, yamcha)
        androide17Recuperado.energia shouldBe(0)
        yamchaIgual shouldBe(yamcha)
      }

      "cuando un humano quiere usar un item que no tiene, no sucede nada, y su oponente queda igual" in {
        val (yamchaIgual, satanIgual)= UsarItem(SemillaDelHermitanio)(yamcha, mrSatan)
        yamchaIgual shouldBe(yamcha)
        satanIgual shouldBe(mrSatan)
      }

      "cuando un humano quiere usar un arma de fuego contra alguien pero no la tiene no pasa nada, y su oponente queda igual" in {
        val (yamchaIgual, pikoloIgual) = UsarItem(ArmaDeFuego)(yamcha,pikolo)
        yamchaIgual shouldBe(yamcha)
        pikoloIgual shouldBe(pikolo)
      }

      "cuando un humano quiere usar un arma de fuego contra alguien pero no tiene municion, no pasa nada" in {
        val yamchaArmado = yamcha.agregarItem(ArmaDeFuego)
        val (yamchaIgual, pikoloIgual) = UsarItem(ArmaDeFuego)(yamchaArmado,pikolo)
        yamchaIgual shouldBe(yamchaArmado)
        pikoloIgual shouldBe(pikolo)
      }

      "cuando un humano quiere usar un arma de fuego contra mr satan, este queda en cero y yamcha queda con 1 menos de municion" in {
        val municionOriginal = 10
        val yamchaArmadoConMunicion = yamcha.agregarItem(ArmaDeFuego).agregarItem(Municion(municionOriginal))
        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaDeFuego)(yamchaArmadoConMunicion, mrSatan)
        nuevoSatan.energia shouldBe(0)
        (nuevoYamcha.municion().get).asInstanceOf[Municion].cantidadActual shouldBe(municionOriginal - 1) // FEO
      }
    }
  }
}
