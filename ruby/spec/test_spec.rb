describe 'Part 1 - Matchers' do
  describe 'Variable matcher' do
    it 'variable matcher should be true' do
      expect(:something.call('some value')).to be true
    end
  end

  describe 'Value matcher' do
    it 'value matcher should be true if equal value and type' do
      expect(val(5).call(5)).to be true
    end

    it 'value matcher should be false if equal value an different type' do
      expect(val(5).call('5')).to be false
    end

    it 'value matcher should be false if different value' do
      expect(val(5).call(4)).to be false
    end
  end

  describe 'Type matcher' do
    it 'type matcher should be true if element is an instance of the class' do
      expect(type(Integer).call(5)).to be true
    end

    it 'type matcher should be false if element is not an instance of the class' do
      expect(type(Symbol).call('Trust me, I\'m a symbol..')).to be false
    end

    it 'type matcher should be true if element is an instance of the class' do
      expect(type(Symbol).call(:a_real_symbol)).to be true
    end
  end

  describe 'List matcher' do
    let(:an_array) { [1, 2, 3, 4] }

    it 'list matcher should be true if same list is send to match and match size matter' do
      expect(list([1, 2, 3, 4], true).call(an_array)).to be true
    end

    it 'list matcher should be true if same list is send to match and match size does not matter' do
      expect(list([1, 2, 3, 4], false).call(an_array)).to be true
    end

    it 'list matcher should be false if same list is send to match and match size matter' do
      expect(list([1, 2, 3], true).call(an_array)).to be false
    end

    it 'list matcher should be true if same list is send to match and match size does not matter' do
      expect(list([1, 2, 3], false).call(an_array)).to be true
    end

    it 'list matcher should be false if list with same elements in different order is send to match and match size does matter' do
      expect(list([2, 1, 3, 4], true).call(an_array)).to be false
    end

    it 'list matcher should be false if list with same elements in different order is send to match and match size does not matter' do
      expect(list([2, 1, 3, 4], false).call(an_array)).to be false
    end

    it 'list matcher should be false if sending list of different size and no match size' do
      expect(list([1, 2, 3]).call(an_array)).to be false
    end

    it 'list matcher should be mixed with variable matcher and should return true' do
      expect(list([:a, :b, :c, :d]).call(an_array)).to be true
    end

    it 'list matcher should be mixed with variables and values and should return true' do
      expect(list([1, :a, 3, :b]).call(an_array)).to be true
    end

    it 'list matcher should be mixed with variables with wrong order and should return false' do
      expect(list([1, 3, :a, 4]).call(an_array)).to be false
    end
  end

  describe 'Duck matcher' do
    class Dragon
      def fly
        'do some flying'
      end
    end

    let(:psyduck) { this = Object.new
      def this.cuack
        'psy..duck?'
      end
      def this.fly
        '(headache)'
      end
      this
    }
    let(:a_dragon) { Dragon.new }

    it 'duck matcher should find the two methods available for psyduck' do
      expect(duck(:cuack, :fly).call(psyduck)).to be true
    end

    it 'duck matcher should not find the two methods availeble for dragons' do
      expect(duck(:cuack, :fly).call(a_dragon)).to be false
    end

    it 'duck matcher should find fly for a dragon' do
      expect(duck(:fly).call(a_dragon)).to be true
    end

    it 'duck matcher should find to_s for an object' do
      expect(duck(:to_s).call(Object.new)).to be true
    end
  end
end