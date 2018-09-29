require_relative 'Combinador.rb'

class Matcher
  include Combinador

  def initialize(&block)
    @block = block
  end

  def call(value, symbol_dictionary = Hash.new)
    @block.call(value, symbol_dictionary)
  end
end

module DuckMatcher
  def duck(first, *others)
    methods = [first, others].flatten
    Matcher.new do
    | value, symbol_dictionary | methods.all? { | method | value.respond_to? method }
    end
  end
end

module ListMatcher
  def list(pattern, fixed_size = true)
    Matcher.new do | value, symbol_dictionary |
      are_enumerables(pattern, value) && eval_list(pattern, value, symbol_dictionary) && valid_size(fixed_size, pattern, value)
    end
  end

  private
  def are_enumerables(pattern, value)
    pattern.is_a?(Enumerable) && value.is_a?(Enumerable)
  end

  def valid_size(fixed_size, pattern, value)
    value.size == pattern.size || (!fixed_size && value.size >= pattern.size)
  end

  def eval_list(pattern_list, value_list, symbol_dictionary)
    value_list.zip(pattern_list).map do | value, pattern |
      pattern.nil? || apply_pattern_matcher(pattern, value, symbol_dictionary)
    end.all?
  end

  def apply_pattern_matcher(pattern, value, symbol_dictionary)
    pattern.respond_to?(:call) ? pattern.call(value, symbol_dictionary) : val(pattern).call(value)
  end
end

module TypeMatcher
  def type(expected_class)
    Matcher.new do
    | value, symbol_dictionary | value.is_a? expected_class
    end
  end
end

module ValueMatcher
  def val(expected_value)
    Matcher.new do
    | value, symbol_dictionary | expected_value == value
    end
  end
end