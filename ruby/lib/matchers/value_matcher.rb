module ValueMatcher
  def val(expected_value)
    lambda { | value, symbol_dictionary = Hash.new | expected_value == value }.extend(Combinable)
  end
end