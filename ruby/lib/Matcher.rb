
module ConcatenableOperations
  def and(first_matcher, *other_matchers)
    PrimitiveMatcher.evaluates { |gotten| self.call(gotten) && first_matcher.call(gotten) && other_matchers.all? {|matcher| matcher.call(gotten)}}
    #ComplexMatcher.evaluates_and(self, second_matcher, other_matchers)
  end

  def or(first_matcher, *other_matchers)
    PrimitiveMatcher.evaluates {|gotten| self.call(gotten) || first_matcher.call(gotten) || other_matchers.any? {|matcher| matcher.call(gotten)}}
    #ComplexMatcher.evaluates_or(self, second_matcher, other_matchers)
  end

  def not
    PrimitiveMatcher.evaluates {|gotten| !self.call(gotten)}
    #ComplexMatcher.evaluates.not(self)
  end
end

module SymbolDictionary
  attr_accessor :symbol_dictionary
  #private
  def obtain_expected
    self.binding.local_variable_get(:expected)
  end

  #private
  def try_to_add_symbols_to_dictionary(gotten)
    self.symbol_dictionary= Hash.new if self.symbol_dictionary.nil?

    expected = self.obtain_expected

    if expected.is_a? Enumerable
      try_to_add_symbols_for_enumerable(gotten)
    else
      if expected.is_a? Symbol
        self.symbol_dictionary[expected.to_sym] = gotten
      end
    end

    puts 'Ahi va el contenido del diccionario : ' + self.symbol_dictionary.to_s
  end

  #private
  def try_to_add_symbols_for_enumerable(gotten)
    self.obtain_expected.each_with_index do |value, index|
      if value.is_a? Symbol
        self.symbol_dictionary[value.to_sym] = gotten.at(index)
      end
    end
  end
end

#Defino el metodo call en los symbols
class Symbol
  def call(a)
    true # FIXME aca falta hacer que el symbol bindee variables... todavia no estaria ni planteado...
  end

  include ConcatenableOperations
  # TODO: ESTO NO ME GUSTA, pero si quiero lograr la interfaz :un_symbol.and(type(Class), etc...), medio como que por ahora no queda otra
end

#Defino los matchers primitivos
module PrimitiveMatcher
  def self.evaluates(&evaluator)
    evaluator.extend(ConcatenableOperations, SymbolDictionary)

    evaluator.instance_eval do
      original_method = self.public_method(:call)
      define_singleton_method(:original_call) {|gotten| original_method.call(gotten)}
    end

    evaluator.define_singleton_method(:call) do |gotten|
      result = self.original_call(gotten)
      self.try_to_add_symbols_to_dictionary(gotten) # if result
      result
    end

    evaluator
  end
end

#module ComplexMatcher
#  def evaluates_and(first_matcher, *matchers)

#  end
#end

def val(expected)
  PrimitiveMatcher.evaluates {|gotten| gotten == expected}
end

def type(expected)
  PrimitiveMatcher.evaluates {|gotten| gotten.is_a?(expected)} # kind_of te retorna true si es una subclase de expected
end

def list(expected, with_size = true) # supuestamente esto lo hace opcional
  PrimitiveMatcher.evaluates {|gotten| expected.is_a?(Enumerable) && gotten.is_a?(Enumerable) && (with_size ? gotten.size == expected.size : true) && are_equivalents(expected, gotten)}
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

def duck(*expected)
  PrimitiveMatcher.evaluates {|gotten| expected.all? {|method_name| gotten.respond_to? method_name} }
  # TODO: hay una forma mas linda de hacer esto de los parametros variables ?
end

#def with(matchers)
#  matchers.get_symbols.each {|symbol| symbol}
#end