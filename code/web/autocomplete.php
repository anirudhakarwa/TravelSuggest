<?php

ini_set('display_errors', '1');
include 'autosuggestactivity.php';
// check the parameter
if(isset($_GET['part']) and $_GET['part'] != '')
{
	// initialize the results array
	$results = array();
	$results=autoSuggestActivity(strtolower($_GET['part']));
	echo json_encode($results);

	// search colors
	//foreach($results as $color)
	//{
		// if it starts with 'part' add to results
	//	if( strpos($color, $_GET['part']) === 0 ){
	//		$res[] = $color;
	//	}
	//}

	// return the array as json with PHP 5.2
	//echo json_encode($res);
}
