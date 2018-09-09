require_relative 'Combinators'

Symbol.define_singleton_method(:call) { | identifier | true }

# Matcher that validates if a value is equal to another one.
def val(value)
  CombinableProc.new { | another_value | value == another_value }
end

# Matcher that validates if a value is of a certain type.
def type(klazz)
  CombinableProc.new { | object | object.is_a?(klazz) }
end

# Matcher that validates if a list has got the same first elements as # another list. You can also specify how many elements should be analyzed.
def list(pattern_list, match_size = true)
  CombinableProc.new do
  |original_list|
    original_list.is_a?(Enumerable) && pattern_list.is_a?(Enumerable) &&
        list_matcher_result(
            pattern_list,
            (match_size ? original_list : original_list.take(pattern_list.length)))
            .all? { | result | result } && pattern_list.length <= original_list.length
  end
end

# Used only by list to bind/match lists
def list_matcher_result(pattern_list, original_list)
  match_results = []
  original_list.zip(pattern_list).each do | original_value, pattern_value |
    match_results.append(original_value == pattern_value ||
                             (!pattern_value.nil? &&
                                 pattern_value.respond_to?(:call) && pattern_value.call(original_value)))
  end
  match_results
end

# Matcher that returns if an element understands certain messages.
def duck(*methods_to_check)
  CombinableProc.new do
  | object |
    methods_to_check.to_set().subset?(object.methods().to_set())
  end
end