 class Symbol
    def call(a)
      true
    end
 end

 def val(expected)
   lambda {|gotten| gotten == expected}
 end

 def type(expected)
   lambda {|gotten| gotten.is_a?(expected)}
 end
