# Executed when our mixin is imported
# We must add 'call' method to Symbol class instances.
Symbol.define_method(:call) { | identifier | true }

# Matcher that validates if a value is equal to another one.
def val(value)
  Proc.new { | another_value | value == another_value }
end

# Matcher that validates if a value is of a certain type.
def type(klazz)
  Proc.new { | object | object.class.ancestors.include?(klazz) }
end

# Matcher that validates if a list has got the same first elements as # another list. You can also specify how many elements should be analyzed.
def list(pattern_list, match_size = true)
  Proc.new do
  |original_list|
    original_list_modified = (match_size ? original_list : original_list.take(pattern_list.length))
    list_matcher_result(pattern_list, original_list_modified).all? { | result | result }
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
  Proc.new do
  | object |
    methods_to_check.to_set().subset?(object.methods().to_set())
  end
end