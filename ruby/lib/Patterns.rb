#3. Pattern
#Llamaremos patrón a un conjunto determinado de características que un objeto puede tener, es decir, a una colección de Matchers. Como cabe esperar, el patrón puede ser evaluado con un objeto, y determinará si éste cumple con TODAS las características definidas.
#
 #   Además, cada patrón tiene asociado un bloque donde se permite explotar el objeto, pudiendo acceder a parte de su estructura interna mediante variables.
#
 #   with: genera un patrón a partir de un conjunto de Matchers pasados por parámetro y el bloque a asociar.
#
 #   with(type(Animal), duck(:fly)) { ... }
#
#otherwise: genera el patrón neutro, que siempre se verifica (es decir, no define ninguna característica en particular). No recibe parámetros, solo el bloque a asociar.
#
 #   match: llamamos match al evento producido cuando un objeto “encaja” (matchea) con cierto patrón, es decir, cuando cumple con todas las características definidas. El resultado de un match es la ejecución del bloque asociado al patrón.
 #   binding: el Matcher Variable tiene una particularidad; ante un match, las variables definidas se bindearán con los valores correspondientes para poder ser utilizadas dentro del bloque de manera limpia y sencilla.
#
#    with(type(String), :a_string) { a_string.length }
#with(type(Integer), :size) { size }
#
#Si se aplica sobre [1,2]
#with(list[:a, :b]) { a + b } #=> 3
#Si se aplica sobre [1,2,Object.new]
#with(list([duck(:+).and(type(Fixnum), :x),
#           :y.or(val(4)), duck(:+).not])) { x + y } #=> 3
#Veremos unos ejemplos completos en la próxima sección.
#