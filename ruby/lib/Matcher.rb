
module ConcatenableOperations
  def and(first_matcher, *other_matchers)
    lambda { |gotten| self.call(gotten) && first_matcher.call(gotten) && other_matchers.all? {|matcher| matcher.call(gotten)}}
  end

  def or(first_matcher, *other_matchers)
    lambda {|gotten| self.call(gotten) || first_matcher.call(gotten) || other_matchers.any? {|matcher| matcher.call(gotten)}}
  end

  def not
    lambda {|gotten| !self.call(gotten)}
  end
end

#Defino el metodo call en los symbols
class Symbol
  def call(a)
    true
  end

  include ConcatenableOperations
  # TODO: ESTO NO ME GUSTA, pero si quiero lograr la interfaz :un_symbol.and(type(Class), etc...), medio como que por ahora no queda otra
end

#Defino los matchers primitivos
def val(expected)
  lambda {|gotten| gotten == expected}.extend(ConcatenableOperations)
end

def type(expected)
  lambda {|gotten| gotten.is_a?(expected)}.extend(ConcatenableOperations) # kind_of te retorna true si es una subclase de expected
end

def list(expected, with_size = true) # supuestamente esto lo hace opcional
  lambda {|gotten| gotten.is_a?(Array) && (with_size ? gotten.size == expected.size : true) && are_equivalents(expected, gotten)}.extend(ConcatenableOperations)
  # TODO: Lo que no me gusta de esto es que el metodo are_equivalents sigue siendo visible, si lo defino como self.are_equivalents ya no es visible, pero no puedo correrlo desde la lambda
end

private
def are_equivalents(expected, gotten)
  false if expected.size > gotten.size

  firsts = gotten.first(expected.size)

  expected.each_with_index {|elem, index| elem.respond_to?(:call) ? (return false unless elem.call(firsts.at(index))) :  (return false unless firsts.at(index) == elem)}
  # FIXME: esto de arriba no me gusta porque estoy diciendo "che, si esta cosa que me pasaste responde a call mandale mecha...", podria generar problemas?
  true # FIXME: Hay una forma de no tener que devolver true asi ?
end

def duck(first, *others)
  lambda {|gotten| (gotten.respond_to? first) && others.all? {|method_name| gotten.respond_to? method_name} }.extend(ConcatenableOperations)
  # TODO: hay una forma mas linda de hacer esto de los parametros variables ?
end