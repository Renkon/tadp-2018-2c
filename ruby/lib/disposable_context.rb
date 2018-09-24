class DisposableContext
  def add_property(str, value)
    instance_variable_set("@" + str, value)
    self.singleton_class.send(:attr_accessor, str)
  end
end