describe 'matcher primitives tests' do
  describe 'syntax-and-usage-test' do
    it '1.call(2) no deberia funcionar, si esta bien armado el fw' do
      expect {1.call(2)}.to raise_error NoMethodError
    end

    it 'type(Integer).call no deberia funcionar sin parametros' do
      expect {type(Integer).call}.to raise_error ArgumentError
    end

    it 'esto no deberia ser visible!!!!!' do # FIXME: Problema... ensucia la interfaz de main y el metodo are_equivalents es accesible de todos lados.
      expect {list_pattern_evaluation([1, 2, 3], [1, 2, 3, 4])}.to raise_error NoMethodError
    end

    it 'una lambda cualquiera no deberia tener el metodo and' do
      expect { lambda {}.and(lambda {})}.to raise_error NoMethodError
    end
  end

  describe 'symbol-test' do
    it ':a.call("b") debe andar y devolver true' do
      expect(:a.call("b")).to be true
    end
  end

  describe 'val-test' do
    it 'val(5).call(2) debe dar falso' do
      expect(val(5).call(2)).to be false
    end

    it 'val(5).call(5) debe dar true' do
      expect(val(5).call(5)).to be true
    end

    it 'val(5).call(char 5) deberia dar falso' do
      expect(val(5).call('5')).to be false
    end
  end

  describe 'type-test' do
    it 'type(Symbol).call(2) debe ser false' do
      expect(type(Symbol).call(2)).to be false
    end

    it 'type(Symbol).call(:a) debe ser true' do
      expect(type(Symbol).call(:a)).to be true
    end

    it 'type(Module).call(Comparable) debe ser true' do
      expect(type(Module).call(Comparable)).to be true
    end
  end

  describe 'list-test' do
    let(:array_pattern1) {[1, 2, 3, 4]}
    let(:array_pattern2) {[1, 2, 3]}
    let(:array_pattern3) {[:a, :b, :c, :d]}
    let(:array_pattern4) {[:a, 2, :b, :c]}
    let(:array_pattern5) {[type(Fixnum), :a, val(3)]}

    it 'al pasarle algo que no es una lista deberia dar false' do # TODO : QUE PASA CON LOS HASHES Y LAS DEMAS COSAS ?
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
      expect(list(array_pattern5).call([1,2,3])).to be true
    end
  end

  describe 'duck-test' do
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
  end
end

describe 'combinations tests' do
  let(:empy_array) { Array.new }
  let(:an_array) {[1,2,3]}

  describe 'and-test' do
    it 'deberia poder combinar dos matchers sencillos con and y deberia devolver true al evaluarlos' do
      expect(type(Integer).and(val(3)).call(3)).to be true
    end

    it 'deberia poder combinar tres matchers sencillos y dar true al evaluarlos' do
      expect(:a.and(type(Array), list([1,2,3])).call(an_array)).to be true
    end

    it 'deberia poder combinar dos matchers sencillos y deberia dar false' do
      expect(list([:a, 1, 3]).and(type(Array)).call(an_array)).to be false
      # este caso me hizo darme cuenta que estaba resolviendo mal la comparacion en el patron de listas
    end

    it 'deberia poder combinar varios matchers y deberia dar false' do
      expect(type(Array).and(:a, val(4)).call(4)).to be false
    end
  end

  describe 'or-test' do
    it 'deberia poder combinar dos matchers sencillos con or y deberia dar true' do
      expect(type(Array).or(type(Comparable)).call(3)).to be true
    end

    it 'deberia poder combinar varios matchers y deberia dar true' do
      expect(type(Integer).or(list([1, :a], false), val(1)).call(an_array)).to be true
    end

    it 'deberia poder combinar dos matchers sencillos y dar false' do
      expect(type(Integer).or(val(2)).call(an_array)).to be false
    end

    it 'deberia poder combinar varios matchers y dar false' do
      expect(val(2).or(list([1,2]), type(Class)).call(Object.new)).to be false
    end
  end

  describe 'not-test' do
    it 'cualquier symbol negado deberia devolver false' do
      expect(:a.not.call("g")).to be false
    end

    it 'al preguntar si un numero es entero deberia decir que no' do
      expect(type(Integer).not.call(2)).to be false
    end

    it 'al preguntar si el patron de lista coincide debe decir que no' do
      expect(list([1, :a], false).not.call(an_array)).to be false
    end

    it 'al preguntar si una clase es de tipo string debe decir que si' do
      expect(type(String).not.call(Module)).to be true
    end

    it 'al preguntar si un array entiende all? debe decir que no' do
      expect(duck(:all?).not.call(an_array)).to be false
    end
  end

  describe 'mixed-combinations-test' do
    it 'bueno, deberia decir que si, es el caso que plantea el enunciado' do
      expect(list([duck(:+).and(type(Fixnum), :x), :y.or(val(4)), duck(:+).not]).call([1, 2, Object.new])).to be true
    end
  end
