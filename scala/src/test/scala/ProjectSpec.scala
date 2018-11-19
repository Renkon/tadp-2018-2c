import org.scalatest.{FreeSpec, Matchers}
import dragonBall._

class ProjectSpec extends FreeSpec with Matchers {
  "dragonBall tests" - {

    "Construccion instancias test" - {

      "Cuando queres instnciar un guerrero sin nombre debe fallar" in {
        intercept[IllegalArgumentException] {
          Guerrero(nombre = "", energia = 40, raza = Saiyajin())
        }
        assertTypeError("El guerrero debe poseer un nombre")
      }

      "Cuando queres instnciar un guerrero con energia negativa debe fallar" in {
        intercept[IllegalArgumentException] {
          Guerrero(nombre = "goku", energia = -5, raza = Saiyajin())
        }
        assertTypeError("La energia del guerrero no puede ser un numero negativo")
      }

      "Cuando queres instnciar un guerrero con un numero de fajadas negativo nombre debe fallar" in {
        intercept[IllegalArgumentException] {
          Guerrero(nombre = "goku", energia = 50, raza = Saiyajin(), roundsQueSeDejoFajar = -1)
        }
        assertTypeError("El numero de veces que se fajo al guerrero no puede ser un nuemero negativo")
      }
    }

    "CargarKi test" - {

      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin())
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide())
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())

      "cuando un guerrero saiyajin con 40 de energia inicial y nivelSS = 1 carga ki entonces su eneria es la original mas 150, y el oponente no debe verse afetado" in {
        val (gokuConMasKi, yamchaIgual) = CargarKi(goku, yamcha)
        gokuConMasKi.energia shouldBe (goku.energia + 150)
        yamchaIgual shouldBe yamcha
      }

      "cuando un androide intenta cargarKi su energia permanece igual, y el oponente debe permanecer igual" in {
        val (androideConMasKi, yamchaIgual) = CargarKi(androide17, yamcha)
        androideConMasKi.energia shouldBe androide17.energia
        yamchaIgual shouldBe yamcha
      }

      "cuando un humano intenta cargarKi su energia aumenta en 100, y el oponente debe permanecer igual" in {
        val (yamchaConMasKi, gokuIgual) = CargarKi(yamcha, goku)
        yamchaConMasKi.energia shouldBe (yamcha.energia + 100)
        gokuIgual shouldBe goku
      }
    }

    "UsarItem test" - {
      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(), items = List(SemillaDelHermitanio))
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), items = List(SemillaDelHermitanio))
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())
      val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano())

      // Semilla del hermitanio
      "cuando un guerrero saiyajin con 50 puntos menos que su maximo se come una semilla del hermitanio, su valor de energia se restaura al maximo y pierde la semilla, y su oponente queda igual" in {
        val gokuDaniado = goku.aumentarEnergia(goku.raza.energiaMaxima - goku.energia).disminuirEnergia(50)
        val (gokuRecuperado, yamchaIgual) = UsarItem(SemillaDelHermitanio)(gokuDaniado, yamcha)
        gokuRecuperado.energia shouldBe goku.raza.energiaMaxima
        gokuRecuperado.items should not contain SemillaDelHermitanio
        yamchaIgual shouldBe yamcha
      }

      "cuando un androide come una semilla del hermitanio, su energia se restaura al maximo" in { // el enunciado dice cualquier guerrero
        val (androide17Recuperado, yamchaIgual) = UsarItem(SemillaDelHermitanio)(androide17, yamcha)
        androide17Recuperado.energia shouldBe androide17.raza.energiaMaxima
        yamchaIgual shouldBe yamcha
      }

      // ver este test
      "cuando alguien inconsciente quiere comer una semilla de hermitanio, todo bien" in {
        val (gokuRecuperado, androideIgual) = UsarItem(SemillaDelHermitanio)(goku, androide17)
        gokuRecuperado.energia shouldBe gokuRecuperado.raza.energiaMaxima
        androideIgual shouldBe androide17
      }

      // Item Generico
      "Cuando un humano quiere usar un item que no tiene, no sucede nada, y su oponente queda igual" in {
        val (yamchaIgual, satanIgual) = UsarItem(SemillaDelHermitanio)(yamcha, mrSatan)
        yamchaIgual shouldBe yamcha
        satanIgual shouldBe mrSatan
      }

      // Arma de fuego
      "Cuando un humano quiere usar un arma de fuego contra alguien pero no la tiene no pasa nada, y su oponente queda igual" in {
        val (yamchaIgual, pikoloIgual) = UsarItem(ArmaDeFuego)(yamcha, pikolo)
        yamchaIgual shouldBe yamcha
        pikoloIgual shouldBe pikolo
      }

      "Cuando un humano quiere usar un arma de fuego contra alguien pero no tiene municion, no pasa nada" in {
        val yamchaArmado = yamcha.agregarItem(ArmaDeFuego)
        val (yamchaIgual, pikoloIgual) = UsarItem(ArmaDeFuego)(yamchaArmado, pikolo)
        yamchaIgual shouldBe yamchaArmado
        pikoloIgual shouldBe pikolo
      }

      "Cuando un humano quiere usar un arma de fuego se gasta una municion" in {
        val municionOriginal = 10
        val yamchaArmadoConMunicion = yamcha.agregarItem(ArmaDeFuego).agregarItem(Municion(municionOriginal))
        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaDeFuego)(yamchaArmadoConMunicion, mrSatan)

        nuevoYamcha.municion().get.cantidadActual shouldBe (municionOriginal - 1)
      }

      "Cuando un humano quiere usar un arma de fuego y gasta su ultima municion deja de tener la munision entre sus items" in {
        val municionOriginal = 1
        val yamchaArmadoConMunicion = yamcha.agregarItem(ArmaDeFuego).agregarItem(Municion(municionOriginal))
        val cantidadOriginaDeItems = yamchaArmadoConMunicion.items.size

        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaDeFuego)(yamchaArmadoConMunicion, mrSatan)

        nuevoYamcha.items.size shouldBe (cantidadOriginaDeItems - 1)
      }

      "Cuando un humano usa un arma de fuego sobre otro, el oponente recibe el danio" in {
        val municionOriginal = 10
        val yamchaArmadoConMunicion = yamcha.agregarItem(ArmaDeFuego).agregarItem(Municion(municionOriginal))
        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaDeFuego)(yamchaArmadoConMunicion, mrSatan)

        nuevoSatan.energia shouldBe 0
      }

      // Arma Roma
      "cuando un humano quiere usar un arma roma contra un androide, ambos quedan igual" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val (nuevoYamcha, nuevoAndroide) = UsarItem(ArmaRoma)(yamchaArmado, androide17)
        nuevoYamcha shouldBe yamchaArmado
        nuevoAndroide shouldBe androide17
      }

      "cuando un humano quiere usar un arma roma contra un no-andoide con mas de 300 de energia, ambos quedan igual" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val gokuConMasEnergiaMaxima = goku.copy(energia = 500, raza = Saiyajin(fase = SSJFase3))
        val (nuevoYamcha, nuevoGoku) = UsarItem(ArmaRoma)(yamchaArmado, gokuConMasEnergiaMaxima)
        nuevoYamcha shouldBe yamchaArmado
        nuevoGoku shouldBe gokuConMasEnergiaMaxima
      }

      "cuando un humano quiere usar un arma roma contra un no-androide con menos de 300 de energia, el oponente queda inconsciente" in {
        val yamchaArmado = yamcha.agregarItem(ArmaRoma)
        val (nuevoYamcha, nuevoSatan) = UsarItem(ArmaRoma)(yamchaArmado, mrSatan)
        nuevoYamcha shouldBe yamchaArmado
        nuevoSatan.estado shouldBe Inconsciente
      }

      // Arma Filosa
      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un humano, el humano reduce su energia 1 punto por cada cien de energia del namek" in {
        val pikoloCon330ConNavaja = pikolo.copy(energia = 330, raza = Namekusein(), items = List(ArmaFilosa))
        val (nuevoPikolo, nuevoSatan) = UsarItem(ArmaFilosa)(pikoloCon330ConNavaja, mrSatan)
        nuevoPikolo shouldBe pikoloCon330ConNavaja
        nuevoSatan.energia shouldBe (mrSatan.energia - 3)
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin CON COLA, el saiyajin queda sin cola, con la misma fase, y en 1 de energia" in {
        val pikoloCon330ConNavaja = pikolo.copy(energia = 330, raza = Namekusein(), items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(SSJFase1, tieneCola = true))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon330ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe pikoloCon330ConNavaja
        nuevoGoku.energia shouldBe 1
        nuevoGoku.raza match {
          case raza: Saiyajin => (raza.tieneCola, raza.fase) shouldBe ((false, SSJFase1))
          case _ => // para que no salte un warning
        }
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin CON COLA y MONO, el saiyajin queda sin cola, con fase normal, y en 1 de energia" in {
        val pikoloCon330ConNavaja = pikolo.copy(energia = 330, raza = Namekusein(), items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(fase = Mono, tieneCola = true))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon330ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe pikoloCon330ConNavaja
        nuevoGoku.energia shouldBe 1
        nuevoGoku.raza match {
          case raza: Saiyajin => (raza.tieneCola, raza.fase) shouldBe ((false, Normal))
          case _ => // para que no salte un warning
        }
      }

      "cuando un namekusein con mucha energia quiere usar un arma filosa contra un saiyajin SIN COLA y cualquier fase, el saiyajin queda sin cola, con la misma fase, y en 1 de energia" in {
        val pikoloCon330ConNavaja = pikolo.copy(energia = 330, raza = Namekusein(), items = List(ArmaFilosa))
        val gokuConColaFaseSS1 = goku.copy(raza = Saiyajin(fase = SSJFase2, tieneCola = false))
        val (nuevoPikolo, nuevoGoku) = UsarItem(ArmaFilosa)(pikoloCon330ConNavaja, gokuConColaFaseSS1)
        nuevoPikolo shouldBe pikoloCon330ConNavaja
        nuevoGoku.energia shouldBe (gokuConColaFaseSS1.energia - 3)
        nuevoGoku.raza match {
          case raza: Saiyajin => (raza.tieneCola, raza.fase) shouldBe ((false, SSJFase2))
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
        androide17.estado should not be Muerto
        val movimientosAndroide = androide17.movimientos
        val (nuevoCell, nuevoAndroide) = ComerseAlOponente(cell, androide17)
        nuevoCell.movimientos should contain theSameElementsAs movimientosAndroide
        nuevoAndroide.estado shouldBe Muerto
      }

      "cuando cell quiere absorver al androide17 y a la androide18, aprende sus movimientos y ambos andoides quedan muertos" in {
        androide17.estado should not be Muerto
        androide18.estado should not be Muerto
        val movimientosAndroide17 = androide17.movimientos
        val movimientosAndroide18 = androide18.movimientos
        val (cellCon17Absorbido, nuevoAndroide17) = ComerseAlOponente(cell, androide17)
        cellCon17Absorbido.movimientos should contain theSameElementsAs movimientosAndroide17
        nuevoAndroide17.estado shouldBe Muerto
        val (cellCon17y18Absorbidos, nuevoAndroide18) = ComerseAlOponente(cellCon17Absorbido, androide18)
        cellCon17y18Absorbidos.movimientos should contain theSameElementsAs (movimientosAndroide17 ::: movimientosAndroide18) // joya, el orden de la concatencion no importa, busca por elementos
        nuevoAndroide18.estado shouldBe Muerto
      }

      "cuando cell quiere absorver a mrSatan, pasa verguenza" in {
        val estadoSatan = mrSatan.estado
        val (nuevoCell, nuevoSatan) = ComerseAlOponente(cell, mrSatan)
        nuevoCell.movimientos shouldBe empty
        nuevoSatan.estado shouldBe estadoSatan
      }

      "cuando majinBuu quiere absorber al androide17 aprende sus movimientos y este queda muerto" in {
        androide17.estado should not be Muerto
        val movimientosAndroide = androide17.movimientos
        val (nuevoManinBuu, nuevoAndroide) = ComerseAlOponente(majinBuu, androide17)
        nuevoManinBuu.movimientos should contain theSameElementsAs movimientosAndroide
        nuevoAndroide.estado shouldBe Muerto
      }

      "cuando majinBuu quiere absorber al androide17 aprende sus movimientos y este queda muerto, luego absorve a mrSatan y solo recuerda los movimientos de satan, y el mismo muere" in {
        androide17.estado should not be Muerto
        val movimientosAndroide = androide17.movimientos
        val (majinBuuCon17Absorbido, nuevoAndroide) = ComerseAlOponente(majinBuu, androide17)
        majinBuuCon17Absorbido.movimientos should contain theSameElementsAs movimientosAndroide
        nuevoAndroide.estado shouldBe Muerto

        val movimientosSatan = mrSatan.movimientos
        val (majinBuuConSatanAbsorbido, nuevoSatan) = ComerseAlOponente(majinBuuCon17Absorbido, mrSatan)
        majinBuuConSatanAbsorbido.movimientos should contain theSameElementsAs movimientosSatan
        nuevoSatan.estado shouldBe Muerto
      }

      "majinBuu no puede absorver a goku porque tiene mas ki que el" in {
        val goku = Guerrero(nombre = "goku", energia = 60, raza = Saiyajin(), movimientos = List(CargarKi))
        val (majinBuuIgual, gokuIgual) = ComerseAlOponente(majinBuu, goku)
        majinBuuIgual shouldBe majinBuu
        gokuIgual shouldBe goku
      }

      "cell no puede absorver a superAndroide17 porque tiene mas ki que el" in {
        val superAndroide17 = Guerrero(nombre = "superAndroide17", energia = 70, raza = Androide(), movimientos = List(CargarKi))
        val (cellIgual, super17Igual) = ComerseAlOponente(cell, superAndroide17)
        cellIgual shouldBe cell
        super17Igual shouldBe superAndroide17
      }
    }

    "ConvertirseEnMono test" - {
      val gohan = Guerrero(nombre = "gohan", energia = 50, raza = Saiyajin(tieneCola = false))
      val gokuGT = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(tieneCola = true))


      "cuando gokuGT quiere transformarse en mono no puede porque no tiene la foto de la luna" in {
        val (gokuIgual, gohanIgual) = ConvertirseEnMono(gokuGT, gohan)
        gokuIgual shouldBe gokuGT
        gohanIgual shouldBe gohan
      }

      "cuando gokuGT quiere transformarse en mono, y tiene la foto de la luna, todo bien" in {
        val gokuConFoto = gokuGT.copy(items = List(FotoDeLaLuna))
        val (gokuMono, gohanIgual) = ConvertirseEnMono(gokuConFoto, gohan)
        gokuMono.raza match {
          case raza: Saiyajin => (gokuMono.energia, raza.fase, raza.energiaMaxima) shouldBe(raza.energiaMaxima, Mono, gokuGT.raza.energiaMaxima * 3)
          case _ =>
        }
        gohanIgual shouldBe gohan
      }

      "cuando gohan quiere transformarse en mono, y tiene la foto de la luna, no puede porque no tiene cola" in {
        val gohanConFoto = gohan.copy(items = List(FotoDeLaLuna))
        val (gohanIgual, gokuIgual) = ConvertirseEnMono(gohanConFoto, gokuGT)
        gohanIgual.raza match {
          case raza: Saiyajin => (gohanIgual.energia, raza.fase, raza.energiaMaxima) shouldBe(gohan.energia, Normal, gohan.raza.energiaMaxima)
          case _ =>
        }
        gokuIgual shouldBe gokuGT
      }

    }

    /*
    * Convertirse en Super Saiyajin:
    * Cuando un Saiyajin se vuelve muy poderoso se convierte en Super Saiyajin,
    * estas transformaciones son acumulables
    * (eso quiere decir que cuando un SS se vuelve muy fuerte se puede convertir en SS nivel 2,
    * luego en SS nivel 3 y así...).
    * Para poder convertirse en SS o pasar al siguiente nivel, el ki del Saiyajin debe estar, por lo menos,
    * por la mitad de su máximo actual.
    * Al transformarse, el máximo ki del guerrero se multiplica por 5 por cada nivel de Super Saiyajin,
    * pero su ki no aumenta.
    * Si el guerrero queda inconsciente o se transforma en mono el estado de SS se pierde.
    * */

    "ConvertirseEnSuperSaiyajin test" - {
      val gohan = Guerrero(nombre = "gohan", energia = 50, raza = Saiyajin(tieneCola = false))
      val gokuGT = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin(tieneCola = true))

      "cuando un saiyajin no tiene la mitad del ki maximo de la fase a la que quiere llegar, se queda igual" in {
        val (gokuGTIgual, gohanIgual) = ConvertirseEnSuperSaiyajin(gokuGT, gohan)
        gokuGTIgual shouldBe gokuGT
        gohanIgual shouldBe gohan
      }

      "cuando un saiyajin normal se transforma a fase1, su energia maxima se quintuplica pero su ki queda igual" in {
        val energiaInicial = (gokuGT.raza.energiaMaxima / 2) + 10
        val gokuGTConMasKi = gokuGT.copy(energia = energiaInicial)
        val (gokuGTSSJFase1, gohanIgual) = ConvertirseEnSuperSaiyajin(gokuGTConMasKi, gohan)
        gokuGTSSJFase1.energia shouldBe energiaInicial
        gokuGTSSJFase1.raza match {
          case raza: Saiyajin => (raza.fase, raza.energiaMaxima) shouldBe(SSJFase1, SSJFase1.energiaMaxima)
          case _ =>
        }
        gohanIgual shouldBe gohan
      }

      "cuando un saiyajin normal se transforma a fase1 y luego a fase2, su energia maxima original se multiplica por 10, y su ki se mantiene" in {
        val energiaInicial = (gokuGT.raza.energiaMaxima / 2) + 10
        val gokuGTConMasKi = gokuGT.copy(energia = energiaInicial)
        val (gokuGTSSJFase1, gohanIgual) = ConvertirseEnSuperSaiyajin(gokuGTConMasKi, gohan)
        val energiaParaSSJ2 = energiaInicial * 5
        val gokuGTSSJ1ConMasKi = gokuGTSSJFase1.copy(energia = energiaParaSSJ2)
        val (gokuGTSSJFase2, gohanDeNuevoIgual) = ConvertirseEnSuperSaiyajin(gokuGTSSJ1ConMasKi, gohanIgual)
        gokuGTSSJFase2.energia shouldBe energiaParaSSJ2
        gokuGTSSJFase2.raza match {
          case raza: Saiyajin => (raza.fase, raza.energiaMaxima) shouldBe(SSJFase2, SSJFase2.energiaMaxima)
          case _ =>
        }
        gohanDeNuevoIgual shouldBe (gohan)
      }

      "cuando un saiyajin faseX se transforma en mono, su estado ssj se pierde, es decir, tu energia maxima pasa a ser la correspondiente al estado mono, y su ki aumenta al maximo" in {
        val gokuGTSSJ3 = gokuGT.copy(raza = Saiyajin(fase = SSJFase3, tieneCola = true), items = List(FotoDeLaLuna))
        gokuGTSSJ3.raza.energiaMaxima shouldBe SSJFase3.energiaMaxima
        val (gokuMono, gohanIgual) = ConvertirseEnMono(gokuGTSSJ3, gohan)
        gokuMono.raza.energiaMaxima shouldBe Mono.energiaMaxima
        gohanIgual shouldBe gohan
      }

      "cuando un saiyajin faseX queda inconsciente, su estado ssj se pierde, vuelve a Normal" in {
        val gokuGTSSJ3 = gokuGT.copy(raza = Saiyajin(fase = SSJFase3))
        val gokuGTInconsciente = gokuGTSSJ3.quedoInconsciente()
        gokuGTInconsciente.raza match {
          case raza: Saiyajin => raza.fase shouldBe Normal
          case _ =>
        }
      }
    }

    "FusionarseCon test" - {
      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin())
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide())
      val paiKuHan = Guerrero(nombre = "paiKuHan", energia = 40, raza = Namekusein())
      val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano())

      "goku no puede fusionarse con androide17 porque los androides no son fusionables" in {
        val (gokuIgual, paiKuHanIgual) = FusionarseCon(androide17)(goku, paiKuHan)
        gokuIgual shouldBe goku
        paiKuHanIgual shouldBe paiKuHan
      }

      "goku puede fusionarse con paikuhan y el resultado tiene la suma de sus energias y energiasMaximas" in {
        val (gokuHan, androideIgual) = FusionarseCon(paiKuHan)(goku, androide17)
        gokuHan.energia shouldBe (goku.energia + paiKuHan.energia)
        gokuHan.raza.energiaMaxima shouldBe (goku.raza.energiaMaxima + paiKuHan.raza.energiaMaxima)
        assert(gokuHan.raza.isInstanceOf[Fusionado])
      }

      "la fusion de goku y paikuhan al quedar inconsciente da como resultado a goku pero con la energia de la fusion" in {
        val (gokuHan, androideIgual) = FusionarseCon(paiKuHan)(goku, androide17)
        val gokuDeNuevo = gokuHan.disminuirEnergia(gokuHan.energia - 1).quedoInconsciente()
        gokuDeNuevo shouldBe goku.copy(energia = 1, estado = Inconsciente)
        androideIgual shouldBe androide17
      }

      "una fusion no puede volver a fusionarse" in {
        val (gokuHan, androideIgual) = FusionarseCon(paiKuHan)(goku, androide17)
        val (gokuHanDeNuevo, androideIgualDeNuevo) = FusionarseCon(mrSatan)(gokuHan, androide17)
        gokuHanDeNuevo shouldBe gokuHan
        androideIgualDeNuevo shouldBe androide17
      }

    }

    "Magia test" - {
      val goku = Guerrero(nombre = "goku", energia = 40, raza = Saiyajin())
      val gohan = Guerrero(nombre = "gohan", energia = 50, raza = Saiyajin())
      val krilin = Guerrero(nombre = "krilin", energia = 25, raza = Humano())
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())
      val majinBuu = Guerrero(nombre = "cell", energia = 50, raza = Monstruo(DigestionMajinBuu), movimientos = List())

      "un guerrero sin las esferas del dragon no puede realizar magia" in {
        val (gokuIgual, gohanIgual) = UsarMagia(efectoSobreAtacante = NoHacerNada, efectoSobreOponente = ObtenerSemillaDelErmitanio)(goku, gohan)
        gokuIgual shouldBe goku
        gohanIgual shouldBe gohan
      }

      "un guerrero con algunas esferas tampoco debe ser capaz de realizar magia" in {
        val gokuCon2Esferas = goku.copy(items = List(EsferasDelDragon.cuarta, EsferasDelDragon.quinta))
        val (gokuIgual, gohanIgual) = UsarMagia(efectoSobreAtacante = NoHacerNada, efectoSobreOponente = ObtenerSemillaDelErmitanio)(gokuCon2Esferas, gohan)
        gokuIgual shouldBe gokuCon2Esferas
        gohanIgual shouldBe gohan
      }

      "un guerrero con las 7 esferas si es capaz de realizar magia, y las pierde al realizarla" in {
        val gokuCon7Esferas = goku.copy(items = EsferasDelDragon.todasLasEsferas)
        gohan.items should not contain SemillaDelHermitanio
        val (gokuIgual, gohanConSemilla) = UsarMagia(efectoSobreAtacante = NoHacerNada, efectoSobreOponente = ObtenerSemillaDelErmitanio)(gokuCon7Esferas, gohan)
        gokuIgual shouldBe goku // pierde las esferas
        gohanConSemilla.items should contain(SemillaDelHermitanio)
      }

      "un namekusein debe ser capaz de realizar magia aunque no tenga las esferas" in {
        val krilinMuerto = krilin.disminuirEnergia(krilin.energia)
        krilinMuerto.estado shouldBe Muerto
        val (pikoloIgual, krilinConVida) = UsarMagia(efectoSobreAtacante = NoHacerNada, efectoSobreOponente = RevivirAKrilin)(pikolo, krilinMuerto)
        pikoloIgual shouldBe pikolo
        krilinConVida.estado shouldBe Ok
      }

      "un monstruo es capaz de realizar magia sin necesidad de tener las esferas" in {
        gohan.estado should not be Muerto
        val (majinBuuIgual, gohanMuerto) = UsarMagia(efectoSobreAtacante = NoHacerNada, efectoSobreOponente = ConvertirEnChocolate)(majinBuu, gohan)
        majinBuuIgual shouldBe majinBuu
        gohanMuerto.estado shouldBe Inconsciente
      }
    }

    "Ataques tests" - {
      val gohan = Guerrero(nombre = "gohan", energia = 50, raza = Saiyajin())
      val krilin = Guerrero(nombre = "krilin", energia = 25, raza = Humano())
      val androide17 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide())

      // MuchosGolpesNinja
      "si un humano ataca con golpes ninja a un androide, el primero sufre 10 de daño" in {
        val (krilinDaniado, androideIgual) = MuchosGolpesNinja(krilin, androide17)
        krilinDaniado.energia shouldBe (krilin.energia - 10)
        androideIgual shouldBe androide17
      }

      "al intercambiar golpes, krilin disminuye su energia en 20 porque gohan tiene mas ki" in {
        val (krilinDaniado, gohanIgual) = MuchosGolpesNinja(krilin, gohan)
        krilinDaniado.energia shouldBe (krilin.energia - 20)
        gohanIgual shouldBe gohan
      }

      //Explotar
      val gohanAlMaximo = gohan.aumentarEnergia(gohan.raza.energiaMaxima - gohan.energia)
      val pikolo = Guerrero(nombre = "pikolo", energia = 35, raza = Namekusein())
      val cell = Guerrero(nombre = "cell", energia = 50, raza = Monstruo(DigestionCell), movimientos = List())

      "gohan no puede explotar porque es humano" in {
        val (gohanIgual, pikoloIgual) = Explotar(gohanAlMaximo, pikolo)
        gohanIgual shouldBe gohanAlMaximo
        pikoloIgual shouldBe pikolo
      }

      "si androide17 explota, gohan recibe un daño proporcional al triple de la bateria del androide, y este muere" in {
        val (androideMuerto, gohanConDanio) = Explotar(androide17, gohanAlMaximo)
        gohanConDanio.energia shouldBe 0.max(gohanAlMaximo.energia - androide17.energia * 3)
        androideMuerto.energia shouldBe 0
        androideMuerto.estado shouldBe Muerto
      }

      "si androide17 explota, aunque la energia de pikolo es menor al danio, este queda como mucho en 1 por su elasticidad, y el androide muere" in {
        val (androideMuerto, pikoloConDanio) = Explotar(androide17, pikolo)
        pikoloConDanio.energia shouldBe 1
        pikoloConDanio.estado should not be Muerto
      }

      "si cell explota, le produce un danio proporcionar al doble de su energia a gohan, y muere" in {
        val (cellMuerto, gohanConDanio) = Explotar(cell, gohanAlMaximo)
        gohanConDanio.energia shouldBe 0.max(gohanAlMaximo.energia - cell.energia * 2)
        cellMuerto.energia shouldBe 0
        cellMuerto.estado shouldBe Muerto
      }

      // Ataques de Energia : Ondas y Genkidama
      val goku = Guerrero(nombre = "goku", energia = 100, raza = Saiyajin())
      val vegeta = Guerrero(nombre = "vegeta", energia = 100, raza = Saiyajin())
      val yamcha = Guerrero(nombre = "yamcha", energia = 25, raza = Humano())
      val majinBuu = Guerrero(nombre = "majinBuu", energia = 200, raza = Monstruo(DigestionMajinBuu))

      "yamcha no puede realizar el kamehameha porque no tiene la suficiente energia para ello" in {
        val (yamchaIgual, majinBuuIgual) = Kamehameha(yamcha, majinBuu)
        yamchaIgual shouldBe yamcha
        majinBuuIgual shouldBe majinBuu
      }

      "goku realiza el kamehameha contra vegeta y el danio que este sufre es el doble del requerido para el ataque" in {
        val (gokuConMenosEnergia, vegetaDaniado) = Kamehameha(goku, vegeta)
        gokuConMenosEnergia.energia shouldBe (goku.energia - Kamehameha.energiaDelAtaquePara(goku))
        vegetaDaniado.energia shouldBe 0.max(vegeta.energia - (Kamehameha.energiaDelAtaquePara(goku) * 2))
      }

      "gohan realiza el kamehameha contra cell y esto le produce un danio equivalente a la mitad de la energia necesaria para realizarlo, porque es un monstruo" in {
        val (gohanConMenosEnergia, cellDaniado) = Kamehameha(gohanAlMaximo, cell)
        gohanConMenosEnergia.energia shouldBe (gohanAlMaximo.energia - Kamehameha.energiaDelAtaquePara(gohanAlMaximo))
        cellDaniado.energia shouldBe (cell.energia - Kamehameha.energiaDelAtaquePara(gohanAlMaximo) / 2)
      }

      "goku no puede realizar la Genkidama si nunca se dejo fajar" in {
        val (gokuIgual, majinBuuIgual) = Genkidama(goku, majinBuu)
        gokuIgual shouldBe goku
        majinBuuIgual shouldBe majinBuu
      }

      "goku no puede realizar la Genkidama si se dejo fajar 2 veces pero despues realizo otro movimiento" in {
        val (gokuFajado2Veces, _) = (DejarseFajar andThen DejarseFajar) (goku, majinBuu)
        gokuFajado2Veces.roundsQueSeDejoFajar shouldBe 2
        val (gokuConMasKi, _) = gokuFajado2Veces.realizarMovimientoContra(CargarKi, majinBuu)
        gokuConMasKi.roundsQueSeDejoFajar shouldBe 0
        val (gokuIgual, majinBuuIgual) = Genkidama(goku, majinBuu)
        gokuIgual shouldBe gokuConMasKi
        majinBuuIgual shouldBe majinBuu
      }

      "goku no puede realizar la Genkidama si se dejo fajar 2 veces pero luego queda inconsciente" in {
        val (gokuFajado2Veces, _) = (DejarseFajar andThen DejarseFajar) (goku, majinBuu)
        val gokuInconsciente = gokuFajado2Veces.quedoInconsciente()
        gokuInconsciente.roundsQueSeDejoFajar shouldBe 0
      }

      "goku no puede realizar la Genkidama si se dejo fajar 2 veces pero luego queda muerto" in {
        val (gokuFajado2Veces, _) = (DejarseFajar andThen DejarseFajar) (goku, majinBuu)
        val gokuInconsciente = gokuFajado2Veces.morir()
        gokuInconsciente.roundsQueSeDejoFajar shouldBe 0
      }

      "goku puede realizar la Genkidama si se dejo fajar 3 veces y esto le produce un danio de 1000 a majinBuu, osea, lo mata. Y la energia de goku queda igual porque usa energia externa" in {
        val (gokuFajado3Veces, _) = (DejarseFajar andThen DejarseFajar andThen DejarseFajar) (goku, majinBuu)
        gokuFajado3Veces.roundsQueSeDejoFajar shouldBe 3
        val (gokuConLaMismaEnergiaInicial, majinBuuMuerto) = Genkidama(gokuFajado3Veces, majinBuu)
        gokuConLaMismaEnergiaInicial.energia shouldBe gokuFajado3Veces.energia
        gokuConLaMismaEnergiaInicial.roundsQueSeDejoFajar shouldBe 0
        majinBuuMuerto.energia shouldBe 0
        majinBuuMuerto.estado shouldBe Muerto
      }
    }
  }

  "Punto 1 - mejorMovimientoContra" - {
    val gokuSSJ1 = Guerrero(nombre = "goku", energia = 100, raza = Saiyajin(fase = SSJFase1), movimientos = List(AtacarCon(Genkidama), AtacarCon(Kamehameha), UsarItem(ArmaFilosa)), items = List(ArmaFilosa))
    val vegetaSSJ1 = Guerrero(nombre = "vegeta", energia = 100, raza = Saiyajin(fase = SSJFase1))
    val krilin = Guerrero(nombre = "krilin", energia = 25, raza = Humano(), movimientos = List(CargarKi, UsarItem(ArmaRoma)), items = List(ArmaRoma))
    val androide18 = Guerrero(nombre = "androide 17", energia = 40, raza = Androide(), movimientos = List(Explotar, AtacarCon(MuchosGolpesNinja)))
    val majinBuu = Guerrero(nombre = "majinBuu", energia = 200, raza = Monstruo(DigestionMajinBuu), movimientos = List(UsarMagia(NoHacerNada, ConvertirEnChocolate), ComerseAlOponente, AtacarCon(MuchosGolpesNinja)))
    val mrSatan = Guerrero(nombre = "mr satan", energia = 5, raza = Humano(), movimientos = List(UsarItem(ArmaDeFuego), AtacarCon(MuchosGolpesNinja)), items = List(ArmaDeFuego, Municion(1)))

    "vegetaSSJ1 no tiene ningun movimiento, el resultado es None" in {
      vegetaSSJ1.movimientoMasEfectivoContra(gokuSSJ1, LoHaceBosta) shouldBe None
    }

    // Criterio : LoHaceBosta
    "el mejor movimiento de goku para 'hacer bosta' a vegeta es el kamehameha" in {
      gokuSSJ1.movimientoMasEfectivoContra(vegetaSSJ1, LoHaceBosta) shouldBe Some(AtacarCon(Kamehameha))
    }

    "el mejor movimiento de goku para 'hacer bosta' a vegeta es la Genkidama, si se dejo fajar tres rounds" in {
      val (gokuFajado, vegetaIgual) = (DejarseFajar andThen DejarseFajar andThen DejarseFajar) (gokuSSJ1, vegetaSSJ1)
      gokuFajado.roundsQueSeDejoFajar shouldBe 3
      gokuFajado.movimientoMasEfectivoContra(vegetaIgual, LoHaceBosta) shouldBe Some(AtacarCon(Genkidama))
    }

    "ninguno de los movimientos de krilin es efectivo si quiere 'hacer bosta' a androide18" in {
      krilin.movimientoMasEfectivoContra(androide18, LoHaceBosta) shouldBe None
    }

    //"el movimiento mas efectivo de majinBuu contra goku para 'hacerlo bosta' es ComerseAlOponente" in {
    //  majinBuu.movimientoMasEfectivoContra(gokuSSJ1, LoHaceBosta) shouldBe(Some(ComerseAlOponente))
    //} lo comente porque esta medio al dope

    // Criterio : DisfrutarCombate
    "el ataque de androide18 que mas la hace 'disfrutar el combate' contra krilin es AtacarCon(MuchosGolpesNinja)" in {
      androide18.movimientoMasEfectivoContra(krilin, DisfrutarCombate) shouldBe Some(AtacarCon(MuchosGolpesNinja))
    }

    // Criterio : Tacanio
    "mrSatan prefiere a krilin con golpes ninja que usar su arma de fuego porque es tacanio" in {
      mrSatan.movimientoMasEfectivoContra(krilin, Tacanio) shouldBe Some(AtacarCon(MuchosGolpesNinja))
    }

    // Criterio : Supervivencia
    "krilin puede conformarse con cualquier ataque que no lo mate (o sea, no puede realizar el Kiensan)" in {
      val krilinConDosAtaques = krilin.copy(energia = Kienzan.energiaDelAtaquePara(krilin), movimientos = List(AtacarCon(Kienzan), CargarKi))
      krilinConDosAtaques.movimientoMasEfectivoContra(androide18, Supervivencia) shouldBe Some(CargarKi)
    }
  }

  "Punto 2 - pelearRound" - {
    val gokuSSJ1 = Guerrero(nombre = "goku", energia = 100, raza = Saiyajin(fase = SSJFase1), movimientos = List(AtacarCon(Genkidama), AtacarCon(Kamehameha), UsarItem(ArmaFilosa)), items = List(ArmaFilosa))
    val majinBuu = Guerrero(nombre = "majinBuu", energia = 200, raza = Monstruo(DigestionMajinBuu), movimientos = List(UsarMagia(NoHacerNada, ConvertirEnChocolate), ComerseAlOponente, AtacarCon(MuchosGolpesNinja)))
    val krilin = Guerrero(nombre = "krilin", energia = 85, raza = Humano(), movimientos = List(AtacarCon(Kienzan), CargarKi, UsarItem(ArmaRoma)), items = List(ArmaRoma))
    val androide18 = Guerrero(nombre = "androide 17", energia = 150, raza = Androide(), movimientos = List(Explotar, AtacarCon(MuchosGolpesNinja)))
    val yamcha = Guerrero(nombre = "yamcha", energia = 70, raza = Humano(), movimientos = List())

    "gokuSSJ1 pelea un round contra majinBuu y el resultado final es que goku termina muerto y majinBuu se lo comio" in {
      val resultadoDeRound = gokuSSJ1.pelearUnRound(AtacarCon(Kamehameha), majinBuu)
      resultadoDeRound.movimientoContraataqueOponente shouldBe Some(ComerseAlOponente)
      resultadoDeRound.estadoFinalAtacante.energia shouldBe 0
      resultadoDeRound.estadoFinalAtacante.estado shouldBe Muerto
      resultadoDeRound.estadoFinalOponente.energia shouldBe (majinBuu.energia - Kamehameha.energiaDelAtaquePara(gokuSSJ1) / 2)
      resultadoDeRound.estadoFinalOponente.movimientos should contain theSameElementsAs gokuSSJ1.movimientos
    }

    /* Caminito de este test :
     * Luego del kamehameha, gokuSSJ1 queda con la misma energia, y como majinBuu es un monstruo le causa 40 de daño, quedando con energia 160
     * luego majinBuu va a contraatacar con el ataque que mayor diferencia de ki le provoque. entre sus ataques estan:
     * . UsarMagia(NoHacerNada, ConvertirEnChocolate) que mantendria la diferencia de ki igual porque solo dejaria inconsciente a goku
     * . ComerseAlOponente, lo cual puede hacer porque tiene mas energia que goku, y si hace esto lo mata
     * . AtacarCon(MuchosGolpesNinja) en cuyo caso le sacaria 20 puntos nada mas mas
     * Por lo tanto va a querer comerselo y el resultado final deberia ser un gokuSSJ1 muerto y con energia 0
     * y un majinBuu que ahora tiene todos los movimientos de goku, y la misma energia y estado que tenia luego de recibir el primer ataque.
    * */

    "krilin ataca a androide18 con Kienzan, esta responde con muchos golpes ninja y queda con ventaja de energia" in {
      val resultadoDeRound = krilin.pelearUnRound(AtacarCon(Kienzan), androide18)
      resultadoDeRound.movimientoContraataqueOponente shouldBe Some(AtacarCon(MuchosGolpesNinja))
      resultadoDeRound.estadoFinalAtacante.energia shouldBe 0.max(krilin.energia - Kienzan.energiaDelAtaquePara(krilin) - 20)
      resultadoDeRound.estadoFinalOponente.energia shouldBe (androide18.energia + Kienzan.energiaDelAtaquePara(krilin) * 2)
    }
    /* Caminito de este test:
   * krilin realiza el kienzan contra androide18, luego de esto androide18 queda con mayor energia porque lo absorbe
   * quedando con 270 (daño del kiensan * 2). Ahora el androide18 tiene 2 posibilidades :
   * . Explotar, que los mata a ambos
   * . AtacarCon(MuchosGolpesNinja), que disminuye la energia de krilin en 20
   * Finalmente elije AtacarCon(MuchosGolpesNinja) porque la diferencia de energia obtenida es mayor
   * */

    "androide18 ataca a yamcha con muchos golpes ninja, lo que le disminuye 20 de energia, y luego el no puede contraatacar porque no tiene ningun movimiento " in {
      val resultadoDeRound = androide18.pelearUnRound(AtacarCon(MuchosGolpesNinja), yamcha)
      resultadoDeRound.movimientoContraataqueOponente shouldBe None
      resultadoDeRound.estadoFinalOponente.energia shouldBe yamcha.copy().disminuirEnergia(20).energia
      resultadoDeRound.estadoFinalAtacante shouldBe androide18
    }
  }

  "Punto 3 - planDeAtaqueContra" - {
    val yajirobe = Guerrero(nombre = "yajirobe", energia = Humano().energiaMaxima, raza = Humano(), items = List(ArmaFilosa, SemillaDelHermitanio), movimientos = List(UsarItem(ArmaFilosa), UsarItem(SemillaDelHermitanio)))
    val cell = Guerrero(nombre = "cell", energia = 160, raza = Monstruo(DigestionCell), movimientos = List(AtacarCon(Kamehameha)))
    val majinBuu = Guerrero(nombre = "majinBuu", energia = 200, raza = Monstruo(DigestionMajinBuu), movimientos = List(UsarMagia(NoHacerNada, ConvertirEnChocolate), ComerseAlOponente, AtacarCon(MuchosGolpesNinja)))
    val goku = Guerrero(nombre = "goku", energia = 200, raza = Saiyajin(), movimientos = List(AtacarCon(Kamehameha)))

    "plan de ataque de yajirobe" in {
      yajirobe.planDeAtaqueContra(cell, LoDejaConMayorVentajaEnKi, 2) shouldBe Some(List(UsarItem(ArmaFilosa), UsarItem(SemillaDelHermitanio)))
    }

    "dado que goku puede pelear un round contra majinBuu lanzandole un kamehameha, pero luego de ese round majinBuu lo mata, entonces en el segundo no tiene movimientos posibles, por lo tanto no se retorna un plan de ataque incompleto" in {
      goku.planDeAtaqueContra(majinBuu, LoHaceBosta, 2) shouldBe None
    }
  }

  "Punto 4 - pelearContra" - {
    val goku = Guerrero(nombre = "goku", energia = Saiyajin().energiaMaxima, raza = Saiyajin(), movimientos = List(AtacarCon(Genkidama), AtacarCon(Kamehameha), AtacarCon(MuchosGolpesNinja)))
    val vegeta = Guerrero(nombre = "vegeta", energia = Saiyajin().energiaMaxima, raza = Saiyajin(), movimientos = List(AtacarCon(Finalflash), AtacarCon(MuchosGolpesNinja)))
    val yajirobe = Guerrero(nombre = "yajirobe", energia = Humano().energiaMaxima, raza = Humano(), items = List(ArmaFilosa, SemillaDelHermitanio), movimientos = List(UsarItem(ArmaFilosa), UsarItem(SemillaDelHermitanio)))
    val mrSatan = Guerrero(nombre = "mr satan", energia = Humano().energiaMaxima, raza = Humano(), movimientos = List(UsarItem(ArmaDeFuego), AtacarCon(MuchosGolpesNinja)), items = List(ArmaDeFuego, Municion(1)))

    //"si pasas un plan vacio no rompe" // TODO

    "goku vs vegeta: gana vegeta " in {
      // round1 : goku -> kamehameha (le saca 160 a vegeta y 80 a el), vegeta -> final flash (le saca 140 a goku y 70 a el) => (goku queda con 130, vegeta con 120)
      // round2 : goku -> muchos golpes ninja (le saca 20 a vegeta), vegeta -> final flash (le saca 70 a el )=> (goku queda con 0, vegeta con 30)

      goku.pelearContra(vegeta, List(AtacarCon(Kamehameha), AtacarCon(MuchosGolpesNinja))) match {
        case Ganador(guerrero) => (guerrero.nombre, guerrero.energia) shouldBe(vegeta.nombre, 30)
        case _ => fail("el ganador deberia ser vegeta")
      }
    }

    "goku vs vegeta: gana goku" in {
      //round 1 : goku -> kamehameha (le gasta 80 a el y 160 a vegeta), vegeta -> finalflash (le gasta 70 a el y 140 a goku) => (goku 130, vegeta 120)
      //round 2 : goku-> genkidama (mata a vegeta, se habia dejado fajar 3 veces asi que le saca 1000), vegeta -> nada => (goku 130, vegeta 0)

      goku.copy(roundsQueSeDejoFajar = 3).pelearContra(vegeta, List(AtacarCon(Kamehameha), AtacarCon(Genkidama))) match {
        case Ganador(guerrero) => (guerrero.nombre, guerrero.energia) shouldBe(goku.nombre, 130)
        case _ => fail("el ganador deberia ser goku")
      }
    }

    "mrSatan vs yajirobe: el combate no termina" in {
      //round 1 : satan -> arma de fuego (le saca 20), yajirobe -> arma filosa (le saca 2) => (mr satan 298, yajirobe 280)
      //round 2 : satan -> muchos golpes ninja, yajirobe -> semilla del ermitanio => (mrsatan 298, 300)
      //round 2 : satan -> muchos golpes ninja (lo afecta a el mismo, le saca 20), yajirobe -> arma filosa (le saca 3) => (mr satan 275, yajirobe 300)

      mrSatan.pelearContra(yajirobe, List(UsarItem(ArmaDeFuego), AtacarCon(MuchosGolpesNinja), AtacarCon(MuchosGolpesNinja))) match {
        case SigueElCombate(atacante, oponente) => {
          (atacante.nombre, atacante.energia) shouldBe(mrSatan.nombre, 275)
          (oponente.nombre, oponente.energia) shouldBe(yajirobe.nombre, 300)
        }
        case _ => fail("el combate deberia serguir")
      }
    }
  }

}

