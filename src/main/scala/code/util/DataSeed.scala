package code
package util

import code.model.festival._
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
    if (Space.count == 0) {
      Space.createRecord.name("Teatros").save(true)
      Space.createRecord.name("Escuelas").save(true)
      Space.createRecord.name("Asociaciones").save(true)
      Space.createRecord.name("Museos").save(true)
      Space.createRecord.name("Bibliotecas").save(true)
      Space.createRecord.name("Centros Sociales").save(true)
      Space.createRecord.name("Bares y Restaurantes").save(true)
      Space.createRecord.name("Centros Culturales").save(true)
      Space.createRecord.name("Plazas").save(true)
      Space.createRecord.name("Calles").save(true)
      Space.createRecord.name("Espacios Alternativos").save(true)
      Space.createRecord.name("Salas o salones").save(true)
    }
    if (PublicKind.count == 0) {
      PublicKind.createRecord.name("Infantil").save(true)
      PublicKind.createRecord.name("Juvenil").save(true)
      PublicKind.createRecord.name("Adulto").save(true)
      PublicKind.createRecord.name("Adulto Mayor").save(true)
      PublicKind.createRecord.name("Todos").save(true)
    }
    if (ServiceExchange.count == 0) {
      ServiceExchange.createRecord.name("Trueque").save(true)
      ServiceExchange.createRecord.name("Fondo").save(true)
      ServiceExchange.createRecord.name("Servicios / Recursos Humanos").save(true)
      ServiceExchange.createRecord.name("Colaboración").save(true)
    }
    if (CommunicationTool.count == 0) {
      CommunicationTool.createRecord.name("Televisión").save(true)
      CommunicationTool.createRecord.name("Diarios y revistas").save(true)
      CommunicationTool.createRecord.name("Radio").save(true)
      CommunicationTool.createRecord.name("Facebook").save(true)
      CommunicationTool.createRecord.name("Twitter").save(true)
      CommunicationTool.createRecord.name("Web / Blog").save(true)
      CommunicationTool.createRecord.name("Afiches").save(true)
      CommunicationTool.createRecord.name("Volantes (Flyers)").save(true)
      CommunicationTool.createRecord.name("Fancines").save(true)
      CommunicationTool.createRecord.name("Perifoneo (Movilidad con parlantes o megáfono)").save(true)
      CommunicationTool.createRecord.name("e-mailing (Voletin electronico, newsletter...)").save(true)
      CommunicationTool.createRecord.name("Pasacalles/banners").save(true)
      CommunicationTool.createRecord.name("Actividad en espacio público").save(true)
    }
    if (TrainingActivity.count == 0) {
      TrainingActivity.createRecord.name("Mesas de dialogo").save(true)
      TrainingActivity.createRecord.name("Talleres y/o Cursos").save(true)
      TrainingActivity.createRecord.name("Conferencias").save(true)
      TrainingActivity.createRecord.name("Simposios, seminarios").save(true)
      TrainingActivity.createRecord.name("Intercambio de experiencias").save(true)
      TrainingActivity.createRecord.name("Laboratorios").save(true)
    }
    if (Partnership.count == 0) {
      Partnership.createRecord.name("Convenio").kind(PartnershipKind.Public).save(true)
      Partnership.createRecord.name("Auspicio").kind(PartnershipKind.Public).save(true)
      Partnership.createRecord.name("Colaboración").kind(PartnershipKind.Public).save(true)
      Partnership.createRecord.name("Convenio").kind(PartnershipKind.Private).save(true)
      Partnership.createRecord.name("Auspicio").kind(PartnershipKind.Private).save(true)
      Partnership.createRecord.name("Colaboración").kind(PartnershipKind.Private).save(true)
      Partnership.createRecord.name("Convenio").kind(PartnershipKind.CivilSociety).save(true)
      Partnership.createRecord.name("Auspicio").kind(PartnershipKind.CivilSociety).save(true)
      Partnership.createRecord.name("Colaboración").kind(PartnershipKind.CivilSociety).save(true)
    }
  }

}
