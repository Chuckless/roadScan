<?php
header('Content-type: text/html; charset=utf-8');

$username="root";
$password="";
$database="RoadieDB";


$connection= mysqli_connect ('localhost', $username, $password,$database);
$query = $_GET['query'];


if (!$connection) 
{  die('Not connected : ' . mysql_error());
}

$result = mysqli_query($connection,$query);
if (!$result) {
  die('Invalid query: ' . mysql_error());
}
?>