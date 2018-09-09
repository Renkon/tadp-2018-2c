require_relative "combinable"
require_relative "matchers"

# This method is used to define which matchers should be analyzed to execute a chunk of code
def with(value, initial_matcher, *matchers)
  matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
  matcher_proc.call(value) && yield # TODO: use return to exit with completely?
end

def otherwise(value)
  yield
end