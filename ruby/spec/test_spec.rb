describe 'matcher tests' do
    describe 'syntax-test' do
      it '1.call(2) no deberia funcionar, si esta bien armado el fw' do
        expect {1.call(2)}.to raise_error NoMethodError
      end

      it 'type(Integer).call no deberia funcionar sin parametros' do
        expect {type(Integer).call}.to raise_error ArgumentError
      end

      it 'esto no deberia ser visible!!!!!' do
        expect {are_equivalents([1,2,3], [1,2,3,4])}.to raise_error NoMethodError
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
      let(:array_pattern4) {[:a, :b, 2, :a]}

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

      it 'al pasarle un array con symbols y numeros deberia dar true' do
        expect(list(array_pattern4, true).call([1,2,3,4])).to be true
      end

      it 'al pasarle un array con symbols y numeros (o solo symbols) teniendo en cuenta el tamanio y evaluarlo
         con un array mas chico deberia dar false' do
        expect(list(array_pattern4, true).call([1,2,3])).to be false
      end

      it 'si no se especifica match size no deberia romper y deberia considerarlo true, y este test deberia dar true' do
        expect(list(array_pattern4).call([1,2,3,4])).to be true
      end
    end
  end