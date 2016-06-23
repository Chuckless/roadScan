<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */


/*define('DB_USER', "root"); // db user
define('DB_PASSWORD', ""); // db password (mention your db password here)
define('DB_DATABASE', "RoadieDB"); // database name
define('DB_SERVER', "localhost"); // db server*/

define('DB_USER', "lucas"); // db user
define('DB_PASSWORD', "123456"); // db password (mention your db password here)
define('DB_DATABASE', "RoadieDB"); // database name
define('DB_SERVER', "localhost"); // db server

// array for JSON response
$response = array();
$response["success"] = 1;
$response["message"] = "Default Message.";
// check for required fields
$data = file_get_contents('php://input');
//$data = $_POST['pavement'];
$data = json_decode($data, true);
//$data = $data['pavement'];
//echo json_encode($data['pavement'][0]["IRI"]);


//echo json_encode($data);

if ($data) {


	//require_once __DIR__ . '/db_connect.php';
	//$con = new DB_CONNECT();
	//$con = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE) or die(mysql_error());
	$con = mysql_pconnect(DB_SERVER , DB_USER, DB_PASSWORD);
	$selected = mysql_select_db(DB_DATABASE , $con);
	foreach ($data as $key) {
		$IRI 	= $key["IRI"];
		$lat 	= $key["lat"];
		$lon 	= $key["long"];
		$IMEI 	= $key["IMEI"];
		$spd	= $key["speed"];
		$flag   = $key['flag'];

		$sql = "INSERT INTO `pavement` (IRI, latitude,longitude, IMEI, speed, flag ) VALUES('$IRI', '$lat', '$lon', '$IMEI', '$spd'. '$flag')";
		//$result = mysqli_query($con, $sql);
		$result = mysql_query($sql);

		if(!$result){
				$response["success"] = 0;
				$response["message"] = $response["message"].$IRI;
				break;
		}

	}


} else {
    // required field is missing
	$response["success"] = 0;
	$response["message"] = $IRI;

    // echoing JSON response

}

echo json_encode($response);
?>