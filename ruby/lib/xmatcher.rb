require_relative 'matchers/value_matcher'
require_relative 'matchers/type_matcher'
require_relative 'matchers/list_matcher'
require_relative 'matchers/duck_matcher'

# We must add 'call' method to Symbol class instances.
class Symbol
  def call(value, bind_to = nil)
    if bind_to
      bind_to.add_property(to_s, value)
    end
    true
  end
end

module XMatcher
  include ValueMatcher
  include TypeMatcher
  include ListMatcher
  include DuckMatcher

  # We define matches? variable which expects an object and a block
  def matches?(object, &block)
    context = MatchingContext.new object

    return_proc = Proc.new { | value | return value }
    context.return_proc = return_proc

    context.instance_exec(&block)
  end
end