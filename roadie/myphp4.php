	<!DOCTYPE html >
	<head>
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<title>PHP/MySQL & Google Maps Example</title>
		<script src="https://maps.googleapis.com/maps/api/js?v=3&sensor=false&libraries=geometry"></script>
		<script type="text/javascript", >
	    //<![CDATA[

	    var goodToGo = false;
	    var Colors = [
	    "#FFFFFF",
	    "#000000"
	    ];

	    var Colors2 = [
	    "#000000", 
	    "#FF0000", 
	    "#00FF00", 
	    "#0000FF",                                                                                
	    "#FFFF00",
	    "#00FFFF",
	    "#FF00FF"
	    ];

	    var ColorsName = [
	    "RED",
	    "GREEN",
	    "ORANGE",
	    ]



	    var customIcons = {
	    	restaurant: {
	    		icon:{
	    			url: 'http://labs.google.com/ridefinder/images/mm_20_blue.png',
	    			scaledSize: new google.maps.Size(0.1,0.1),
	    		} 
	    	},
	    	bar: {
	    		icon: { 
	    			url:'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/geolocationmarker/images/gpsloc.png',
	    			scaledSize: new google.maps.Size(3,3),
	    		}
	    	}
	    };

	  var MIN_DISTANCE = 20;    //DISTANCIA MINIMA QUE OS PONTOS DEVE ESTAR UM DOS OUTROS
	  var CURVE_WNDW = 3;     //TAMANHO DA JANELA PARA SER CONSIDERADO UMA CURVA
	  var MAX_DISTANCE = 20;    //TAMANHO MAXIMO QUE UM CAMINHO PODE TER
	  var STARTER_ZOOM = 20;	//Zoom que a tela se inicia
	  var MIN_ZOOM	= 12; 		//Zoom minimo para mostrar retas
	  var LENGTH_ADJUST = 5000; //VALOR PARA DETERMINAR COMPRIMENTO DAS RETAS PERPENDICULARS
	  							 // MENOS = RETANGULOS MAIORES
	  							 var MIN_DIF_TIMES = 5	;
	  							 var MAP_INCREASEMENT = 0.00000;							 

	  							 //var markers = new Array();
	  							 var markers = new Array();
	  							 var polylineList = [];
	  							 var rectangleList = [];
	  							 var polygonCoords = [];
	  							 var ids = [];
	  							 var AllPathList = [];
	  							 var ActualPathList = [];
	  							 var IRIs = [];
	  							 var IRIpath = [];
	  							 var IRIall = [];
	  							 var Path = [];
	  							 var infoWindow = new google.maps.InfoWindow();
	  							 var control = 0;
	  							 var markerslength;
	  							 var lineNumber = 0;
	  							 var xml;


	  							 function load() {

	       //QUANTIDADE QUE VAI SER AUMENTADO NAS BORDAS DO MAPA

	       var map = new google.maps.Map(document.getElementById("map"), {
	       	center: new google.maps.LatLng(-19.835769958578826 , -43.16760063171387),
	       	zoom: STARTER_ZOOM,
	       	mapTypeId: 'roadmap'
	       });
	       var latlngbounds = new google.maps.LatLngBounds();

	      // Change this depending on the name of your PHP file

	      google.maps.event.addListener(map, 'idle', function(){
	      	var lat_lng = new Array();
	      	lineNumber = 0;

	      	if(markers.length > 0){
	      		deleteOverlays(map);
	      		markers = new Array();
	      	}

	        if(map.getZoom() > MIN_ZOOM){  //So será exibido caso zoom seja maior que 18

	          bounds = this.getBounds(); //Le bordas do mapa

	          var S = bounds.getSouthWest().lat();
	          var E = bounds.getSouthWest().lng();
	          var N = bounds.getNorthEast().lat();
	          var W = bounds.getNorthEast().lng();

	          S = S - MAP_INCREASEMENT;
	          E = E - MAP_INCREASEMENT;
	          N = N + MAP_INCREASEMENT;
	          W = W + MAP_INCREASEMENT;

	          bounds = new google.maps.LatLngBounds(
	          	new google.maps.LatLng(S, E),
	          	new google.maps.LatLng(N, W)
	          	);

	          console.log("SW: " + bounds.getSouthWest());
	          console.log("NE: " + bounds.getNorthEast());

	          downloadUrl("phpsqlajax_genxml2.php?SWlat=" + S + "&SWlng=" + E + "&NElat=" + N + "&NElng=" + W + "", function(data) {

	          	xml = data.responseXML;
	          	markers = xml.documentElement.getElementsByTagName("marker");
	          	console.log("markerslength: " + markers.length);
	          });
	          drawPolylines(map);
	      }else{
	      	console.log("Aumente o zoom!");
	      }
	  });

}


