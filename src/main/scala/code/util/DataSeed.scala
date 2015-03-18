package code
package util

import code.model.festival.{Area, City}
import net.liftweb.record.field.Countries

object DataSeed {

  def run = {
    if (City.count == 0) {
      City.createRecord.country(Countries.C22).name("La Paz").save(true)
      City.createRecord.country(Countries.C22).name("Cochabamba").save(true)
      City.createRecord.country(Countries.C22).name("Santa Cruz").save(true)
    }
    if (Area.count == 0) {
      Area.createRecord.name("Artes Escénicas")
        .description("Ej. Teatro, Danza, Performance, Circo, Títeres, etc...").save(true)
      Area.createRecord.name("Artes Plásticas")
        .description("Ej. Arte urbano, Pintura, Escultura, Dibujo, Arquitectura, Cerámica, etc...").save(true)
      Area.createRecord.name("Artes Populares y/o Tradicionales")
        .description("Artesanía, Textiles, etc...").save(true)
      Area.createRecord.name("Artes AudioVisuales")
        .description("Ej. Cine, Documental, etc...").save(true)
      Area.createRecord.name("Cultura Digital")
        .description("Ej. Software libre, Hackmeeting, Videojuegos,...").save(true)
      Area.createRecord.name("Diseño")
        .description("Ej. Gráfico, Moda, Joyas, etc...").save(true)
      Area.createRecord.name("Fotografía")
        .description("").save(true)
      Area.createRecord.name("Letras")
        .description("Ej. Literatura, Poesía, etc...").save(true)
      Area.createRecord.name("Música")
        .description("Ej. Rock, Pop, Barroco, Clásica,etc...").save(true)
    }
  }

}
