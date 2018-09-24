module TypeMatcher
  # Matcher that validates if a value is of a certain type.
  def type(expected_class)
    lambda { | value, symbol_dictionary = Hash.new | value.is_a?(expected_class) }.extend(Combinable)
  end
end