function drawPolylines(map){
	var distance, totaldistance = 0;
	for(var i = 0; i < markers.length-1; i++){
		console.log("i: " + i);

		    var bool = markers[i].getAttribute('read');     //Confere se ponto ja foi lido
		    var bool2 = markers[i+1].getAttribute('read');  //Confere se proximo ponto ja foi lido

		    if(bool == 'false' && bool2 == 'false'){
		    	distance = getDistance(markers[i], markers[i+1]);
		    	if(distance < MIN_DISTANCE){
		    		draw(i, (i+1), map);
		    		markers[i].parentNode.removeChild(markers[i]);
		    		i--;
		    	}else{
		    		console.log("pontos distantes");
		    		markers[i].setAttribute('read', 'true');
		      }//Distancia entre pontos muito grande (novo segmento)
		    }else{console.log("pontos ja lidos")}//Pontos ja lidos
		}

		for(var i = 0; i < markers.length; i++){
			if(markers[i].getAttribute('flag') == 4){
				var circle = makeCircle(i);
				var points = searchWithinCircle(circle, i);
				console.log("length: " + points.length);
				if(points.length >= 2){
					circle.setOptions({radius: 3, fillColor:'#000000', strokeColor:'#000000'})
					circle.setMap(map);
				}
				
			}
		}
	}



	function draw(begin, end,  map){
		if(begin != end){
			console.log("DRAWing linha nº " + lineNumber);

			//console.log("begin: " + begin);
			//console.log("end: " + end);

			var beginmarker = newPoint(markers[begin]);
			var endmarker = newPoint(markers[end]);
			var polygon = perpendicularVetor(begin, end, map);
			var points, manyDifferentTimes, holePoints, stdvMean;
			var beginflag = parseInt(markers[begin].getAttribute('flag'));

			points = searchWithinSquare(polygon, begin, end);

			if(points.length > 1){
				manyDifferentTimes = howManyPaths(points);
				flagMean = getMean2(points);
				cleanData(points, flagMean);
			}else{

				flagMean = parseFloat(points[0].getAttribute("flag"));
				var stack = points[0].getAttribute("stack");
				if(stack == ""){
					manyDifferentTimes = 1;
				}else{
					manyDifferentTimes = parseInt(stack);
				}

				console.log("length: " + points.length);
			}


			console.log("flagMean: " + flagMean);


			if(manyDifferentTimes >= MIN_DIF_TIMES){
				Path = new google.maps.Polyline({                                                                                               
					path: [beginmarker, endmarker],
			      strokeColor: ColorsName[1],  //verde
			      strokeOpacity: 0.5,
			      strokeWeight: 6,
			      map: map
			  });
				polylineList.push(Path);



				IRIColorDraw(Path, Math.round(flagMean), manyDifferentTimes, map);
				console.log("---------------------");
				lineNumber++;
			}else{

				console.log("poca gente     :(  " + manyDifferentTimes);
			}
		}
	}
	// ############################################################# //

	function cleanData(points, flagMean){
		console.log("id: " + points[0].getAttribute('id'));

		var id = points[0].getAttribute('id');
		var query , i;


		query = "";
		id = points[1].getAttribute('id');

		var query = "execSQL.php?query=";
		query = query + "DELETE FROM `pavement` WHERE id IN (" + id;
			for (i = 2 ; i < points.length; i++){
				id = points[i].getAttribute('id');
				query = query + ", " + id;
			}
			query = query + ")"
	downloadUrl2(query);

	console.log("query3: " + query);

}

	// ############################################################# //

	function selectionSort(sortMe){
		var i, j, tmp, tmp2, compara;
		for (i = 1; i < sortMe.length - 1; i++)
		{
			tmp = i;
			compara = i-1;
			for (j = i + 1; j < sortMe.length; j++){
				if (getDistance(sortMe[compara], sortMe[j]) < getDistance(sortMe[compara], sortMe[tmp])){
					tmp = j;
				}
			}
			if(tmp!=i){
				tmp2 = sortMe[tmp];
				sortMe[tmp] = sortMe[i];
				sortMe[i] = tmp2;
			}
		}

		return sortMe;
	}

	function rad (x) {
		return x * Math.PI / 180;
	}

	function getDistance (p1, p2) {
		p1 = newPoint(p1);
		p2 = newPoint(p2);

	  var R = 6378137; // Earth’s mean radius in meter
	  var dLat = rad(p2.lat() - p1.lat());
	  var dLong = rad(p2.lng() - p1.lng());
	  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
	  Math.cos(rad(p1.lat())) * Math.cos(rad(p2.lat())) *
	  Math.sin(dLong / 2) * Math.sin(dLong / 2);
	  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	  var d = R * c;
	  return d; // returns the distance in meter
	}

	// ############################################################# //

	function deleteOverlays(map){
	  //console.log("polylineList.length " + polylineList.length);
	  for (var i = 0; i < polylineList.length ; i++){
	  	polylineList[i].setMap(null);
	  }

	  for (var i = 0; i < rectangleList.length ; i++){
	  	rectangleList[i].setMap(null);
	  }

	  polylineList = [];
	  AllPathList = [];
	  IRIs = [];
	  IRIall = [];
	}
	// ############################################################# //

	function getAngCoef(p1, p2){
		if(p1 != null & p2 != null){
			p1 = newPoint(p1);
			p2 = newPoint(p2);

			if(p1 != null && p2 != null){
				var y1 = p1.lat();
				var x1 = p1.lng();
				var y2 = p2.lat();
				var x2 = p2.lng();

				return ((y2 - y1) / (x2 - x1));
			}
		}
	}


	function makeCircle(begin){
		var p1 = newPoint(markers[begin]);
		var circle = new google.maps.Circle({
			center: p1,
			radius: 5,
			strokeColor: '#FF0000',
			strokeOpacity: 0.8,
			strokeWeight: 2,
			fillColor: '#FF0000',
			fillOpacity: 0.35
		});

		//circle.setMap(map);
		rectangleList.push(circle);
		return circle;
	}

	// ############################################################# //
	function perpendicularVetor(begin, end, map){


		var p1 = newPoint(markers[begin]);
		var p2 = newPoint(markers[end]);
	    //var p1 = lat_lng[beginlist[i]];
	    //var p2 = lat_lng[endlist[i]];

	    //PEGA X E Y DE CADA UM DOS PONTOS
	    var x1 = p1.lat();
	    var x2 = p2.lat();
	    var y1 = p1.lng();
	    var y2 = p2.lng();

	    //FAZ A SUBTRACAO DE P2 - P1 (transladar para o 0)
	    var dx = x2 - x1;
	    var dy = y2 - y1;
	    
	    //CALCULA A NORMA
	    var norma = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));

	    //ENCONTRA VETOR UNITARIO / 20000 (vetor unitario é muito grande na representacao do mapa)
	    dx = parseFloat(dx/(norma*LENGTH_ADJUST))
	    dy = parseFloat(dy/(norma*LENGTH_ADJUST));

	    //ENCONTRA O VETOR PERPENDICULAR
	    var newy = 1*dx;
	    var newx = -1*dy;

	    //ENCONTRA O PRIMEIRO PONTO DO RETANGULO  
	    var tempx = x1-newx-0.00000;
	    var tempy = y1-newy-0.00000;
	    var point = new google.maps.LatLng(tempx,tempy);

	    tempx = x1+newx;
	    tempy = y1+newy;
	    var point2 = new google.maps.LatLng(tempx, tempy);

	    tempx = x2-newx;
	    tempy = y2-newy;
	    var point3 = new google.maps.LatLng(tempx, tempy);

	    tempx = x2+newx+0.00000;
	    tempy = y2+newy+0.00000;
	    var point4 = new google.maps.LatLng(tempx, tempy);

	    //var distance = getDistance(markers[beginlist[i]], point);
	    //var distance = getDistance(markers[0], point);
	    //console.log("distance " + distance);

	      // Define the LatLng coordinates for the polygon's path.
	      polygonCoords = [point , point2, point4, point3, point];

	  // Construct the polygon.
	  polygon = new google.maps.Polygon({
	  	paths: polygonCoords,
	  	strokeColor: '#FF0000',
	  	strokeOpacity: 0.8,
	  	strokeWeight: 2,
	  	fillColor: '#FF0000',
	  	fillOpacity: 0.35
	  });

	  //polygon.setMap(map);
	  rectangleList.push(polygon);

	  /*var rectangle = new google.maps.Rectangle({
	    strokeColor: '#FF0000',
	    strokeOpacity: 0.8,
	    strokeWeight: 2,
	    fillColor: '#FF0000',
	    fillOpacity: 0.35,
	    map: map,
	    bounds: new google.maps.LatLngBounds(p1, p2)
	  });


	rectangleList.push(rectangle);*/

	    /*Path = new google.maps.Polyline({                                                                                               
	      path: [newPoint(markers[begin]), point],
	      strokeColor: Colors2[0],  //verde
	      strokeOpacity: 0.5,
	      strokeWeight: 6,
	      map: map
	    });
	    polylineList.push(Path);

	    Path = new google.maps.Polyline({                                                                                               
	      path: [newPoint(markers[end]), point4],
	      strokeColor: Colors2[0],  //verde
	      strokeOpacity: 0.5,
	      stnullrokeWeight: 6,
	      map: map
	    });
	polylineList.push(Path)*/

	return polygon;
}
	// ############################################################# //
	function IRIColorDraw(Path, IRIMean, manyDifferentUsers, map){
		var GOOD_IRI = 5;
		var BAD_IRI = 8;
		var quality;

		if(map.getZoom() <= 15){
			Path.setOptions({strokeWeight: 1})
		}else if(map.getZoom() == 16){
			Path.setOptions({strokeWeight: 4})
		}else if(map.getZoom() == 17){
			Path.setOptions({strokeWeight: 6})		
		}else if(map.getZoom() == 18){
			Path.setOptions({strokeWeight: 8})		
		}else if(map.getZoom() == 19){
			Path.setOptions({strokeWeight: 10})		
		}else if(map.getZoom() == 20){
			Path.setOptions({strokeWeight: 12})		
		}



		if(IRIMean == 0){
			Path.setOptions({strokeColor: "GREEN"});
			quality = "Otimo";
		}else if(IRIMean == 1){
			Path.setOptions({strokeColor: "YELLOW"});
			quality = "Normal";
		}else if(IRIMean == 2){
			Path.setOptions({strokeColor: "ORANGE"});
			quality = "Ruim";
		}else if(IRIMean == 3){
			Path.setOptions({strokeColor: "RED"});
			quality = "Horrivel";
		}

		console.log("Quality: " + quality);

		setInfoWindow(map, quality, Path, infoWindow, manyDifferentUsers);
	}

	// ############################################################# //
	function searchWithinCircle(circle, begin){
		var points = [];
		points.push(markers[begin]);
		markers[begin].parentNode.removeChild(markers[begin]);
		for(var i = 0; i < markers.length; i++){
			var flag = markers[i].getAttribute('flag');
			if(flag == 4){
				if (google.maps.geometry.spherical.computeDistanceBetween(newPoint(markers[i]), circle.getCenter()) <= circle.getRadius()){
					points.push(markers[i]);
					markers[i].parentNode.removeChild(markers[i]);
				}
			}
		}
		return points;
	}
	// ############################################################# //

	function searchWithinSquare(square, begin, end){
		var points = [];
		var brng = parseFloat(markers[begin].getAttribute('brng'));

		var minimum;
		var maximum;
		var nearzero;

		if(brng < 45){
			nearzero = true;
			maximum = brng + 45;
			minimum = brng - 45 + 360;
		}else if(brng > 315){
			nearzero = true;
			maximum = brng + 45 - 360;
			minimum = brng - 45;

		}else{
			nearzero = false;
			minimum = brng - 45;
			maximum = brng + 45;
		}

		points.push(markers[begin]);


		markers[begin].setAttribute('read', 'true');
		markers[end].setAttribute('read', 'true');

		var endmarkerid = markers[end].getAttribute('id');

		console.log("markerslength: " + markers.length);
		for(var i = 0; i < markers.length; i++){

			var bool =  markers[i].getAttribute('read');

			var actualbrng = parseFloat(markers[i].getAttribute('brng'));
			if(bool == 'false'){
				if((nearzero == false && actualbrng < maximum && actualbrng > minimum) || (nearzero == true && (actualbrng < maximum || actualbrng > minimum))){
					if(google.maps.geometry.poly.containsLocation(newPoint(markers[i]), square)){

						markers[i].setAttribute('read', 'true');
						points.push(markers[i]);

						if(markers[i].getAttribute('flag') != 4){
							markers[i].parentNode.removeChild(markers[i]);
						}
					}
				}
			}else if(markers[i].getAttribute('id') == endmarkerid){
				markers[i].setAttribute('read', 'false');
			}
		}



		return points;
	}
	// ############################################################# //
	function howManyPaths(points){

		var previd = points[0].getAttribute('id');
		var stack = points[0].getAttribute('stack');
		var actualid;
		var manyDifferentTimes;
		if(stack == ""){
			manyDifferentTimes = 1;
		}else{
			manyDifferentTimes = parseInt(stack);
		}
		
		manyDifferentTimes = manyDifferentTimes + points.length;

		/*for(var i = 1; i < points.length; i++){
			actualid = points[i].getAttribute('id');
			var temp = previd+1;
			if(actualid != temp){
				manyDifferentTimes++;
			}
			previd = actualid;
		}*/


		return manyDifferentTimes;
	}


	// ############################################################# //
	function setInfoWindow(map, quality, thisPath, infoWindow, manyDifferentUsers){
		var toolTip = '<div id="map-box">'+
		'<h1 id="firstHeading" class="firstHeading">'+ 'RoadScan' +'</h1>'+
		'<div id="bodyContent">'+
		'<p>Qualidade: '+ quality +'</p>'+
		'<p>Avaliado ' + manyDifferentUsers + ' vez(es)</p>' +
		'<p>Reta nº ' + lineNumber + '</p>' +
		'</div>'+
		'<p>Fidelidade: </p>' +
		'</div>';

		if(manyDifferentUsers >= MIN_DIF_TIMES && manyDifferentUsers <= 10)  {
			toolTip = toolTip + 
			'<img src="lilgoldstar.png">';
		}else if(manyDifferentUsers > 10 && manyDifferentUsers <= 30){
			toolTip = toolTip + 
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">';
		}else if(manyDifferentUsers > 30){
			toolTip = toolTip + 
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">' +
			'<img src="lilgoldstar.png">';
		}

		bindInfoWindow(thisPath, map, infoWindow, toolTip);
	}

	// ############################################################# //

	function getMean2(points){

		var sum = 0;
		var stack = points[0].getAttribute("stack");
		var flag = parseFloat(points[0].getAttribute("flag"));

		if(stack == ""){
			stack = 1;
		}else{
			stack = parseInt(stack);
 		}

		sum = stack * flag;


		for(var i = 1; i < points.length; i++){
			flag = parseFloat(points[i].getAttribute("flag"));
			if(flag != 4){
				sum = sum + flag;
			}
		}

		console.log("STACK = " + stack);
		var finalValue = sum / (stack + points.length);




 		var id = parseInt(points[0].getAttribute("id"));

 		var query = "execSQL.php?query=";
 		query = query + "UPDATE `pavement` SET stackCount= " + (((points.length) + stack)) + " WHERE `id` =" + id + ";";
 		downloadUrl2(query);


 		var query = "execSQL.php?query=";
 		query = query + "UPDATE `pavement` SET flag= " + finalValue + " WHERE `id` =" + id + ";";
 		downloadUrl2(query);

 		return finalValue;
 	}


	// ############################################################# //

	function getMean(points){
		var sum = 0;
		for(var i = 0; i < points.length; i++){
			sum = sum + parseFloat(points[i].getAttribute("IRI"));
		}
		var mean = parseFloat(sum/(parseInt(points.length)));
		return mean;
	}

	// ############################################################# //

	function bindInfoWindow(marker, map, infoWindow, html) {
		google.maps.event.addListener(marker, 'click', function(e) {
			infoWindow.setContent(html);  
			infoWindow.setOptions({disableAutoPan: true});
			infoWindow.setPosition(e.latLng);
			infoWindow.open(map, marker);
			return true;
		});
	}
	// ############################################################# //

	function downloadUrl(url, callback) {
		var request = window.ActiveXObject ? new ActiveXObject('Microsoft.XMLHTTP') : new XMLHttpRequest;
		request.onreadystatechange = function() {
			if (request.readyState == 4) {
				request.onreadystatechange = doNothing;
				callback(request, request.status);
			}
		};
		request.open('GET', url, false);
		request.send(null);
	}

	// ############################################################# //

	function downloadUrl2(url) {
		var request = window.ActiveXObject ? new ActiveXObject('Microsoft.XMLHTTP') : new XMLHttpRequest;
		request.onreadystatechange = function() {
			if (request.readyState == 4) {
				request.onreadystatechange = doNothing;
				//callback(request, request.status);
			}
		};
		request.open('GET', url, true);
		//request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		request.send(null);
	}

	// ############################################################# //

	function newPoint(marker){
		return (new google.maps.LatLng(getLat(marker), getLng(marker)));
	}
	// ############################################################# //
	function getLat(marker){
		return parseFloat(marker.getAttribute("lat"));
	}

	// ############################################################# //
	function getLng(marker){
		return parseFloat(marker.getAttribute("lng"));
	}
	// ############################################################# //
	function getIRI(marker){
		return parseFloat(marker.getAttribute("IRI"));
	}


	// ############################################################# //

	function doNothing() {}

	// ############################################################# //
	    //]]>

	</script>

</head>

<body onload="load()">
	<div id="map" style="width: 1900px; height: 1000px"></div>
</body>

</html>