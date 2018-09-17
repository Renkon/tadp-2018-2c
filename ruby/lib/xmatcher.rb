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
    eval_output = nil
    catch :evalSuccess do
      eval_output = context.instance_eval(&block)
    end
    # If __ret is not set (because no with or otherwise matches)
    # We will default to the value of the evaluation of the block
    context.__ret__ || eval_output
  end
end