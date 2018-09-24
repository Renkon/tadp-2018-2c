module ValueMatcher
  # Matcher that validates if a value is equal to another one.
  def val(expected)
    lambda { | value, symbol_dictionary = Hash.new | expected == value }.extend(Combinable)
  end
end