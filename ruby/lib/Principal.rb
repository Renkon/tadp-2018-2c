require_relative '../lib/Macheador'
require_relative '../lib/Patterns'
require_relative 'Combinador'


class Symbol
  include Combinador

  def call(value, symbol_dictionary = Hash.new)
    if(symbol_dictionary[self] == nil)
      puts 'entro al if true'
      symbol_dictionary[self] = value
      return true
    end
  else
    puts 'entro al else false'
    return false
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
    context.instance_eval(&block)
  end

end