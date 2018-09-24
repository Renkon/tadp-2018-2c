module TypeMatcher
  # Matcher that validates if a value is of a certain type.
  def type(klazz)
    lambda { | object, bind_to = nil | object.is_a?(klazz) }.extend(Combinable)
  end
end