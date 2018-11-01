require_relative '../combinable'

class Matcher
  include Combinable

  def initialize(&block)
    @block = block
  end

  def call(value, symbol_dictionary = Hash.new)
    @block.call(value, symbol_dictionary)
  end
end