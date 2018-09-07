# Executed when our mixin is imported
# We must add 'call' method to Symbol class instances.
Symbol.define_method(:call) { | identifier | true }

class Object

  # Matcher that validates if a value is equal to another one.
  private
  def val(value)
    Proc.new { | another_value | value == another_value }
  end

  # Matcher that validates if a value is of a certain type.
  private
  def type(klazz)
    Proc.new { | object | object.class.ancestors.include?(klazz) }
  end

  # Matcher that validates if a list has got the same first elements as
  # another list. You can also specify how many elements should be analyzed.
  private
  def list(pattern_list, match_size = true)
    Proc.new do
    |original_list|
      pattern_list == (match_size ? original_list : original_list.take(pattern_list.length))
    end
  end

  # Matcher that returns if an element understands certain messages.
  private
  def duck(*methods_to_check)
    Proc.new do
      | object |
        methods_to_check.to_set().subset?(object.methods().to_set())
    end
  end
end