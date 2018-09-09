describe Prueba do
  let(:prueba) { Prueba.new }

  describe '#materia' do
      it 'debería pasar este test' do
        expect(prueba.materia).to be :tadp
      end

      it 'Prueba de los muchahcos' do
        expect(prueba.los_muchachos).to eq'aca se programa'
      end

  end
end