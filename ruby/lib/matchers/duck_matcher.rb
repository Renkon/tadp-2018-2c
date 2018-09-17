module DuckMatcher
  # Matcher that returns if an element understands certain messages.
  def duck(*methods_to_check)
    lambda do
    | object, bind_to = nil |
      methods_to_check.to_set().subset?(object.methods().to_set())
    end.extend(Combinable)
  end
end