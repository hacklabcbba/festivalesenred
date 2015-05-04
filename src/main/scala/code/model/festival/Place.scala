package code
package model
package festival

import code.config.Site
import code.lib.field.{SingleComboBoxField, DatepickerField}
import com.foursquare.rogue.LatLong
import net.liftweb.common.Full
import net.liftweb.http.S
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField
import net.liftweb.util.Helpers
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

class Place extends BsonRecord[Place] {

  override def meta = Place

  object name extends StringField(this, 300) {
    override def displayName = "Nombre"
  }
  object city extends SingleComboBoxField(this, City) {
    override def displayName = "Ciudad"
    def toString(in: City) = s"${in.name.get}/${in.country.get}"
    val placeholder = "Seleccione la ciudad a la que pertenece el lugar"
  }
  object date extends DatepickerField(this) {
    override def displayName = "Fecha"
  }
  object geoLatLng extends MongoCaseClassField[Place, LatLong](this) {
    override def displayName = "Ubicación"

    lazy val mapId = Helpers.nextFuncName

    def script = Run(
      """
         console.log("volvio a generar!: ", $('#""" + mapId + """'))

        var iconFeature = new ol.Feature({
        	  geometry: new ol.geom.Point([-7354864, -1889219]),
        	  name: 'Festival 1'
        	});
        
        	var iconFeature2 = new ol.Feature({
        	  geometry: new ol.geom.Point([-7654865, -1889233]),
        	  name: 'Festival 2'
        	});
        
        	var iconStyle = new ol.style.Style({
        	  image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
        		anchor: [0.5, 46],
        		anchorXUnits: 'fraction',
        		anchorYUnits: 'pixels',
        		opacity: 0.75,
        		src: '/img/marker-icon.png'
        	  }))
        	});
        
        
        	iconFeature.setStyle(iconStyle);
        	iconFeature2.setStyle(iconStyle);
        
        	var vectorSource = new ol.source.Vector({
        	  features: [iconFeature, iconFeature2]
        	});
        
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
        	   target: document.getElementById('""" + mapId + """'),
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
        	  var feature = map.forEachFeatureAtPixel(evt.pixel,
        		  function(feature, layer) {
        			return feature;
        		  });
        	  if (feature) {
        		var geometry = feature.getGeometry();
        		var coord = geometry.getCoordinates();
        		popup.setPosition(coord);
        		$(element).popover('destroy');
        		$(element).popover({
        		  'placement': 'top',
        		  'html': true,
        		  'content': feature.get('name')
        		});
        		$(element).popover('show');
        	  } else {
        		$(element).popover('destroy');
        	  }
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
          $('#""" + mapId + """').data('map', map);
          $(document).on('opened.fndtn.reveal', '[data-reveal]', function () {
          	if ( $('#""" + mapId + """').data('map') ){
            	$('#""" + mapId + """').data('map').updateSize();
						}else{
            	console.log('map in data no exist')
						}
          });
      """.stripMargin
    )

    override def toForm = {
      S.appendJs(script)
      val node =
        <div id={mapId} style="height:200px;width:100%;">
        </div>
      Full(node)
    }
  }
}

object Place extends Place with BsonMetaRecord[Place] {
	def asJValue(inst: Place, festival: Festival): JObject = {
		("url" -> Site.festival.calcHref(festival)) ~
		super.asJValue(inst)
	}
}