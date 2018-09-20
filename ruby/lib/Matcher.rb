module ConcatenableOperations
  def and(first_matcher, *other_matchers)
    all_matchers = [self, first_matcher, other_matchers].flatten
    lambda {|gotten, symbol_dictionary = Hash.new| evaluate_matchers(all_matchers, gotten, symbol_dictionary).all?}
  end

  def or(first_matcher, *other_matchers)
    all_matchers = [self, first_matcher, other_matchers].flatten
    lambda {|gotten, symbol_dictionary = Hash.new| evaluate_matchers(all_matchers, gotten, symbol_dictionary).any?}
  end

  def not
    lambda {|gotten, symbol_dictionary = Hash.new| !self.call(gotten, symbol_dictionary)}
  end

  private
  def evaluate_matchers(matchers, gotten, symbol_dictionary)
    matchers.map {|matcher| matcher.call(gotten, symbol_dictionary)}
  end

end

#Defino el metodo call en los symbols
class Symbol
  def call(a, symbol_dictionary = Hash.new)
    symbol_dictionary[self] = a
    true
  end

  include ConcatenableOperations
end

#Defino los matchers primitivos
def val(expected)
  lambda do |gotten, symbol_dictionary = Hash.new|
    if expected.is_a?(Symbol)
     expected.call(gotten, symbol_dictionary)
    else
      gotten == expected
    end
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
  all_names = Array.new([first, others]).flatten

  lambda do |gotten, symbol_dictionary = Hash.new| all_names.all? {|method_name| gotten.respond_to? method_name} end.extend(ConcatenableOperations)
end

# Definiciones para la parte de matchers
class EvaluationContext
  attr_accessor :value, :evaluation_result
  attr_accessor :end_of_evaluation

  def initialize(value_, end_block = lambda {|context| return context.evaluation_result})
    self.value= value_
    self.end_of_evaluation= end_block #el default sirve solo para que en los test de with no quiera hacer return dede el objeto rspec
  end
end

def matches?(value, &action)
  evaluation_context = EvaluationContext.new(value, proc {|context| return context.evaluation_result})
  evaluation_context.instance_eval &action
end

def with(pattern, &action)
  raise 'El patron necesita un valor con el cual ser evaluado' if(self.value == nil) # no se si esto esta bien, o si hace falta

  symbol_dictionary = Hash.new

  result = pattern.call(self.value, symbol_dictionary)

  if result
    self.evaluation_result= context_from(symbol_dictionary).instance_eval(&action)
    self.end_of_evaluation.call(self)
  end

end

def context_from(symbol_dictionary)
  context = Object.new
  symbol_dictionary.each_pair do |key, value|
    context.instance_eval do
      self.singleton_class.send(:attr_accessor, key)
    end

    context.send((key.to_s + "=").to_sym, value)
  end
  context
end

def otherwise(&action)
  self.evaluation_result= action.call
  self.end_of_evaluation.call(self)
end