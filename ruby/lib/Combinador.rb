module Combinador

  def and(*lista_matcher)
    combinator = CombinadorConcreto.new
    combinator.validator(lista_matcher)

    lista_completa = [self]+lista_matcher
    lambda { | valor | combinator.aplicar_matchers(lista_completa, valor).all? }
  end

  def or(*lista_matcher)
    combinator = CombinadorConcreto.new
    combinator.validator(lista_matcher)

    lista_completa = [self]+lista_matcher
    lambda { | valor | combinator.aplicar_matchers(lista_completa, valor).any? }
  end

  def not
    lambda { |valor| !self.call(valor)}
  end

  class CombinadorConcreto
    def initialize
    end

    def validator(lista_matchers)
      raise ArgumentError, 'Argument cant be less than 1' if lista_matchers.size < 1
      raise ArgumentError, 'Argument should be a proc' unless lista_matchers.all? {|matcher| matcher.is_a? Proc}
    end

    def aplicar_matchers(lista_matchers, valor)
      lista_matchers.map {|marcher| marcher.call(valor)}
    end

  end

end