require_relative 'matchers/matcher'
require_relative 'matchers/value_matcher'
require_relative 'matchers/type_matcher'
require_relative 'matchers/list_matcher'
require_relative 'matchers/duck_matcher'

class Symbol
  def call(value, symbol_dictionary = Hash.new)
    symbol_dictionary[self] = value
    true
  end
end

module XMatcher
  include ValueMatcher
  include TypeMatcher
  include ListMatcher
  include DuckMatcher

  def matches?(object, &block)
    return_proc = Proc.new { | value | return value }
    context = MatchingContext.new(object, return_proc)
    context.instance_exec(&block)
  end
end