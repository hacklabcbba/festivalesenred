package code
package snippet

import java.text.SimpleDateFormat
import java.util.Date

import code.config.Site
import code.model.{FileRecord, User}
import code.model.festival._
import com.foursquare.rogue.LatLong
import net.liftmodules.extras.SnippetHelper
import net.liftweb.common._
import net.liftweb.http.js.JsCmds.{Run, RedirectTo, Noop}
import net.liftweb.http.{PaginatorSnippet, SHtml, S}
import net.liftweb.record.Field
import net.liftweb.util._
import net.liftweb.util.Helpers._

import scala.xml.{NodeSeq, Text}
import net.liftweb.util.Mailer._

object FestivalesSnippet extends SnippetHelper with PaginatorSnippet[Festival] {

  override def itemsPerPage = 5

  def filter = {
    val area = S.param("area")
    val tag = S.param("tag")
    val equipment = S.param("equipment")
    val service = S.param("service")
    val training = S.param("training")
    val communication = S.param("communication")
    val partnership = S.param("partnership")
    val networking = S.param("networking")
    (area.toList ++ tag.toList ++ equipment.toList ++ service.toList ++
      training.toList ++ communication.toList ++ partnership.toList ++ networking.toList).headOption
  }

  def head = {
    filter match {
      case Some(filter) => "title *" #> s"[Búsqueda] | Festivales en Red | Telartes > $filter"
      case None => "title *" #> "Festivales en Red | Telartes"
    }
  }

  def title = {
    filter match {
      case Some(filter) => "h1 *" #> s"Festivales en Red: $filter"
      case None => "h1 *" #> "Festivales en Red"
    }
  }

  override def count = {
    val area = S.param("area").flatMap(Area.findByName(_))
    val tag = S.param("tag")
    val equipment = S.param("equipment").flatMap(EquipmentDetail.findByName(_))
    val service = S.param("service").flatMap(ServiceExchange.findByName(_))
    val training = S.param("training").flatMap(TrainingActivity.findByName(_))
    val communication = S.param("communication").flatMap(CommunicationTool.findByName(_))
    val partnership = S.param("partnership").flatMap(Partnership.findByName(_))
    val networking = S.param("networking").flatMap(Network.findByName(_))
    Festival.countFiltered(area, tag, equipment, service, training, communication, partnership, networking)
  }

  def mongoPage = if (curPage < 1) 1 else curPage

  override def page = {
    val area = S.param("area").flatMap(Area.findByName(_))
    val tag = S.param("tag")
    val equipment = S.param("equipment").flatMap(EquipmentDetail.findByName(_))
    val service = S.param("service").flatMap(ServiceExchange.findByName(_))
    val training = S.param("training").flatMap(TrainingActivity.findByName(_))
    val communication = S.param("communication").flatMap(CommunicationTool.findByName(_))
    val partnership = S.param("partnership").flatMap(Partnership.findByName(_))
    val networking = S.param("networking").flatMap(Network.findByName(_))
    Festival.findAllFilteredByPage(itemsPerPage, mongoPage, area, tag, equipment, service, training, communication, partnership, networking)
  }


  def render: CssSel = {
    for {
      user <- User.currentUser
    } yield ({
      "data-name=row *" #> Festival.findAllByUser(user).map(festival => {
        "data-name=name *" #> festival.name.get &
        "data-name=name [href]" #> Site.festival.calcHref(festival) &
        "data-name=logo" #> (festival.logo.get match {
          case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
          case _ => <br />
        }) &
        "data-name=description *" #> festival.presentation.get &
        "data-name=city *" #> festival.city.objs.map(_.name.get).mkString(", ") &
        "data-name=owner *" #> festival.owner.obj.dmap("")(_.name.get) &
        "data-name=responsible *" #> festival.responsible.get &
        "data-name=begins *" #> festival.begins.literalDate &
        "data-name=ends *" #> festival.ends.literalDate &
        "data-name=edit [href]" #> Site.festivalEdit.calcHref(festival)
      }) &
      "data-name=add [href]" #> "/festival-form/new"
    }): CssSel
  }

  def list = {
    "data-name=row *" #> page.map(festival => {
      "data-name=name *" #> festival.name.get &
      "data-name=name [href]" #> Site.festival.calcHref(festival) &
      "data-name=logo" #> (festival.logo.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
      "data-name=description *" #> festival.presentation.get &
      "data-name=city *" #> festival.city.objs.map(_.name.get).mkString(", ") &
      "data-name=owner *" #> festival.owner.obj.dmap("")(_.name.get) &
      "data-name=responsible *" #> festival.responsible.get &
      "data-name=begins *" #> festival.begins.literalDate &
      "data-name=ends *" #> festival.ends.literalDate
    })
  }
}

