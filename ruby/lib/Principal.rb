require_relative '../lib/Macheador'

class Symbol

  include Combinador

  def call(value, bind_to = nil)
    if bind_to
      bind_to.add_property(to_s, value)
    end
    true
  end

end

class Object
  include Matchers
end

class Pito

  include Combinador

  def initialize
  end

  def unmetodo(lista)
    puts lista.to_s

    acum = 0
    lista.each {|item| acum=item+acum}

    puts (acum)
    acum
  end

  def probando_lista_procs(listaProcs)
    listaProcs.each {|proc| proc.call('Gaby')}
  end

  def otroM
    metod
  end

end