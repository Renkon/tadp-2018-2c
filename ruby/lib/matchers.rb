require_relative 'combinable'

# We must add 'call' method to Symbol class instances.
class Symbol
  def call(value, bind_to = nil)
    if bind_to
      bind_to.add_property(to_s, value)
    end
    true
  end
end

class Object
  # Matcher that validates if a value is equal to another one.
  def val(value)
    lambda { | another_value, bind_to = nil | value == another_value }.extend(Combinable)
  end

  # Matcher that validates if a value is of a certain type.
  def type(klazz)
    lambda { | object, bind_to = nil | object.is_a?(klazz) }.extend(Combinable)
  end

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

  # Used only by list to bind/match lists
  def list_matcher_result(pattern_list, original_list, bind_to)
    original_list.zip(pattern_list).map do | original_value, pattern_value |
      original_value == pattern_value ||
          (!pattern_value.nil? && pattern_value.respond_to?(:call) && pattern_value.call(original_value, bind_to))
    end
  end

  # Matcher that returns if an element understands certain messages.
  def duck(*methods_to_check)
    lambda do
    | object, bind_to = nil |
      methods_to_check.to_set().subset?(object.methods().to_set())
    end.extend(Combinable)
  end
end