package code
package model
package link

import code.model.development.Development
import code.model.development.RoleType._
import code.model.field.StringDataType

class LinkSpec extends BaseMongoSessionWordSpec {

  "Link" should {
    "create, validate, save, and retrieve properly" in {

      // Development
      val development1 = Development.createRecord
        .name("Carol")
        .role(Responsible)

      val development2 = Development.createRecord
        .name("Lou")
        .role(Responsible)

      development1.save(false)
      development2.save(false)

      // Link

      val link1 = StringDataType("Fora do Eixo", List("https://docs.google.com/a/foradoeixo.org.br/spreadsheet/ccc?key=" +
        "0As8Q1RK2MpetdFdFNEZTUk1uYVBZck5Ha2pLM0J1Vmc&usp=sharing", "https://docs.google.com/a/foradoeixo.org.br/document/" +
        "d/1QDWgCuBT6vNxljcPZaZmkVvdM0hViWT88NZmbYvM-68/edit"))

      // Detail links

      val linkDetail1 = LinkDetail.createRecord
        .product("Relatoria Imersion Cultura de Red")
        .responsible(development1 :: development2 :: Nil)
        .link(link1 :: Nil)
        .means("Google Docs")

      val link = Link.createRecord
        .nameGroup("General")
        .linkDetail(linkDetail1 :: Nil)

      val errs = link.validate
      if (errs.length > 1) {
        fail("Validation error: "+errs.mkString(", "))
      }

      link.validate.length should equal (0)

      // save to db
      link.save(false)

      // retrieve from db and compare
      val festivalFromDb = Link.find(link.id.get)
      festivalFromDb.isDefined should equal (true)
      festivalFromDb.map(u => u.id.get should equal (link.id.get))
    }
  }
}
