module DuckMatcher
  def duck(first, *others)
    methods = [first, others].flatten
    Matcher.new do
      | value, symbol_dictionary | methods.all? { | method | value.respond_to? method }
    end
  end
end