require_relative "xmatcher"

# Extra class useful for our environment
class MatchingContext
  attr_accessor :__object__
  attr_accessor :__ret__
  attr_accessor :return_proc

  def initialize(object)
    @__object__ = object
  end

  # This method is used to define which matchers should be analyzed to execute a chunk of code
  def with(initial_matcher, *matchers, &block)
    matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
    disposable_context = DisposableContext.new
    if matcher_proc.call(__object__, disposable_context)
      return_proc.call disposable_context.instance_eval(&block)
    end
  end

  def otherwise(&block)
    disposable_context = DisposableContext.new
    return_proc.call disposable_context.instance_eval(&block)
  end
end

class DisposableContext
  def add_property(str, value)
    instance_variable_set("@" + str, value)
    self.singleton_class.send(:attr_accessor, str)
  end
end