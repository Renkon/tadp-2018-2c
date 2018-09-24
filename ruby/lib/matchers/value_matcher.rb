module ValueMatcher
  # Matcher that validates if a value is equal to another one.
  def val(value)
    lambda { | another_value, bind_to = nil | value == another_value }.extend(Combinable)
  end
end