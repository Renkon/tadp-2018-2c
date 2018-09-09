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

    it 'list matcher should work but return false when sending pattern with more than N elements' do
      expect(list([1, 2, 3, 4, 5], true).call(an_array)).to be false
    end

    it 'list should be false if sending a number as pattern list' do
      expect(list(1).call(an_array)).to be false
    end

    it 'list should be false if sending a number as caller list' do
      expect(list([1, 2]).call(1)).to be false
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

describe 'Part 2 - Combinators' do
  describe 'and combinator' do
    it '1 should be both an integer and comparable' do
      expect(type(Integer).and(type(Comparable)).call(1)).to be true
    end

    it 'A simple array should not be comparable' do
      expect(type(Comparable).and(type(Array)).call([])).to be false
    end

    it '1 should be an integer with value 1 that understands plus and minus' do
      expect(type(Integer).and(val(1), duck(:+, :-)).call(1)).to be true
    end

    it 'and should explode if no args sent' do
      expect { :anything.and() }.to raise_error(ArgumentError)
    end
  end

  describe 'or combinator' do
    it '1 should be value 1 or a string' do
      expect(val(1).or(type(String)).call(1)).to be true
    end

    it 'A symbol should be bound to 1 or value should be 10' do
      expect(:a_symbol.or(val(10)).call(1)).to be true
    end

    it '5 should not be a String or be 7 or understand kamehameha' do
      expect(type(String).or(val(7), duck(:kamehameha)).call(5)).to be false
    end

    it 'or should explode if no args sent' do
      expect { duck(:something).or() }.to raise_error(ArgumentError)
    end
  end

  describe 'not combinator' do
    it '5 should not be 5 if negated' do
      expect(val(5).not.call(5)).to be false
    end

    it ':symbol should be a symbol if negated twice' do
      expect(type(Symbol).not.not.call(:symbol)).to be true
    end

    it 'not should fail if args sent' do
      expect { val(3).not(1) }.to raise_error(ArgumentError)
    end
  end

  describe 'complex combinators' do
    it 'Chau shouldnt be a symbol or Hola or integer and bound to a symbol' do
      expect(type(Symbol).not.or(val("Hola"), type(Integer).and(:a_symbol)).call("Chau")).to be true
    end

    it '3 should understand * and be Integer and Comparable but should not be 5 and should be bound to :asd or used with list' do
      expect(duck(:*).and(type(Integer), type(Comparable), val(5).not, :asd.or(list([3]))).call(3)).to be true
    end

    it 'List should match pattern and be of type Enumerable and not understand :potato negated' do
      expect(list([:xin_zhao, :malphite, :kindred], false).and(type(Enumerable), duck(:potato).not).not.call([100, 101, 102, 103, 104, 105])).to be false
    end
  end
end