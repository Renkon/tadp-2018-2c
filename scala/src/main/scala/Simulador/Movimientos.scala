package Simulador

object MovimientosContainer
{
  type Movimiento = Guerrero => Guerrero

  //CargarKi :: Movimiento
  object CargarKi {
    def apply(atacante : Guerrero, atacado : Guerrero) : (Guerrero, Guerrero) = {
      atacante.raza match {
        case Saiyajin(_,_) => (atacante.aumentarEnergia(100), atacado)
        case Androide => (atacante, atacado)
        case _ => (atacante.aumentarEnergia(100), atacado)
      }
    }
  }



}

/*
type Movimiento = Guerrero => Guerrero

def DejarseFajar(guerrero: Guerrero) = {
}

def CargarKi(guerrero: Guerrero) = {
}
def UsarItem(guerrero: Guerrero) = {
}
def ComerseAlOponente(guerrero: Guerrero) = {
}
def ConvertirseEnMono(guerrero: Guerrero) = {
}
def ConvertirseEnSuperSaiyajin(guerrero: Guerrero) = {
}
def Fusion(guerrero: Guerrero) = {
}
def Magia(guerrero: Guerrero) = {
}

type Ataque = Guerrero => Guerrero

def MuchosGolpesNinja(guerrero: Guerrero) = {
}
def Explotar(guerrero: Guerrero) = {
}
def Onda(guerrero: Guerrero) = {
}
def Genkidama(guerrero: Guerrero) = {
}
*/