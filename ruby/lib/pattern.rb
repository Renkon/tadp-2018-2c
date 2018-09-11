require_relative "matchers"

# This method is used to define which matchers should be analyzed to execute a chunk of code
def with(initial_matcher, *matchers, &block)
  matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
  if matcher_proc.call(__object__, self)
    self.__ret__ = instance_eval(&block)
    raise EndOfEvaluation
  end
  clear_context
end

def otherwise(&block)
    self.__ret__ = instance_eval(&block)
    raise EndOfEvaluation
end

# We define matches? variable which expects an object and a block
class Object
  def matches?(object, &block)
    context = MatchingContext.new object
    begin
      context.instance_eval(&block)
    rescue EndOfEvaluation
      context.__ret__
    end
  end
end

# Error class to stop execution
class EndOfEvaluation < RuntimeError
end

# Extra class useful for our environment
class MatchingContext
  attr_accessor :__object__
  attr_accessor :__ret__

  def initialize(object)
    @__object__ = object
    @__custom_properties = []
  end

  def add_property(str, value)
    instance_variable_set("@" + str, value)
    self.singleton_class.send(:attr_accessor, str)
    @__custom_properties.append(str)
  end

  def remove_property(str)
    instance_variable_set("@" + str, nil)
    self.singleton_class.send(:remove_method, str.to_sym)
    self.singleton_class.send(:remove_method, (str + "=").to_sym)
  end

  def clear_context
    @__custom_properties.each { | var | remove_property(var) }
    @__custom_properties = []
    nil
  end
end