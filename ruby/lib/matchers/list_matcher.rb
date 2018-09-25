module ListMatcher
  # Matcher that validates if a list has got the same first elements as # another list. You can also specify how many elements should be analyzed.
  def list(pattern, fixed_size = true)
    lambda do | value, symbol_dictionary = Hash.new |
      are_enumerables(pattern, value) && eval_list(pattern, value, symbol_dictionary) && valid_size(fixed_size, pattern, value)
    end.extend(Combinable)
  end

  private
  def are_enumerables(pattern, value)
    pattern.is_a?(Enumerable) && value.is_a?(Enumerable)
  end

  def valid_size(fixed_size, pattern, value)
    (fixed_size && value.size == pattern.size) || value.size >= pattern.size
  end

  def eval_list(pattern_list, value_list, symbol_dictionary)
    value_list.zip(pattern_list).map do | value, pattern |
      test = !pattern.nil? && pattern.respond_to?(:call) ? pattern.call(value, symbol_dictionary) : val(pattern).call(value)
      puts "Eval patron: #{pattern} con val: #{value}. Result: #{test}"
      test
    end.all?
    #value_list.each_with_index do |value, index|
    #  pattern_list.size > index && (pattern_list[index].respond_to?(:call) ? pattern_list[index].call(value, symbol_dictionary) : val(pattern_list[index]).call(value))
    #end.all?
  end
end