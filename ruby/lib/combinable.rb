module Combinable
  def and(first_matcher, *other_matchers)
    matchers = [self, first_matcher, other_matchers].flatten
    Matcher.new do
      | value, symbol_dictionary | eval_matchers(matchers, value, symbol_dictionary).all?
    end
  end

  def or(first_matcher, *other_matchers)
    matchers = [self, first_matcher, other_matchers].flatten
    Matcher.new do
      | value, symbol_dictionary | eval_matchers(matchers, value, symbol_dictionary).any?
    end
  end

  def not()
    Matcher.new do
      | value, symbol_dictionary | !self.call(value, symbol_dictionary)
    end
  end

  private
  def eval_matchers(matchers, value, symbol_dictionary)
    matchers.map { | matcher | matcher.call(value, symbol_dictionary) }
  end
end

module CombinableIf
  include Combinable

  def if(&block)
    Matcher.new do
      | value, symbol_dictionary |
        self.call(value, symbol_dictionary)
        value.instance_eval(&block)
    end
  end
end

class Symbol
  include CombinableIf
end
