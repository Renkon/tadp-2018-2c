module Combinable
  # Given 1 to n CombinableProcs it combines them as a single CombinableProc with and condition.
  def and(*combinable_procs)
    validate_varargs(combinable_procs)
    combinate_procs(combinable_procs, :&, true)
  end

  # Given 1 to n CombinableProcs it combines them as a single CombinableProc with or condition.
  def or(*combinable_procs)
    validate_varargs(combinable_procs)
    combinate_procs(combinable_procs, :|, false)
  end

  # Negates current CombinableProc condition.
  def not()

  end

  # Returns a CombinableProc with a recursive operation (like &&, &&, ||, |, +, etc)
  private
  def combinate_procs(procs, operation, neutral_value)
    CombinableProc.new do |value|
      recursive_operation(operation, procs.unshift(self), value, neutral_value)
    end
  end

  private
  def recursive_operation(operation, procs, value, neutral_value)
    procs.reduce(neutral_value) do |full_cond, new_cond|
      full_cond.send(operation, new_cond.call(value))
    end
  end

  private
  def validate_varargs(elems)
    # In order to write down a friendly error, we get caller method's name and inform the user if sending no args.
    caller_method = "Combinable." + caller[0][/`.*'/][1..-2] + "()"

    raise "Invalid number of arguments used for #{caller_method}(). At least one required." if elems.length < 1
    raise "Invalid elements sent to #{caller_method}. Only Combinable elements allowed." unless elems.all? { | elem | elem.is_a?(Combinable) }
  end
end



class CombinableProc < Proc
  include Combinable
end
