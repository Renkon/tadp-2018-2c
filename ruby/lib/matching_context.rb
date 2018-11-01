require_relative "disposable_context"

class MatchingContext
  def initialize(object, return_proc)
    @object = object
    @return_proc = return_proc
  end

  def with(initial_matcher, *matchers, &block)
    matcher_proc = matchers.length > 0 ? initial_matcher.and(*matchers) : initial_matcher
    symbol_dictionary = Hash.new
    if matcher_proc.call(@object, symbol_dictionary)
      context_exec(symbol_dictionary, &block)
    end
  end

  def otherwise(&block)
    context_exec &block
  end

  private
  def context_exec(symbol_dictionary = Hash.new, &block)
    disposable_context = DisposableContext.from symbol_dictionary
    @return_proc.call disposable_context.instance_eval(&block)
  end
end