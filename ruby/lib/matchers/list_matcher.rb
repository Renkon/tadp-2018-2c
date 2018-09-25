module ListMatcher
  # Matcher that validates if a list has got the same first elements as # another list. You can also specify how many elements should be analyzed.
  def list(expected, with_size = true)
    lambda do | value, symbol_dictionary = Hash.new |
      expected.is_a?(Enumerable) && value.is_a?(Enumerable) && (!with_size || value.size == expected.size) && list_pattern_evaluation(expected, value, symbol_dictionary)
    end.extend(Combinable)
  end

  def list_pattern_evaluation(expected, gotten, symbol_dictionary = Hash.new)
    final_result = expected.size > gotten.size
    firsts = gotten.first(expected.size)
    expected.each_with_index do |elem, index|
      elem.respond_to?(:call) ? final_result = elem.call(firsts.at(index), symbol_dictionary) : final_result = firsts.at(index) == elem
    end
    final_result
  end
end