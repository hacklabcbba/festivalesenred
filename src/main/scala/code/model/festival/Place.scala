package code
package model
package festival

import code.config.Site
import code.lib.field.{SingleComboBoxField, DatepickerField}
import com.foursquare.rogue.LatLong
import net.liftweb.common.Full
import net.liftweb.http.{SHtml, S}
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.json.JsonAST.{JObject, JValue}
import net.liftweb.mongodb.record.field.{MongoCaseClassField, ObjectIdRefField, DateField}
import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.StringField
import net.liftweb.util.Helpers
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import net.liftweb.util.Helpers._

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

		override def defaultValue = new LatLong(-7654865, -1889233)

		def scriptMovableFeature = Run( """

				/**
				 * Define a namespace for the application.
				 */

				window.app = {};
				var app = window.app;

				/**
				 * @constructor
				 * @extends {ol.interaction.Pointer}
				 */
				app.Drag = function() {

				  ol.interaction.Pointer.call(this, {
				    handleDownEvent: app.Drag.prototype.handleDownEvent,
				    handleDragEvent: app.Drag.prototype.handleDragEvent,
				    handleMoveEvent: app.Drag.prototype.handleMoveEvent,
				    handleUpEvent: app.Drag.prototype.handleUpEvent
				  });

				  /**
				   * @type {ol.Pixel}
				   * @private
				   */
				  this.coordinate_ = null;

				  /**
				   * @type {string undefined}
				   * @private
				   */
				  this.cursor_ = 'pointer';

				  /**
				   * @type {ol.Feature}
				   * @private
				   */
				  this.feature_ = null;

				  /**
				   * @type {string undefined}
				   * @private
				   */
				  this.previousCursor_ = undefined;

				};
				ol.inherits(app.Drag, ol.interaction.Pointer);


				/**
				 * @param {ol.MapBrowserEvent} evt Map browser event.
				 * @return {boolean} `true` to start the drag sequence.
				 */
				app.Drag.prototype.handleDownEvent = function(evt) {
				  var map = evt.map;

				  var feature = map.forEachFeatureAtPixel(evt.pixel,
				      function(feature, layer) {
				        return feature;
				      });

				  if (feature) {
				    this.coordinate_ = evt.coordinate;
				    this.feature_ = feature;
				  }

				  return !!feature;
				};


				/**
				 * @param {ol.MapBrowserEvent} evt Map browser event.
				 */
				app.Drag.prototype.handleDragEvent = function(evt) {
				  var map = evt.map;

				  var feature = map.forEachFeatureAtPixel(evt.pixel,
				      function(feature, layer) {
				        return feature;
				      });

				  var deltaX = evt.coordinate[0] - this.coordinate_[0];
				  var deltaY = evt.coordinate[1] - this.coordinate_[1];

				  var geometry =  (this.feature_.getGeometry());
				  geometry.translate(deltaX, deltaY);

				  this.coordinate_[0] = evt.coordinate[0];
				  this.coordinate_[1] = evt.coordinate[1];
					setPosition(this.coordinate_[0], this.coordinate_[1])
				};


				/**
				 * @param {ol.MapBrowserEvent} evt Event.
				 */
				app.Drag.prototype.handleMoveEvent = function(evt) {
				  if (this.cursor_) {
				    var map = evt.map;
				    var feature = map.forEachFeatureAtPixel(evt.pixel,
				        function(feature, layer) {
				          return feature;
				        });
				    var element = evt.map.getTargetElement();
				    if (feature) {
				      if (element.style.cursor != this.cursor_) {
				        this.previousCursor_ = element.style.cursor;
				        element.style.cursor = this.cursor_;
				      }
				    } else if (this.previousCursor_ !== undefined) {
				      element.style.cursor = this.previousCursor_;
				      this.previousCursor_ = undefined;
				    }
				  }
				};


				/**
				 * @param {ol.MapBrowserEvent} evt Map browser event.
				 * @return {boolean} `false` to stop the drag sequence.
				 */
				app.Drag.prototype.handleUpEvent = function(evt) {
				  this.coordinate_ = null;
				  this.feature_ = null;
				  return false;
				};

  			console.log("se cargo el script movible")

				setPosition = function(lat, long){
    			console.log("moviendo: ", lat, long )
       		var value = {lat:lat, long: long};
       		$("#latLongField").val(JSON.stringify(value));
				}

        var iconFeature = new ol.Feature({
        	  geometry: new ol.geom.Point(["""+ this.get.lat + """, """+ this.get.long +"""]),
        	  name: '""" + name.toString +"""'
        	});

				var iconStyle = new ol.style.Style({
					image: new ol.style.Icon( ({
					anchor: [0.5, 46],
					anchorXUnits: 'fraction',
					anchorYUnits: 'pixels',
					opacity: 0.75,
					src: '/img/marker-icon.png'
					}))
				});

        	iconFeature.setStyle(iconStyle);

        	var vectorSource = new ol.source.Vector({
        	  features: [iconFeature]
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
         		 interactions: ol.interaction.defaults().extend([new app.Drag()]),
        	   layers: [
        	   		new ol.layer.Tile({ source: new ol.source.OSM() }),
        		 		vectorLayer
        	   ],
        	   renderer: 'canvas',
        	   target: document.getElementById('""" + mapId + """'),
        	   view: new ol.View({
        		 center: ["""+ this.get.lat +""", """+ this.get.long + """],
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
			//this.set(position)
			S.appendJs(scriptMovableFeature)
			val node =
				<div>
					<div id={mapId} style="height:200px;width:100%;"></div>
					{/*SHtml.hidden(s => setLatLong(s), compactRender(this.asJValue), "id" -> "latLongField")*/}
					{SHtml.hidden(s => setLatLong(s), "{}", "id" -> "latLongField")}
				</div>
			Full(node)
    }

		def setLatLong(in: String) = {
			for{
				latLong <- tryo((parse(in)).extract[LatLong])
			}
			this.set(latLong)
			println("NEW VALS:"+this.get)
		}
	}
}

object Place extends Place with BsonMetaRecord[Place] {
	def asJValue(inst: Place, festival: Festival): JObject = {
    ("festivalName" -> festival.name.asJValue) ~
		("url" -> Site.festival.calcHref(festival)) ~
		super.asJValue(inst)
	}
}