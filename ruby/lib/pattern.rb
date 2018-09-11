require_relative "combinable"
require_relative "matchers"

# This method is used to define which matchers should be analyzed to execute a chunk of code
def with(value, initial_matcher, *matchers, &block)
  matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
  context = Object.new
  if matcher_proc.call(value, context)
    context.instance_exec(&block)
  end
end

def otherwise(value)
  yield
end