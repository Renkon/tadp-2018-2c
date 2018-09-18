require_relative 'Combinador.rb'

module Matchers

  def val(a_value)
    lambda { | another_value | a_value == another_value }.extend(Combinador)
  end

  def type(a_type)
    lambda { | object | object.is_a?(a_type) }.extend(Combinador)
  end

  def list(list_to_match, match_size = true)
    lambda do
    | a_list |
          are_lists?(a_list, list_to_match) &&
          list_matcher_result(list_to_match, list_to_bind(a_list, list_to_match, match_size), nil ).all? { | result | result } &&
              list_to_match.length <= a_list.length
    end.extend(Combinador)

  end

  private def are_lists?(a_list, list_to_match)
    a_list.is_a?(Enumerable) && list_to_match.is_a?(Enumerable)
  end

  private def list_to_bind(a_list, list_to_match, match_size)
    match_size ? a_list : a_list.take(list_to_match.length)
  end

  private def list_matcher_result(list_to_match, a_list, bind_to)
    a_list.zip(list_to_match).map do | original_value, pattern_value |
        original_value == pattern_value ||
        (!pattern_value.nil? && pattern_value.respond_to?(:call) && pattern_value.call(original_value, bind_to))
    end
  end

  def duck(*mesagges)
    lambda do | object |
      mesagges.to_set().subset?(object.methods().to_set())
    end.extend(Combinador)
  end

end
