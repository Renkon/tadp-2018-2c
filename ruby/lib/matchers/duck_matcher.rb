module DuckMatcher
  def duck(first, *others)
    methods = [first, others].flatten
    lambda do | value, symbol_dictionary = Hash.new | methods.all? { | method | value.respond_to? method } end.extend(Combinable)
  end
end