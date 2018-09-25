module Combinable
  def and(first_matcher, *other_matchers)
    matchers = [self, first_matcher, other_matchers].flatten
    lambda { | value, symbol_dictionary = Hash.new | eval_matchers(matchers, value, symbol_dictionary).all? }.extend(Combinable)
  end

  def or(first_matcher, *other_matchers)
    matchers = [self, first_matcher, other_matchers].flatten
    lambda { | value, symbol_dictionary = Hash.new | eval_matchers(matchers, value, symbol_dictionary).any? }.extend(Combinable)
  end

  def not()
    lambda do | value, symbol_dictionary = Hash.new |
      !self.call(value, symbol_dictionary)
    end.extend(Combinable)
  end

  def eval_matchers(matchers, value, symbol_dictionary)
    matchers.map { | matcher | matcher.call(value, symbol_dictionary) }
  end
end

class Symbol
  include Combinable
end
