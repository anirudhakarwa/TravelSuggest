<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Travel Media</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" type="text/css" href="style1.css" />

</head>

<body>

<?

ini_set('display_errors', '1');
include 'qp.php';
session_start();
$found=false;

if(isset($_GET['state'])){
	
	$found=true;
	$state=$_GET['state'];
	$_SESSION['second_level_city']=$state;
	//echo $state;
	$requestObject = new QueryPacket();
	$_SESSION['facet_state_name']=$state;
	$requestObject->inputCategory[0] = "state";
	$requestObject->inputValue[0] = $state;
	$responseObjectArray=queryProcessing($requestObject,"city",true,false,"activity");
	$arr_facet=$responseObjectArray["facet"]->facetResults;
	
	
	$categories=array();
	foreach($arr_facet as $key=>$value){
		$categories[$key]=$value;	
	}
	
	$_SESSION['categories']=serialize($categories);

	$ind_city=0;
	$city=array();
	foreach ($responseObjectArray as $responseObject)        
	{	
		foreach ($responseObject->outputValue as $responseValue)        
		{
			$city[$ind_city]=$responseValue;
			$ind_city++;
		}

	}

}
else if(isset($_SESSION['second_level_city'])&&!isset($_GET['vehicle'])){

	$found=true;
	//$state=$_GET['state'];
	$state=$_SESSION['second_level_city'];
	//echo $state;
	$requestObject = new QueryPacket();
	$_SESSION['facet_state_name']=$state;
	$requestObject->inputCategory[0] = "state";
	$requestObject->inputValue[0] = $state;
	$responseObjectArray=queryProcessing($requestObject,"city",true,false,"activity");
	$arr_facet=$responseObjectArray["facet"]->facetResults;
	
	
	$categories=array();
	foreach($arr_facet as $key=>$value){
		$categories[$key]=$value;	
	}
	
	$_SESSION['categories']=serialize($categories);

	$ind_city=0;
	$city=array();
	foreach ($responseObjectArray as $responseObject)        
	{	
		foreach ($responseObject->outputValue as $responseValue)        
		{
			$city[$ind_city]=$responseValue;
			$ind_city++;
		}

	}


}
else if(isset($_GET['vehicle'])){
	
	$found=true;
	$categories=array();
	$categories=unserialize($_SESSION['categories']);

	$res=$_GET["vehicle"];
	//print_r($res);
	$requestObject = new QueryPacket();			
	for($i=0;$i<count($res);$i++)	
	{
		$requestObject->inputCategory[$i] = "activity";
		$requestObject->inputValue[$i] = $res[$i];
	}
	$ss_sta=$_SESSION['facet_state_name'];
	$requestObject->inputCategory[count($res)] = "state";
	$requestObject->inputValue[count($res)] = $ss_sta;

	$responseObjectArray=queryProcessing($requestObject,"city", true);
	//print_r($requestObject);

	$ind_city=0;
	$city=array();
	foreach ($responseObjectArray as $i=> $responseObject)        
	{			
		foreach ($responseObject->outputValue as $indofres=>$responseValue)        
		{
			if(strcmp($responseObject->outputCategory[$indofres],"city")==0){		
				$city[$ind_city]=$responseValue;
				$ind_city++;
			}
		}

	}

}

if(isset($_SESSION['second_level_city'])){
	$state=$_SESSION['second_level_city'];
}


$resp_for_spell='';

if(isset($responseObjectArray["spell"]->spellSuggest[$state])){
	if(strcmp($responseObjectArray["spell"]->spellSuggest[$state],"correct_spell") == 0)
	{
		//$resp_for_spell=$resp_for_spell.'1)'.$state.': Correctly Spelt, ';
	}
	else if(strcmp($responseObjectArray["spell"]->spellSuggest[$state],"refine_search") == 0)
	{
		$resp_for_spell=$resp_for_spell.'Misspelled word- '.$state.': '.'Refine search, ';
	}
	else
	{
		$resp_for_spell=$resp_for_spell.'Misspelled word- '.$state.': '."Did you mean- ".$responseObjectArray["spell"]->spellSuggest[$state];
	}
}
else{
	if(isset($res)){	
	for($ind=0;$ind<count($res);$ind++){
	
		if(strcmp($responseObjectArray["spell"]->spellSuggest[$res[$ind]],"correct_spell") == 0)
		{
			//$resp_for_spell=$resp_for_spell.''.$ind.')'.$act[$ind].': Correctly Spelt, ';
		}
		else if(strcmp($responseObjectArray["spell"]->spellSuggest[$res[$ind]],"refine_search") == 0)
		{
			$resp_for_spell=$resp_for_spell.$ind.')'.$res[$ind].': '.'Refine search, ';
		}
		else
		{
			$resp_for_spell=$resp_for_spell.$res[$ind].': 	'."Showing results for- ".$responseObjectArray["spell"]->spellSuggest[$res[$ind]];
		}

	}
	}
}	

