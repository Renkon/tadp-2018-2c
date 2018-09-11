module Combinable
  # Returns a CombinableProc which consists of all the combinable procs sent joined by &.
  def and(*combinable_procs)
    validate_varargs(combinable_procs)
    combinate_procs(combinable_procs, :&, true)
  end

  # Returns a CombinableProc which consists of all the combinable procs sent joined by |.
  def or(*combinable_procs)
    validate_varargs(combinable_procs)
    combinate_procs(combinable_procs, :|, false)
  end

  # Negates current CombinableProc condition.
  def not()
    lambda do | value, bind_to = nil |
      !self.call(value, bind_to)
    end.extend(Combinable)
  end

  # Returns a CombinableProc with a recursive operation (like &&, &&, ||, |, +, etc)
  private
  def combinate_procs(procs, operation, neutral_value)
    lambda do | value, bind_to = nil |
      recursive_operation(operation, procs.unshift(self), value, bind_to, neutral_value)
    end.extend(Combinable)
  end

  private
  def recursive_operation(operation, procs, value, bind_to, neutral_value)
    procs.reduce(neutral_value) do |full_cond, new_cond|
      full_cond.send(operation, new_cond.call(value, bind_to))
    end
  end

  private
  def validate_varargs(elems)
    # In order to write down a friendly error, we get caller method's name and inform the user if sending no args.
    caller_method = "Combinable." + caller[0][/`.*'/][1..-2]

    raise ArgumentError.new "Invalid number of arguments used for #{caller_method}(). At least one required." if elems.length < 1
    raise ArgumentError.new "Invalid elements sent to #{caller_method}. Only Combinable elements allowed." unless elems.all? { | elem | elem.is_a?(Combinable) }
  end
end

class Symbol
  include Combinable
end