end

describe 'primitive-symbol_dictionary-test' do
  let(:symbol_dictionary) {Hash.new}
  let(:array_pattern1) {[1, 2, 3]}
  let(:array_pattern2) {[:a, :b, :c, :d]}
  let(:array_pattern3) {[:a, 2, :b, :c]}

  it 'deberia retornar el diccionario original mas un nuevo elemento corriespondiente a (:a,2)' do
    symbol_dictionary[:b] = 3
    :a.call(2, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:b=>3, :a=>2})
  end

  it 'deberia obtener el diccionario que contiene :a=>2' do
    :a.call(2, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:a=>2})
  end

  it 'deberia obtener el diccionario vacio' do
    val(3).call(3, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia obtener el diccionario que contiene :a=>2' do
    val(:a).call(1, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:a=>1})
  end

  it 'deberia devolver siempre el diccionario vacio para type porque si le pasas un symbol va a fallar porque no es un tipo, va a dar false' do
    type(Integer).call(2, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario vacio al evaluarlo para list' do
    list(array_pattern1).call([1,2,3], symbol_dictionary) # pense que iba a fallar porque el segundo parametro opcional creo que es un booleano
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario vacio porque la lista esta vacia' do
    list(array_pattern2).call([], symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario vacio porque no matchea' do #TODO esto es lo que queremos que pase ?
    list(array_pattern3).call([1,3,:a], symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario vacio porque un symbol no matchea' do
    list(array_pattern2).call(:a, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario con {:a=>1, :b=>2, :c=>3, :d=>4} al evaluarlo para list' do
    list(array_pattern2).call([1,2,3,4], symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:a=>1, :b=>2, :c=>3, :d=>4})
  end

  it 'deberia devolver el diccionario con {:a=>1, :b=>3, :c=>4}' do
    list(array_pattern3).call([1,2,3,4], symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:a=>1, :b=>3, :c=>4})
  end

  it 'deberia devolver el diccionario vacio porque un array no responde a :volar ni a :saltar' do
    duck(:volar, :saltar).call(Array.new, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'deberia devolver el diccionario con :each y su valor porque el array entiende ese mensaje' do
    duck(:volar, :each).call(Array.new, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:each=>"each"})
  end

  it 'deberia devolver el diccionario con ambos metodos porque array los entiende' do
    duck(:each, :all?).call(Array.new, symbol_dictionary)
    expect(symbol_dictionary.include?(:each) && symbol_dictionary.include?(:all?)).to be true
  end
end

describe 'combinations-symbol_dictionary-test' do
  let(:symbol_dictionary) {Hash.new}

  it 'deberia quedar vacio porque da true pero no hay variables para ligar' do
    val(2).and(type(Integer)).call(2, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'si el primero da false en un and, pero el segundo da true, la variable del segundo deberia queda bindeada y el resultado final es falso' do
    result = list([1,2]).and(duck(:+)).call(1, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({"+".to_sym=>"+"})
    expect(result).to be false
  end

  it 'si el primero da true en un and pero el segundo da false, las variables del primero deberian quedar ligadas' do
    list([:a,:b]).and(type(Integer)).call([1,2], symbol_dictionary)
    expect(symbol_dictionary.include?(:a) && symbol_dictionary.include?(:b)).to be true
  end

  it 'si el primero en un or falla pero el segundo no, el resultado final es true, y la variable del segundo deberia queda bindeada' do # en el with se terminarian descartando al ver que son false
    result = list([1,2]).or(duck(:+)).call(1, symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({"+".to_sym=>"+"})
    expect(result).to be true
  end

  it 'si el primero da true en un or pero el segundo da false, las variables del primero deberian quedar ligadas' do
    list([:a,:b]).or(type(Integer)).call([1,2], symbol_dictionary)
    expect(symbol_dictionary.include?(:a) && symbol_dictionary.include?(:b)).to be true
  end

  it 'con un symbol negado deberia bindear igual las variables' do
    :a.not.call(1 ,symbol_dictionary)
    expect(symbol_dictionary).to be_an_eql({:a=>1})
  end

  it 'no deberia haber variables ligadas luego de la negacion de un val sin symbols' do
    val(2).not.call(2, symbol_dictionary)
    expect(symbol_dictionary).to be_empty
  end

  it 'ante la negacion de un list con symbols deberian quedar bindeadas, siendo el resultado falso' do
    list([:a, 2, :c]).not.call([1,2,3], symbol_dictionary)
    expect(symbol_dictionary.include?(:a) && symbol_dictionary.include?(:c)).to be true
  end

  it 'ante la negacion de un duck igualmente deberian quedar bindeadas, siendo el resultado falso' do
    duck(:each, :all?).not.call(Array.new, symbol_dictionary)
    expect(symbol_dictionary.include?(:each) && symbol_dictionary.include?(:all?)).to be true
  end
end