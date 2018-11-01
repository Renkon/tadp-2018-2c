package ar.com.tadp18c2.grupo1.guerrero.traits

import ar.com.tadp18c2.grupo1.guerrero.Guerrero

// Este trait va a ser usado por todos los guerreros que tengan ki
trait Ki extends Guerrero {

  val ki: Int
  val kiMáximo: Int
}