object FestivalView extends SnippetHelper {

  def title: CssSel = {
    for {
      item <- Site.festival.currentValue ?~ "Opción no válida"
    } yield ("title -*" #> item.name): CssSel
  }

  def mapScript(inst: Festival) = {
    Run("""
      var search = function(data) {
      $.ajax({
        url: "/api/festival/localizations/""" + inst.id.get + """",
        type: 'get',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        success: function(response) {
          vectorSource.clear();
          $.each(response.locs, function(i,e){
            if (e.geoLatLng){
              var marker = new ol.Feature({
                name: e.festivalName + "<br/ ><a href='"+ e.url+"'>Detalles</a>",
                geometry: new ol.geom.Point([e.geoLatLng.lat, e.geoLatLng.long]),
                style: iconStyle
              });
              marker.setStyle(iconStyle);
              marker.on('click', function(){ console.log('over')}, marker);
              vectorSource.addFeature(marker);
            }
          });
        }
      });
      };

      var iconStyle = new ol.style.Style({
      image: new ol.style.Icon(({
        anchor: [0.5, 46],
        anchorXUnits: 'fraction',
        anchorYUnits: 'pixels',
        opacity: 0.75,
        src: '/img/marker-icon.png'
      }))
      });

      var vectorSource = new ol.source.Vector();
      var vectorLayer = new ol.layer.Vector({
      source: vectorSource
      });

      var projection = ol.proj.get('EPSG:900913');
      var projectionExtent = projection.getExtent();
      var size = ol.extent.getWidth(projectionExtent) / 256;
      var resolutions = new Array(26);
      var matrixIds = new Array(26);
      for (var z = 0; z < 26; ++z) {
      // generate resolutions and matrixIds arrays for this WMTS
      resolutions[z] = size / Math.pow(2, z);
      matrixIds[z] = 'EPSG:900913:' + z;
    }

    var map = new ol.Map({
    layers: [
    new ol.layer.Tile({
      source: new ol.source.OSM()
    }),
    vectorLayer
    ],
    renderer: 'canvas',
    target: document.getElementById('map'),
    view: new ol.View({
      center: [-7354864, -1889219],
      zoom: 5
    })
    });

    //Añadimos un control de zoom

    map.addControl(new ol.control.ZoomSlider());

    var element = document.getElementById('popup');

    var popup = new ol.Overlay({
    element: element,
    positioning: 'bottom-center',
    stopEvent: false
    });
    map.addOverlay(popup);

    // display popup on click
    map.on('click', function(evt) {
    console.log("click en el map", evt);

    map.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
      //return feature;
      if (feature) {
        var geometry = feature.getGeometry();
        var coord = geometry.getCoordinates();
        popup.setPosition(coord);
        $(element).popover('destroy');
        $(element).popover({
          placement: 'top',
          html: true,
          content: feature.get('name')
        });
        $(element).popover('show');
      } else {
        $(element).popover('destroy');
      }
    });

    });

    map.on('pointermove', function(e) {
    if (e.dragging) {
      $(element).popover('destroy');
      return;
    }
    var pixel = map.getEventPixel(e.originalEvent);
    var hit = map.hasFeatureAtPixel(pixel);
    map.getTarget().style.cursor = hit ? 'pointer' : '';
    });

    search({});

    """)
  }

  def render: CssSel = {
    for {
      item <- Site.festival.currentValue ?~ "Opción no válida"
    } yield {
      val generalDataFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.logo, s.name, s.responsible, s.productionManagement, s.city, s.places, s.begins, s.ends, s.durationType, s.call, s.areas,
          s.website, s.responsibleEmail, s.pressResponsibleEmail, s.facebookPage, s.twitter, s.skype, s.spaces, s.equipment,
          s.numberOfAttendees, s.publicKind, s.photo1, s.photo2, s.photo3
        ))
      val aboutFields = Site.festivalEdit.currentValue.dmap[List[Field[_, _]]](Nil)(s =>
        List(
          s.staff, s.presentation, s.numberEditions, s.serviceExchange, s.trainingActivity, s.communicationTools,
          s.publicInstitutionPartnerships, s.privateInstitutionPartnerships, s.civilOrganizationPartnerships,
          s.networking, s.minimalBudget, s.budget, s.collaborativeEconomyBudget, s.managementDuration, s.tags
        ))

      S.appendJs(mapScript(item))

      "data-name=name *" #> item.name.get &
      "data-name=begins *" #> item.begins.literalDate &
      "data-name=ends *" #> item.ends.literalDate &
      "data-name=production-manangement *" #> item.productionManagement.get &
      "data-name=place" #> item.productionManagement.get &
      "data-name=cities" #> item.city.objs.map(_.name.get).mkString(", ") &
      "data-name=duration" #> (item.duration.get + " " + item.durationType.get) &
      "data-name=website *" #> item.website.get &
      "data-name=website [href]" #> item.website.get &
      "data-name=facebook [href]" #> s"https://www.facebook.com/${item.facebookPage.get}" &
      "data-name=facebook *" #> item.facebookPage.get &
      "data-name=twitter [href]" #> s"https://twitter.com/${item.twitter.get}" &
      "data-name=skype *" #> item.twitter.get &
      "data-name=skype [href]" #> item.skype.get &
      "data-name=twitter *" #> item.skype.get &
      "data-name=responsible *" #> item.responsible.get &
      "data-name=responsible-email" #> item.responsibleEmail.get.replace("@", "[a]") &
      "data-name=call" #> SHtml.link(s"/service/images/${item.call.get.fileId.get}", () => (), <span>Descargar (hasta el {item.callDate.literalDate})</span>, "class" -> item.callDate.css) &
      "data-name=areas" #> item.areas.objs.zipWithIndex.map(s => <a href={s"/festivales?area=${s._1.name.get}"} itemprop="url">{s._1.name.get}</a> ++ (if (s._2 == item.areas.objs.size - 1) Text("") else Text(", "))) &
      "data-name=tags" #> item.tags.get.zipWithIndex.map(s => <a href={s"/festivales?tag=${s._1.tag.get}"} itemprop="url">{s._1.tag.get}</a> ++ (if (s._2 == item.areas.objs.size - 1) Text("") else Text(", "))) &
      "data-name=description *" #> item.presentation.get &
      "data-name=equipment" #> item.equipment.objs.map(s => "a" #> <a href={s"/festivales?equipment=${s.name.get}"} itemprop="url"><i class="fa fa-check"></i> {s.name.get}</a>) &
      "data-name=team" #> item.staff.get.map(tm => {
        "data-name=name *+" #> tm.name.get &
        "data-name=role *+" #> tm.role.get &
        "data-name=email *+" #> tm.email.get &
        "data-name=cellphone *+" #> tm.cellphone.get
      }) &
      "data-name=item-exchange" #> item.serviceExchange.objs.map(s => "a" #> <a href={s"/festivales?service=${s.name.get}"}><i class="fa fa-check"></i> {s.name.get}</a>) &
      "data-name=item-training" #> item.trainingActivity.objs.map(s => "a" #> <a href={s"/festivales?training=${s.name.get}"} ><i class="fa fa-check"></i> {s.name.get}</a>) &
      "data-name=item-tools" #> item.communicationTools.objs.map(s => "a" #> <a href={s"/festivales?communication=${s.name.get}"} ><i class="fa fa-check"></i> {s.name.get}</a>) &
      "data-name=item-public" #> item.publicInstitutionPartnerships.objs.map(s => "a" #> <a href={s"/festivales?partnership=${s.name.get}"} >{s.name.get}</a>) &
      "data-name=item-private" #> item.privateInstitutionPartnerships.objs.map(s => "a" #> <a href={s"/festivales?partnership=${s.name.get}"} >{s.name.get}</a>) &
      "data-name=item-civil" #> item.civilOrganizationPartnerships.objs.map(s => "a" #> <a href={s"/festivales?partnership=${s.name.get}"} >{s.name.get}</a>) &
      "data-name=item-networking" #> item.networking.objs.map(s => "a" #> <a href={s"/festivales?networking=${s.name.get}"} ><i class="fa fa-check"></i> {s.name.get}</a>) &
      "data-name=minimal-budget *" #> item.minimalBudget.toString &
      "data-name=budget *" #> item.budget.toString &
      "data-name=colaborative-budget *" #> item.collaborativeEconomyBudget.toString &
      "data-name=logo-link [href]" #> (item.logo.get match {
        case p: FileRecord => "/service/images/"+ p.fileId.get
        case _ => "#"
      }) &
      "data-name=logo" #> (item.logo.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
      "data-name=photo1-link [href]" #> (item.photo1.get match {
        case p: FileRecord => "/service/images/"+ p.fileId.get
        case _ => "#"
      }) &
      "data-name=photo1" #> (item.photo1.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
      "data-name=photo2-link [href]" #> (item.photo2.get match {
        case p: FileRecord => "/service/images/"+ p.fileId.get
        case _ => "#"
      }) &
      "data-name=photo2" #> (item.photo2.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      }) &
      "data-name=photo3-link [href]" #> (item.photo3.get match {
        case p: FileRecord => "/service/images/"+ p.fileId.get
        case _ => "#"
      }) &
      "data-name=photo3" #> (item.photo3.get match {
        case p: FileRecord => <img src={"/service/images/"+ p.fileId.get} />
        case _ => <br />
      })
    }: CssSel
  }
}