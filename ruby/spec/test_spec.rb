describe 'matcher tests' do
    describe 'syntax-test' do
      it '1.call(2) no deberia funcionar, si esta bien armado el fw' do
        expect {1.call(2)}.to raise_error NoMethodError
      end

      it 'type(Integer).call no deberia funcionar sin parametros' do
        expect {type(Integer).call}.to raise_error ArgumentError
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
    end
  end