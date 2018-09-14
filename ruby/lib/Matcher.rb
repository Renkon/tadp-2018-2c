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
  def call(a, symbol_dictionary = Hash.new)
    symbol_dictionary[self] = a
    true
  end

  include ConcatenableOperations
  # TODO: ESTO NO ME GUSTA, pero si quiero lograr la interfaz :un_symbol.and(type(Class), etc...), medio como que por ahora no queda otra
end

#Defino los matchers primitivos
def val(expected)
  lambda do |gotten, symbol_dictionary = Hash.new|
    if expected.is_a?(Symbol)
      symbol_dictionary[expected] = gotten
    end
    gotten == expected
  end.extend(ConcatenableOperations)
end

def type(expected)
  lambda do |gotten, symbol_dictionary = Hash.new|
    gotten.is_a?(expected)
  end.extend(ConcatenableOperations) # Aca no tengo que tocar nada con el diccionario porque no se puede recibir un symbol
end

def list(expected, with_size = true) # supuestamente esto lo hace opcional
  lambda do |gotten, symbol_dictionary = Hash.new|
    expected.is_a?(Enumerable) && gotten.is_a?(Enumerable) && (with_size ? gotten.size == expected.size : true) && list_pattern_evaluation(expected, gotten, symbol_dictionary)
  end.extend(ConcatenableOperations)
  # TODO: Lo que no me gusta de esto es que el metodo are_equivalents sigue siendo visible, si lo defino como self.are_equivalents ya no es visible, pero no puedo correrlo desde la lambda
end

private
def list_pattern_evaluation(expected, gotten, symbol_dictionary = Hash.new)
  final_result = true
  final_result = false if expected.size > gotten.size

  firsts = gotten.first(expected.size)

  expected.each_with_index do |elem, index|
    if elem.respond_to?(:call)
      final_result = false unless elem.call(firsts.at(index), symbol_dictionary)
    else
      final_result = false unless firsts.at(index) == elem
    end
  end

  final_result
end

def duck(first, *others)
  lambda {|gotten| (gotten.respond_to? first) && others.all? {|method_name| gotten.respond_to? method_name} }.extend(ConcatenableOperations)
  # TODO: hay una forma mas linda de hacer esto de los parametros variables ?
end