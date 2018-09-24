module DuckMatcher
  # Matcher that returns if an element understands certain messages.
  def duck(first, *others)
    all_names = [first, others].flatten
    lambda do | value, symbol_dictionary = Hash.new | all_names.all? { |method_name| value.respond_to? method_name } end.extend(Combinable)
  end
end