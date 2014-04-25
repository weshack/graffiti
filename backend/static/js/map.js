var markers = {};
var mapLatLngs = {};
var infowindows = {};

$(document).ready(function() {
  google.maps.event.addDomListener(window, 'load', initialize);
});
initialize = function() {
  map = new google.maps.Map(document.getElementById('map-canvas'), {
    zoom: 19,
    center: new google.maps.LatLng(41.5544318, -72.65528429999999),
    mapTypeId: google.maps.MapTypeId.SATELLITE
  });

  google.maps.event.addListener(map, 'dragend', loadImages);
};
loadImages = function() {
  //map.panTo(marker.getPosition());
  var pos = map.getCenter();
  $.getJSON('/images/' + pos.lat() + '/' + pos.lng(), function(data) {
    handleResponse(data);
  });
};

//handleResponse([{url:'1234', latitude: 41.5544318, longitude:-72.65528429999999, timestamp:1231234}])
handleResponse = function(data) {
  data.forEach(function(image) {
    mapLatLngs[image.url] = new google.maps.LatLng(image.latitude, image.longitude);

    if(typeof markers[image.url] === 'undefined') {
      markers[image.url] = new google.maps.Marker({
          position: mapLatLngs[image.url],
          //icon: getIcon(true),
          map: map,
          title: image.url
      });
      var url = '/static/graffiti/' + image.url+ '.png';
      infowindows[image.url] = new google.maps.InfoWindow({
        content: '<a href="'+url+'" target="_blank"><img src="'+url+'"></a>'
      });

      google.maps.event.addListener(markers[image.url], 'click', function () {
          for (url in infowindows) {
              infowindows[url].close();
          }
          infowindows[image.url].open(map, markers[image.url]);
      });
    }
  })
};