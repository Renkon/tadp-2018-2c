require_relative "disposable_context"

# Extra class useful for our environment
class MatchingContext

  def initialize(object, return_proc)
    @object = object
    @return_proc = return_proc
  end

  # This method is used to define which matchers should be analyzed to execute a chunk of code
  def with(initial_matcher, *matchers, &block)
    matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
    disposable_context = DisposableContext.new
    if matcher_proc.call(@object, disposable_context)
      @return_proc.call disposable_context.instance_eval(&block)
    end
  end

  def otherwise(&block)
    disposable_context = DisposableContext.new
    @return_proc.call disposable_context.instance_eval(&block)
  end
end