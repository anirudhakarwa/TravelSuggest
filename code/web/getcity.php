<html>
<head>
	<link href="css/style.css" rel="stylesheet" type="text/css"  media="all" />
		<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-2.0.3.js"></script>
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
		<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
		<link href='http://fonts.googleapis.com/css?family=Waiting+for+the+Sunrise|Engagement' rel='stylesheet' type='text/css'>
		<script type="text/javascript" src="js/move-top.js"></script>
		<script type="text/javascript" src="js/easing.js"></script>
		<script type="text/javascript">
						jQuery(document).ready(function($) {
							$(".scroll").click(function(event){		
								event.preventDefault();
								$('html,body').animate({scrollTop:$(this.hash).offset().top},1200);
							});
						});
					</script>
		<script>
			$("#slideshow > div:gt(0)").hide();
			setInterval(function() { 
			  $('#slideshow > div:first')
			    .fadeOut(500)
			    .next()
			    .fadeIn(500)
			    .end()
			    .appendTo('#slideshow');
			},  2000);
		</script>
</head>
<body>

	<!---start-header----->
	<div class="header1" id="home" id="content"> 
		<div class="top-header"> 
			<div class="wrap">
			<div class="logo">
				<a href="http://localhost/IR/web">Travel <span>media</span></a>
			</div>
			<div class="top-nav">
				<ul>
					
				</ul>
			</div>
			<div class="clear"> </div>
	
	<!---//End-header----->
 	</div>
	</div>

<?php
include 'qp.php';
ini_set('display_errors', '1');
$act=array();

session_start();
//echo 'hie';
if(isset($_GET["divID"])){
	$act1=explode(',',$_GET["divID"]);
}

for($ind=1;$ind<count($act1);$ind++){
	$act[$ind]=$act1[$ind-1];
}
//var_dump($act);
//$act=array(0=>$_GET["activity1"],1=>$_GET["activity2"]);
//$act=array("title"=>"Buffalo","activity"=>$aaa);

$requestObject = new QueryPacket();			
for($i=1;$i<=count($act);$i++)	
{
	$requestObject->inputCategory[$i-1] = "activity";
	$requestObject->inputValue[$i-1] = $act[$i];
}

$responseObjectArray=queryProcessing($requestObject,"city", true);

//$_SESSION['getcity_response_obj']=serialize($responseObjectArray);
$resp_for_spell='';

for($ind=1;$ind<=count($act);$ind++){
	
	if(strcmp($responseObjectArray["spell"]->spellSuggest[$act[$ind]],"correct_spell") == 0)
	{
		//$resp_for_spell=$resp_for_spell.''.$ind.')'.$act[$ind].': Correctly Spelt, ';
	}
	else if(strcmp($responseObjectArray["spell"]->spellSuggest[$act[$ind]],"refine_search") == 0)
	{
		$resp_for_spell=$resp_for_spell.'Misspelled word- '.$act[$ind].': '.'Refine search, ';
	}
	else
	{
		$resp_for_spell=$resp_for_spell.'Misspelled word- '.$act[$ind].': '."Showing results for- ".$responseObjectArray["spell"]->spellSuggest[$act[$ind]];
	}

}
//echo $arrlength;
?>
<form action="showpref.php" method="get">

<?
for($z=1;$z<=count($act);$z++){
	$var="activity".$z;
?>
	<input type="hidden" name="<?echo $var;?>" value="<?echo $act[$z]?>" />
<?	
}
?>

<div class="content">
	<div class="our-mission">

		<!---start-mid-grids---->
		<div class="mid-grids" id="port">
			<div class="mid-grids-header">
		 		<h3>Cities that you might be interested in !</h3>
		 	 	<h2>Plan your vacation to one these beautiful cities</h2>
				<h2></br><?echo $resp_for_spell;?></h2>
		 	</div>
		 	<ul class="grid cs-style-2">

				<?
				$photoid=-1;
				foreach ($responseObjectArray as $responseObject)        
				{	
					foreach ($responseObject->outputValue as $responseValue)        
					{
					$photoid=($photoid+1)%16;	
				?>					
					<li>
						<figure>
							<?
//$url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query=".urlencode($responseValue)."&sensor=true&key=AIzaSyCeDEGK76f-8LUcGnosj115sh5nUR0quwQ";
//$ch = curl_init();
//curl_setopt($ch, CURLOPT_URL, $url);
//curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
//$response = curl_exec($ch);

//$jsonIterator = new RecursiveIteratorIterator(
//			    new RecursiveArrayIterator(json_decode($response, TRUE)),
//			    RecursiveIteratorIterator::SELF_FIRST);

//foreach ($jsonIterator as $key => $val) {
//	if(strcmp($key,"photo_reference")==0){
//		$ref=$val;
//	}
//}

if(empty($ref)){
//do nothing
							?><img src="images/tr<?echo $photoid;?>.jpg" alt="img02" height="300" width="400"><?
}
else{
	$url="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=".$ref."&sensor=true&key=AIzaSyCeDEGK76f-8LUcGnosj115sh5nUR0quwQ";
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	// Set so curl_exec returns the result instead of outputting it.
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	// Get the response and close the channel.
	$response = curl_exec($ch);
							?><img src="<?echo $response;?>.jpg" alt="img02"><?	
}



//print_r($response);

							?>
							<figcaption>
								<h3><?echo $responseValue;?></h3>
								<span>seven versalia</span>

							<?$_SESSION[$responseValue]=serialize($responseObject);?>
								<input type="submit" name="city" value="<?echo $responseValue;?>" />
							</figcaption>
						</figure>
					</li>
				
				<?
				}
				}
				?>
			</ul>
		</div><!-- /container -->
		<script src="js/toucheffects.js"></script>
		<script src="js/modernizr.custom.js"></script>
	</div>
        <!---End-mid-grids---->
</div>


</form>
</body>
</hmtl>


