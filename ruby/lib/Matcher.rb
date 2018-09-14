module ConcatenableOperations
  def and(first_matcher, *other_matchers)
    all_matchers = [self, first_matcher, other_matchers].flatten
    lambda {|gotten, symbol_dictionary = Hash.new| evaluate_matchers(all_matchers, gotten, symbol_dictionary).all?}
    #self.call(gotten, symbol_dictionary) & first_matcher.call(gotten, symbol_dictionary) & other_matchers.all? {|matcher| matcher.call(gotten, symbol_dictionary)}}
  end

  def or(first_matcher, *other_matchers)
    all_matchers = [self, first_matcher, other_matchers].flatten
    lambda {|gotten, symbol_dictionary = Hash.new| evaluate_matchers(all_matchers, gotten, symbol_dictionary).any?}
    # self.call(gotten, symbol_dictionary) || first_matcher.call(gotten, symbol_dictionary) || other_matchers.any? {|matcher| matcher.call(gotten)}
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

  lambda do |gotten, symbol_dictionary = Hash.new| (gotten.respond_to? first) && others.all? {|method_name| gotten.respond_to? method_name}
    #final_result = true

    #all_names.each do |method_name|
    #  if gotten.respond_to? method_name
    #    symbol_dictionary[method_name.to_sym] = method_name.to_s
    #    next
    #  end

     # final_result = false
    #end

    #final_result
    ## TODO PREGUNTARRRRRRRRRRRRRRRRRRRRR que onda....
  end.extend(ConcatenableOperations)
end

def matches?(value, &action)
  define_instance_variable(:value)
  self.value= value
  define_instance_variable(:done)
  self.done= false
  action.call
end # TODO se espera que ademas retorne true o false ?

def define_instance_variable(variable_name)
  p 'variable_name tiene ' + variable_name.to_s
  self.instance_variable_set("@" + variable_name.to_s, nil)
  self.define_singleton_method(variable_name) {return variable_name}
  self.define_singleton_method((variable_name.to_s + "=").to_sym) {|new_value| variable_name = new_value}
end

def with(pattern, &action)
  return if self.done

  raise 'El patron necesita un valor con el cual ser evaluado' if(self.value == nil) # no se si esto esta bien, o si hace falta

  symbol_dictionary = Hash.new

  result = pattern.call(self.value, symbol_dictionary)

  self.done= result

  p 'result dio :' + result.to_s
  p 'value tiene :' + self.value.to_s
  p 'el diccionario de simbolos :'+ symbol_dictionary.inspect

  context_from(symbol_dictionary).instance_eval(&action) if result
end

def context_from(symbol_dictionary)
  context = Object.new
  symbol_dictionary.each_pair do |key, value|
    p 'key tiene : ' + key.to_s
    context.instance_eval do
      p 'adentro del bloque, key tiene ' + key.to_s
      define_instance_variable(key)
    end

    context.send((key.to_s + "=").to_sym, value)
  end
  p 'voy a retornar el contexto armadito, sus variables son : ' + context.instance_variables.to_s
  context
end

def otherwise(&action)
  self.done= true
  action.call
end