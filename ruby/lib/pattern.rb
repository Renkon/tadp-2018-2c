require_relative "combinable"
require_relative "matchers"

# This method is used to define which matchers should be analyzed to execute a chunk of code
def with(value, initial_matcher, *matchers, &block)
  context = Object.new
  matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
  if matcher_proc.call(value, context)
    context.instance_exec(&block)
  end
end

def otherwise(value, &block)
  context = Object.new
  context.instance_exec(&block)
end