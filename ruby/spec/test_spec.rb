# Esto puede ser util
# https://en.wikibooks.org/wiki/Ruby_Programming/Unit_testing

describe 'Part 1 - Matchers' do

  describe 'Val matcher' do
    it 'value matcher should be true if equal value and type' do
      expect(val(5).call(5)).to be true
    end

    it 'value matcher should be false if equal value an different type' do
      expect(val(5).call('5')).to be false
    end

    it 'value matcher should be false if different value' do
      expect(val(5).call(4)).to be false
    end

    it 'value matcher should be false if different value but same type' do
      expect(val("otro valor").call("otro")).to be false
    end

    it 'value matcher should be false if different value and type' do
      expect(val(5).call([5])).to be false
    end
  end

  describe 'type matcher' do
    it '2 no es de tipo symbol' do
      expect(type(Symbol).call(2)).to be false
    end

    it ':a es de tipo symbol' do
      expect(type(Symbol).call(:a)).to be true
    end

    it 'comparable es de tipo modulo' do
      expect(type(Module).call(Comparable)).to be true
    end

    it 'si declaro una clase de tipo ganzo, su instancia deber ser de ese tipo' do
      class Ganzo
        def initialize
        end
      end
      ganzo = Ganzo.new
      expect(type(Ganzo).call(ganzo)).to be true
    end

    it 'si declaro una lambda debe de ser tipo Proc' do
      lambda_new = lambda {return "123"}
      expect(type(Proc).call(lambda_new)).to be true
    end

    it 'si declaro una lambda no debe de ser tipo Fixnum' do
      lambda_new = lambda {return "123"}
      expect(type(Fixnum).call(lambda_new)).to be false
    end
  end

  describe 'List matcher' do
    let(:array_pattern1) {[1, 2, 3, 4]}
    let(:array_pattern2) {[1, 2, 3]}
    let(:array_pattern3) {[:a, :b, :c, :d]}
    let(:array_pattern4) {[:a, 2, :b, :c]}
    let(:array_pattern5) {[type(Fixnum), :a, val(3)]}

    it 'al pasarle algo que no es una lista deberia dar false' do
      expect(list([],false).call(Module)).to be false
    end
    it 'al pasarle un array de cuatro siendo que espera ese mismo y tiene en cuenta el tamanio,
          deberia devolver true' do
      expect(list(array_pattern1, true).call([1, 2, 3, 4])).to be true # es un objeto distinto a proposito
    end

    it 'al pasarle un array de cuatro siendo que espera ese mismo y NO tiene en cuenta el tamanio,
          deberia devolver true' do
      expect(list(array_pattern1, false).call([1, 2, 3, 4])).to be true # es un objeto distinto a proposito
    end

    it 'al pasarle lo que espera pero al reves deberia ser false, sin importar el tamanio,
          porque sino no anda bien la busqueda de los primeros N elementos' do
      expect(list(array_pattern1, true).call([4,3,2,1])).to be false
    end

    it 'al pasarle un array con todos symbols deberia dar true' do
      expect(list(array_pattern3, true).call([1,2,3,4])).to be true
    end

    it 'al pasarle un array con symbols y numeros deberia dar true, cuando se evalua con un array con los elementos en el orden esperado' do
      expect(list(array_pattern4, true).call([1,2,3,4])).to be true
    end

    it 'al pasarle un array con symbols y numeros (o solo symbols) teniendo en cuenta el tamanio y evaluarlo
         con un array mas chico deberia dar false' do
      expect(list(array_pattern4, true).call([1,2,3])).to be false
    end

    it 'si no se especifica match size no deberia romper y deberia considerarlo true, y este test deberia dar true' do
      expect(list(array_pattern4).call([1,2,3,4])).to be true
    end

    it 'al pasarle una lista con matchers deberia andar' do
      # expect(list(array_pattern5).call([1,2,3])).to be true
      # conversar este caso
    end

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

    let(:some_array) { Array.new }

    it 'un array entiende each' do
      expect(duck(:each).call(some_array)).to be true
    end

    it 'un array entiende each, all?' do
      expect(duck(:each, :all?).call(some_array)).to be true
    end

    it 'un array entiende each, all? y any?' do
      expect(duck(:each, :all?, :any?).call(some_array)).to be true
    end

    it 'un array no entiende salto_ninja' do
      expect(duck(:salto_ninja).call(some_array)).to be false
    end

    it 'un array entiende any? y all? pero no entiende salto_ninja, debe dar false' do
      expect(duck(:any?, :all?, :salto_ninja).call(some_array)).to be false
    end

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