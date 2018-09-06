#Defino el metodo call en los symbols
class Symbol
  def call(a)
    true
  end
end

#Defino los matchers basicos
def val(expected)
  lambda {|gotten| gotten == expected}
end

def type(expected)
  lambda {|gotten| gotten.is_a?(expected)} # kind_of te retorna true si es una subclase de expected
end

def list(expected, with_size = true) # supuestamente esto lo hace opcional
  lambda {|gotten| (with_size ? gotten.size == expected.size : true) && are_equivalents(expected, gotten)}
  # Lo que no me gusta de esto es que el metodo are_equivalents sigue siendo visible, si lo defino como
  # self.are_equivalents ya no es visible, pero no puedo correrlo desde la lambda
end

private
def are_equivalents(expected, gotten)
  # alternativas :
  #   comparision <=>
  #   == (pero tiene en cuenta la cantidad)
  #  drop o drop_while y una logica media procedural
  #  each o each_index y una logica tambien media procedural
  false if expected.size > gotten.size

  firsts = gotten.first(expected.size)

  result = false

  expected.each_with_index {|elem, index| type(Symbol).call(elem) ? result = elem.call(firsts.at(index)) :  result = firsts.at(index) == elem}

  result
end