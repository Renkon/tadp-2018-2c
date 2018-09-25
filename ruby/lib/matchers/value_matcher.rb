module ValueMatcher
  # Matcher that validates if a value is equal to another one.
  def val(expected_value)
    lambda { | value, symbol_dictionary = Hash.new | expected_value == value }.extend(Combinable)
  end
end