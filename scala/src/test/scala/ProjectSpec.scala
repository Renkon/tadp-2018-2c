import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

import scala.None

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBall tests" - {

    "Construccion test" - {
      "cuando queres superar la energia maxima, deberia fallar" in {
        //val goku = Guerrero(nombre = "goku", energia = 1000, raza = Saiyajin(nivelSS = 0,tieneCola = true))

      }

    }

    "CargarKi test" - {

      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin())
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide())
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())

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
      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(), items = List(SemillaDelHermitanio))
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), items = List(SemillaDelHermitanio))
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())
      val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano())

      // Semilla del hermitanio
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

      "cuando alguien inconsciente quiere comer una semilla de hermitanio, todo bien" in {
        val (gokuRecuperado, androideIgual) = UsarItem(SemillaDelHermitanio) (goku, androide17)
        gokuRecuperado.energia shouldBe(gokuRecuperado.raza.energiaMaxima)
        androideIgual shouldBe(androide17)
      }

      // Item Generico
      "cuando un humano quiere usar un item que no tiene, no sucede nada, y su oponente queda igual" in {
        val (yamchaIgual, satanIgual)= UsarItem(SemillaDelHermitanio)(yamcha, mrSatan)
        yamchaIgual shouldBe(yamcha)
        satanIgual shouldBe(mrSatan)
      }

      // Arma de fuego
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

      // Arma Roma
      "cuando un humano quiere usar un arma roma contra un androide, ambos quedan igual" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val (nuevoYamcha, nuevoAndroide) = UsarItem(ArmaRoma)(yamchaArmado, androide17)
        nuevoYamcha shouldBe(yamchaArmado)
        nuevoAndroide shouldBe(androide17)
      }

      "cuando un humano quiere usar un arma roma contra un no-andoide con mas de 300 de energia, ambos quedan igual" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val razaModificada = Saiyajin()
        razaModificada.energiaMaxima = 1000
        val gokuConMasEnergiaMaxima = goku.copy(energia = 500, raza = razaModificada)
        val (nuevoYamcha, nuevoGoku) = UsarItem(ArmaRoma)(yamchaArmado, gokuConMasEnergiaMaxima)
        nuevoYamcha shouldBe(yamchaArmado)
        nuevoGoku shouldBe(gokuConMasEnergiaMaxima)
      }

      "cuando un humano quiere usar un arma roma contra un no-androide con menos de 300 de energia, el oponente queda inconsciente" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaRoma)(yamchaArmado, mrSatan)
        nuevoYamcha shouldBe(yamchaArmado)
        nuevoSatan.estado shouldBe(Inconsciente)
      }

      // Arma Filosa
      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un humano, el humano reduce su energia 1 punto por cada cien de energia del namek" in {
        val SuperNamekusein = Namekusein()
        SuperNamekusein.energiaMaxima = 400 // TODO hacer un metodo en el guerrero que esconda esto y que se concatene con el copy del guerrero
        val pikoloCon350ConNavaja = pikolo.copy(energia = 350, raza = SuperNamekusein, items = List(ArmaFilosa))
        val (nuevoPikolo, nuevoSatan) = UsarItem(ArmaFilosa)(pikoloCon350ConNavaja, mrSatan)
        nuevoPikolo shouldBe(pikoloCon350ConNavaja)
        nuevoSatan.energia shouldBe(mrSatan.energia - 3)
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin CON COLA, el saiyajin queda sin cola, con la misma fase, y en 1 de energia" in {
        val SuperNamekusein = Namekusein()
        SuperNamekusein.energiaMaxima = 400
        val pikoloCon350ConNavaja = pikolo.copy(energia = 350, raza = SuperNamekusein, items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(Fases.SSFase1, tieneCola = true))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon350ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe(pikoloCon350ConNavaja)
        nuevoGoku.energia shouldBe(1)
        nuevoGoku.raza match {
          case raza:Saiyajin => (raza.tieneCola, raza.nivelDeFase()) shouldBe((false, Fases.SSFase1))
          case _ => // para que no salte un warning
        }
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin CON COLA y MONO, el saiyajin queda sin cola, con fase normal, y en 1 de energia" in {
        val SuperNamekusein = Namekusein()
        SuperNamekusein.energiaMaxima = 400
        val pikoloCon350ConNavaja = pikolo.copy(energia = 350, raza = SuperNamekusein, items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(Fases.Mono, tieneCola = true))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon350ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe(pikoloCon350ConNavaja)
        nuevoGoku.energia shouldBe(1)
        nuevoGoku.raza match {
          case raza:Saiyajin => (raza.tieneCola, raza.nivelDeFase()) shouldBe((false,Fases.Normal ))
          case _ => // para que no salte un warning
        }
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin SIN COLA y cualquier fase, el saiyajin queda sin cola, con la misma fase, y en 1 de energia" in {
        val SuperNamekusein = Namekusein()
        SuperNamekusein.energiaMaxima = 400
        val pikoloCon350ConNavaja = pikolo.copy(energia = 350, raza = SuperNamekusein, items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(Fases.SSFase2, tieneCola = false))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon350ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe(pikoloCon350ConNavaja)
        nuevoGoku.energia shouldBe(gokuConColaFaseSS1.energia - 3)
        nuevoGoku.raza match {
          case raza:Saiyajin => (raza.tieneCola, raza.nivelDeFase()) shouldBe((false, Fases.SSFase2))
          case _ => // para que no salte un warning
        }
      }
    }

    "ComerseAlOponente test" - {
      val cell = Guerrero(nombre = "cell", energia = 50, raza = Monstruo(DigestionCell), movimientos = List())
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), movimientos = List(CargarKi, UsarItem(ArmaFilosa)))
      val androide18 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), items = List(Municion(2)), movimientos = List(CargarKi, UsarItem(ArmaDeFuego)))
      val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano(), movimientos = List(UsarItem(ArmaRoma)))
      val majinBuu = Guerrero(nombre = "cell", energia = 50, raza = Monstruo(DigestionMajinBuu), movimientos = List())


      "cuando cell quiere absorver al androide17, aprende sus movimientos y el andoide queda muerto" in {
        androide17.estado should not be (Muerto)
        val movimientosAndroide = androide17.movimientos
        val (nuevoCell, nuevoAndroide) = ComerseAlOponente(cell, androide17)
        nuevoCell.movimientos should contain theSameElementsAs (movimientosAndroide)
        nuevoAndroide.estado shouldBe(Muerto)
      }

      "cuando cell quiere absorver al androide17 y a la androide18, aprende sus movimientos y ambos andoides quedan muertos" in {
        androide17.estado should not be (Muerto)
        androide18.estado should not be (Muerto)
        val movimientosAndroide17 = androide17.movimientos
        val movimientosAndroide18 = androide18.movimientos
        val (cellCon17Absorbido, nuevoAndroide17) = ComerseAlOponente(cell, androide17)
        cellCon17Absorbido.movimientos should contain theSameElementsAs (movimientosAndroide17)
        nuevoAndroide17.estado shouldBe(Muerto)
        val (cellCon17y18Absorbidos, nuevoAndroide18) = ComerseAlOponente(cellCon17Absorbido, androide18)
        cellCon17y18Absorbidos.movimientos should contain theSameElementsAs ( movimientosAndroide17 ::: movimientosAndroide18) // joya, el orden de la concatencion no importa, busca por elementos
        nuevoAndroide18.estado shouldBe(Muerto)
      }

      "cuando cell quiere absorver a mrSatan, pasa verguenza" in {
        val estadoSatan = mrSatan.estado
        val (nuevoCell, nuevoSatan) = ComerseAlOponente(cell, mrSatan)
        nuevoCell.movimientos shouldBe empty
        nuevoSatan.estado shouldBe (estadoSatan)
      }

      "cuando majinBuu quiere absorber al androide17 aprende sus movimientos y este queda muerto" in {
        androide17.estado should not be (Muerto)
        val movimientosAndroide = androide17.movimientos
        val (nuevoManinBuu, nuevoAndroide) = ComerseAlOponente(majinBuu, androide17)
        nuevoManinBuu.movimientos should contain theSameElementsAs (movimientosAndroide)
        nuevoAndroide.estado shouldBe(Muerto)
      }

      "cuando majinBuu quiere absorber al androide17 aprende sus movimientos y este queda muerto, luego absorve a mrSatan y solo recuerda los movimientos de satan, y el mismo muere" in {
        androide17.estado should not be (Muerto)
        val movimientosAndroide = androide17.movimientos
        val (majinBuuCon17Absorbido, nuevoAndroide) = ComerseAlOponente(majinBuu, androide17)
        majinBuuCon17Absorbido.movimientos should contain theSameElementsAs (movimientosAndroide)
        nuevoAndroide.estado shouldBe(Muerto)

        val movimientosSatan = mrSatan.movimientos
        val (majinBuuConSatanAbsorbido, nuevoSatan) =  ComerseAlOponente(majinBuuCon17Absorbido, mrSatan)
        majinBuuConSatanAbsorbido.movimientos should contain theSameElementsAs (movimientosSatan)
        nuevoSatan.estado shouldBe(Muerto)
      }

      "majinBuu no puede absorver a goku porque tiene mas ki que el" in {
        val goku = Guerrero(nombre = "goku", energia = 60 , raza = Saiyajin(), movimientos = List(CargarKi))
        val (majinBuuIgual, gokuIgual) = ComerseAlOponente(majinBuu, goku)
        majinBuuIgual shouldBe(majinBuu)
        gokuIgual shouldBe(goku)
      }

      "cell no puede absorver a superAndroide17 porque tiene mas ki que el" in {
        val superAndroide17 = Guerrero(nombre = "superAndroide17", energia = 70 , raza = Androide(), movimientos = List(CargarKi))
        val (cellIgual, super17Igual) = ComerseAlOponente(cell, superAndroide17)
        cellIgual shouldBe(cell)
        super17Igual shouldBe(superAndroide17)
      }

    }


    "ConvertirseEnMono test" - {
      val gohan = Guerrero(nombre = "gohan", energia = 50, raza = Saiyajin(tieneCola = false))
      val gokuGT = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(tieneCola = true))


      "cuando gokuGT quiere transformarse en mono no puede porque no tiene la foto de la luna" in {
        val (gokuIgual, gohanIgual) = ConvertirseEnMono(gokuGT, gohan)
        gokuIgual shouldBe(gokuGT)
        gohanIgual shouldBe(gohan)
      }

      "cuando gokuGT quiere transformarse en mono, y tiene la foto de la luna, todo bien" in {
        val gokuConFoto = gokuGT.copy(items = List(FotoDeLaLuna))
        val (gokuMono, gohanIgual) = ConvertirseEnMono(gokuConFoto, gohan)
        gokuMono.raza match {
          case raza:Saiyajin => (gokuMono.energia, raza.nivelDeFase(), raza.energiaMaxima) shouldBe(raza.energiaMaxima, Fases.Mono, gokuGT.raza.energiaMaxima * 3)
          case _ =>
        }
        gohanIgual shouldBe(gohan)
      }

      "cuando gohan quiere transformarse en mono, y tiene la foto de la luna, no puede porque no tiene cola" in {
        val gohanConFoto = gohan.copy(items = List(FotoDeLaLuna))
        val (gohanIgual, gokuIgual) = ConvertirseEnMono(gohanConFoto, gokuGT)
        gohanIgual.raza match {
          case raza:Saiyajin => (gohanIgual.energia, raza.nivelDeFase(), raza.energiaMaxima) shouldBe(gohan.energia, Fases.Normal, gohan.raza.energiaMaxima)
          case _ =>
        }
        gokuIgual shouldBe(gokuGT)
      }

    }


  }
}
