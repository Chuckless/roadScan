<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['IRI']) && isset($_POST['latitude']) && isset($_POST['longitude']) && isset($_POST['IMEI'])) {
    
    $IRI = $_POST['IRI'];
    $latitude = $_POST['latitude'];
    $longitude = $_POST['longitude'];
    $IMEI = $_POST['IMEI'];

    /*echo $IRI . "<br/>";
    echo $latitude . "<br/>";
    echo $longitude . "<br/>";*/

    // include db connect class
    require_once __DIR__ . '/db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO pavement(IRI, latitude,longitude, IMEI ) VALUES('$IRI', '$latitude', '$longitude', '$IMEI')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Product successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>
