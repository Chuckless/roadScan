<?php

header('Content-type: text/html; charset=utf-8');

$username="root";
$password="";
$database="RoadieDB";

/*define('DB_USER', "root"); // db user
define('DB_PASSWORD', ""); // db password (mention your db password here)
define('DB_DATABASE', "RoadieDB"); // database name
define('DB_SERVER', "localhost"); // db server*/

/*define('DB_USER', "lucas"); // db user
define('DB_PASSWORD', "123456"); // db password (mention your db password here)
define('DB_DATABASE', "RoadieDB"); // database name
define('DB_SERVER', "200.239.153.214"); // db server*/

function parseToXML($htmlStr)
{
$xmlStr=str_replace('<','&lt;',$htmlStr);
$xmlStr=str_replace('>','&gt;',$xmlStr);
$xmlStr=str_replace('"','&quot;',$xmlStr);
$xmlStr=str_replace("'",'&#39;',$xmlStr);

return $xmlStr;
}  
    $SWlat = $_GET['SWlat'];
    $SWlng = $_GET['SWlng'];
    $NElat = $_GET['NElat'];
    $NElng = $_GET['NElng'];

// Opens a connection to a MySQL server
//$connection = mysqli_connect(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE) or die(mysql_error());
$connection= mysqli_connect ('localhost', $username, $password,$database);
if (!$connection) 
{  die('Not connected : ' . mysql_error());
}
// Select all the rows in the markers table
$query = "SELECT * FROM pavement WHERE ((`latitude` >= $SWlat AND `latitude` <= $NElat)
								 AND    (`longitude` >= $SWlng AND `longitude` <= $NElng))";

//SELECT * FROM pavement WHERE ((`latitude` >= -19.836446140212455 AND `latitude` <= -19.8352653435437) AND   (`longitude` >= -43.16883444786072 AND `longitude` <= -43.1668496131897));


$result = mysqli_query($connection,$query);
if (!$result) {
  die('Invalid query: ' . mysql_error());
}

$bool = false;
header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';

// Iterate through the rows, printing XML nodes for each
foreach ($result as $row){
  // ADD TO XML DOCUMENT NODE
  echo '<marker ';
  echo 'id="' . parseToXML($row['id']) . '" ';
  echo 'IRI="' . parseToXML($row['IRI']) . '" ';
  echo 'lat="' . $row['latitude'] . '" ';
  echo 'lng="' . $row['longitude'] . '" ';
  echo 'speed="' . $row['speed'] . '" ';
  echo 'read="' . "false" . '" ';
  echo 'brng="' . $row['brng'] . '" ';
  echo 'flag="' . $row['flag'] . '" ';
  echo 'stack="'. $row['stackCount'] . '" ';
  echo '/>';
}

// End XML file
echo '</markers>';
?>