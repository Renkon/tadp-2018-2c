module ListMatcher
  # Matcher that validates if a list has got the same first elements as # another list. You can also specify how many elements should be analyzed.
  def list(pattern_list, match_size = true)
    lambda do
    | original_list, bind_to = nil |
      original_list.is_a?(Enumerable) && pattern_list.is_a?(Enumerable) &&
          list_matcher_result(
              pattern_list,
              (match_size ? original_list : original_list.take(pattern_list.length)), bind_to)
              .all? { | result | result } && pattern_list.length <= original_list.length
    end.extend(Combinable)
  end

  private
  def list_matcher_result(pattern_list, original_list, bind_to)
    original_list.zip(pattern_list).map do | original_value, pattern_value |
      original_value == pattern_value ||
          (!pattern_value.nil? && pattern_value.respond_to?(:call) && pattern_value.call(original_value, bind_to))
    end
  end
end