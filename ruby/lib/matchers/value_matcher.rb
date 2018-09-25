module ValueMatcher
  def val(expected_value)
    Matcher.new do
    | value, symbol_dictionary | expected_value == value
    end
  end
end