package ar.com.tadp18c2.grupo1.guerrero.others

sealed trait EstadoCola
case object Normal extends EstadoCola
case object Cortada extends EstadoCola
