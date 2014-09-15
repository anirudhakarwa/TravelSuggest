<?php    

ini_set('display_errors', '1');
$put_cook=$_GET["name"];
$act=$_GET["act"];

if(isset($_COOKIE['plan'])){
	
	$coo=$_COOKIE['plan'];
	//echo $coo;
	$coo1 = stripslashes($coo);
	$coo2 = json_decode($coo1, true);
	if(isset($coo2[$act])){
		$inter=$coo2[$act];
		$inter[$put_cook]='01/01/2013';
		$coo2[$act]=$inter;
	}
	else{
		$arr[$put_cook]='01/01/2013';
		$coo2[$act]=$arr;
	}
	$json = json_encode($coo2, true);
	setcookie('plan', $json);
}
else{
	$arr[$put_cook]='01/01/2013';
	$cardArray=array($act=>$arr);
	$json = json_encode($cardArray, true);
	setcookie('plan', $json);
}

header('Location: getplan.php');    
?>