?>
	<table width="100%" cellpadding="0" cellspacing="0" border="0">
		<tr>
			<td class="top_left">&nbsp;</td>
		    <td width="1006" class="top">
				<ul id="menu">
					<li><a href="http://localhost/IR/web" class="but1">HOME</a></li>
				</ul>
			    <img src="images1/tel.gif" alt="" width="213" height="31" /><br />
			</td>
			<td class="top_right">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3">
				<div id="main">
					<div class="small_column">
						<div class="logo">
							<a href="#"><img src="images1/logo.jpg" alt="setalpm" width="240" height="128" /></a>																																							
						</div>
						<div>
						 	<img src="images1/travel_banner.jpg" alt="" width="700" height="138" /><br />
							
						</div>
						<ul class="small_nav">
							<li><a>Your possible destination places. You can refine your results by using left navigation panel.</a></br></li>
<?
if(strcmp($resp_for_spell,'')!=0){
?>
<li><a><?echo $resp_for_spell;?></a></br></li>
<?}?>	            
<li><a>Number of results: <?if($found){echo count($city);}else{echo '0';}?></a></br></li>  																																																																		
						
						</ul>
						<div class="column1">
							<img src="images1/slice1.gif" alt="" width="22" height="81" class="slice1" />
							<img src="images1/title1.gif" alt="" width="247" height="49" /><br />
							<div class="items">
								
									
								<form method="get" action="facetgetcity.php">
<?
if($found){
foreach($categories as $key=>$value){

?>
								<div class="item"> 
<?
$fla=false;
if(isset($res)){
	for($r=0;$r<count($res);$r++){
		if(strcmp($key,$res[$r])==0){
		$fla=true;
		break;
		}
	}
}
if($fla){
?>
								
								<input type="checkbox" name="vehicle[]" checked="checked" value="<?echo $key;?>" checked="checked" onclick="this.form.submit();"/>&nbsp;<?echo $key;?> (<?echo $value;?>)</br>
<?
}
else{
?>
								<input type="checkbox" name="vehicle[]" value="<?echo $key;?>" onclick="this.form.submit();"/>&nbsp;<?echo $key;?> (<?echo $value;?>)</br>
<?
}
?>
								</div>
								
<?
}
}
?>
								&nbsp;&nbsp;&nbsp;<!--<input type="submit" value="Filter"/>-->
								</form>
								
							</div>
							<img src="images1/end1.gif" alt="" width="247" height="16" /><br />
						</div>
						<div class="column2">
							<img src="images1/slice2.gif" alt="" width="22" height="81" class="slice1" />
							<img src="images1/title2.gif" alt="" width="700" height="49" /><br />
							<div class="articles">
							</br>
							<table>
								
							
<?
if($found){
for($i=0;$i<count($city);$i++){

if($i%3==0){

?>
<div class="article">
<?

echo '<tr>';
for($ifow=$i;$ifow<($i+3)&&$ifow<count($city);$ifow++){
echo '<form action="showpref.php" method="get">';

if(isset($res)){
	for($ifow1=1;$ifow1<=count($res);$ifow1++){
	echo '<input type="hidden" name="activity'.$ifow1.'" value="'.$res[$ifow1-1].'" />';
	}
}
$_SESSION[$city[$ifow]]=serialize($responseObjectArray[$ifow]);
echo '<td><h5>&nbsp;<input type="submit" name="city" value="'.$city[$ifow].'" style="width:150px;"/></h5></td>';
echo '</form>';
}
echo '</tr>';
echo '<tr>';
?>
								<div>
<?
}
?>
									
								<td><img src="images/tr<?echo $i%16;?>.jpg" alt="" width="180" height="180" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</br></br></td>
<?
if($i%3==2){

echo '</tr>';
?>
								</div>
								</div>
<?
}								
}
}
?>
							
							</table>							
							</div>
							<img src="images1/end2.gif" alt="" width="700" height="15" /><br />
						</div>
						
					</div>
					
				</div>
			</td>
		</tr>
		<tr>
			<td class="bot_left">&nbsp;</td>
			<td class="bot_center">
				<ul id="navigation">
					
				</ul>
				<p>Copyright &copy;. All rights reserved. Design by badk: Anirudh Karwa, Babu Prasad, Deeshen Shah, Kalpesh Kagresha.</a></p>																																																																																												
			</td>
			<td class="bot_right">&nbsp;</td>
		</tr>
	</table>
</body>
</html>
