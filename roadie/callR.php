<?php

$stdv = $_POST['stdv'];
$speed = $_POST['speed'];

$arr = json_encode($stdv);
$arr2 = json_encode($speed);

//echo gettype($arr);
//echo gettype($arr2);

if()
exec("Rscript regression.R $arr $arr2");

$filename = "data.txt";
while(!file_exists($filename)){
	sleep(1);
}
if(file_exists($filename)){
	$myfile = fopen($filename, "r");
	$finalValue = fgets($myfile);
	fclose($myfile);
	unlink($filename);
}


header("Content-type: text/xml");
echo '<returns>';
echo '<return ';
echo 'stdv="' . $finalValue .'" ';
//echo 'speed="' . echo $speed .'" ';
echo '/>';

echo '</returns>';

?>