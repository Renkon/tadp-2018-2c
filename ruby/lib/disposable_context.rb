class DisposableContext
  def self.from(symbol_dictionary = Hash.new)
    disposable_context = DisposableContext.new
    symbol_dictionary.each_pair { | sym, value | disposable_context.add_property(sym.to_s, value) }
    disposable_context
  end

  def add_property(str, value)
    instance_variable_set("@" + str, value)
    self.singleton_class.send(:attr_reader, str)
  end
end