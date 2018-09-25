module ListMatcher
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
    value.size == pattern.size || (!fixed_size && value.size >= pattern.size)
  end

  def eval_list(pattern_list, value_list, symbol_dictionary)
    value_list.zip(pattern_list).map do | value, pattern |
      pattern.nil? || apply_pattern_matcher(pattern, value, symbol_dictionary)
    end.all?
  end

  def apply_pattern_matcher(pattern, value, symbol_dictionary)
    pattern.respond_to?(:call) ? pattern.call(value, symbol_dictionary) : val(pattern).call(value)
  end
end