module TypeMatcher
  def type(expected_class)
    Matcher.new do
      | value, symbol_dictionary | value.is_a? expected_class
    end
  end
end