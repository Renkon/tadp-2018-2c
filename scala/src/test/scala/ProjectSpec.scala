import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

import scala.None

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBall tests" - {

    "CargarKi test" - {

      val goku = new Saiyajin(nombre ="goku", energia = 40, tieneCola = true, nivelSS = 0, energiaMaxima = 300)
      val androide17 = new Androide(nombre = "androide 17", energiaMaxima = 300)
      val yamcha = new Humano(nombre = "yamcha", energia = 25, energiaMaxima = 200)

      "cuando un guerrero saiyajin con 40 de energia inicial y nivelSS = 1 carga ki entonces su eneria es la original mas 150" in {
        val gokuConMasKi = (CargarKi(goku)(None))._1
        gokuConMasKi.energia shouldBe(goku.energia + 150)
      }

      "cuando un androide intenta cargarKi su energia permanece igual" in {
        val androideConMasKi = (CargarKi(androide17)(None))._1
        androideConMasKi.energia shouldBe(androide17.energia)
      }

      "cuando un humano intenta cargarKi su energia aumenta en 100" in {
        val yamchaConMasKi = (CargarKi(yamcha)(None))._1
        yamchaConMasKi.energia shouldBe(yamcha.energia + 100)
      }
    }

    "UsarItem test" - {
      val goku = new Saiyajin(nombre ="goku", energia = 40, tieneCola = true, nivelSS = 0, energiaMaxima = 300, items = List(SemillaDelHermitanio))
      val androide17 = new Androide(nombre = "androide 17", energiaMaxima = 300, items = List(SemillaDelHermitanio))
      val yamcha = new Humano(nombre = "yamcha", energia = 15, energiaMaxima = 200)
      val pikolo = new Namekusein(nombre = "pikolo", energia = 35, energiaMaxima = 200)
      val mrSatan = new Humano(nombre = "mr Satan", energia = 5, energiaMaxima = 200)

      "cuando un guerrero saiyajin con 50 puntos menos que su maximo se come una semilla del hermitanio, su valor de energia se restaura al maximo" in {
        val gokuDaniado = goku.aumentarEnergia(goku.energiaMaxima - goku.energia).disminuirEnergia(50)
        val gokuRecuperado = (UsarItem(SemillaDelHermitanio)(gokuDaniado)(None))._1 // FIXME por que tengo que anteponer dragonBall. ?
        gokuRecuperado.energia shouldBe(goku.energiaMaxima)
      }

      "cuando un androide come una semilla del hermitanio, su energia sigue siendo cero" in {
        val androide17Recuperado = (UsarItem(SemillaDelHermitanio)(androide17)(None))._1
        androide17Recuperado.energia shouldBe(0)
      }

      "cuando un humano quiere usar un item que no tiene, no sucede nada" in {
        val yamchaIgual = (UsarItem(SemillaDelHermitanio)(yamcha)(None))._1
        yamchaIgual shouldBe(yamcha)
      }

      "cuando un humano quiere usar un arma de fuego contra alguien pero no la tiene no pasa nada" in {
        val resultadoTiroteo = UsarItem(ArmaDeFuego)(yamcha)(Some(pikolo))
        resultadoTiroteo shouldBe(yamcha, Some(pikolo))
      }

      "cuando un humano quiere usar un arma de fuego contra alguien pero no tiene municion, no pasa nada" in {
        val yamchaArmado = yamcha.agregarItem(ArmaDeFuego)
        val resultadoTiroteo = UsarItem(ArmaDeFuego)(yamchaArmado)(Some(pikolo))
        resultadoTiroteo shouldBe(yamchaArmado, Some(pikolo))
      }

      "cuando un humano quiere usar un arma de fuego contra mr satan, este queda en cero y yamcha queda con 1 menos de municion" in {
        val municionOriginal = 10
        val yamchaArmadoConMunicion = yamcha.agregarItem(ArmaDeFuego).agregarItem(Municion(municionOriginal))
        val (nuevoYamcha, Some(nuevoSatan)) = UsarItem(ArmaDeFuego)(yamchaArmadoConMunicion)(Some(mrSatan))
        nuevoSatan.energia shouldBe(0)
        (nuevoYamcha.municiones().get).asInstanceOf[Municion].cantidadActual shouldBe(municionOriginal - 1) // FEO
      }
    }
  }
}
