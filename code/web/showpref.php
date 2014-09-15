

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
	<div class="header2" id="home" id="content"> 
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
	</div>

<?php
include 'qp.php';
include 'example.php';

ini_set('display_errors', '1');



$city=$_GET["city"];
//echo $city;

$act=array();
$ind=1;
while(true==true){
	$var="activity".$ind;
	if(isset($_GET[$var])){
		$act[$ind]=$_GET[$var];
		//echo $act[$ind];
		$ind++;
	}
	else{
		break;
	}	
}

if($ind==1){
echo 'Category not selected.</br>';
}

session_start();
$getcity_reqobj= unserialize($_SESSION[$city]);
//var_dump($getcity_reqobj);
$id = $getcity_reqobj->id;
$weight = $getcity_reqobj->weight;
$localWeight = $getcity_reqobj->localWeight;
//var_dump($getcity_reqobj);

$requestObject = new QueryPacket();
$requestObject->id = $id;
$requestObject->weight = $weight;
$requestObject->localWeight = $localWeight;

$requestObject->inputCategory[0] = "city";
$requestObject->inputValue[0] = $city;


for($i=1;$i<=count($act);$i++)	
{
	$requestObject->inputCategory[$i] = "activity";
	$requestObject->inputValue[$i] = $act[$i];
}

$requestObject->expectedResponse[0] = "name";
$requestObject->expectedResponse[1] = "displaycontent";
$requestObject->expectedResponse[2] = "store";
//echo '<br/><br/>';
//var_dump($requestObject);
$responseObjectArray=queryProcessing($requestObject, "activity",false,true,"");

if(empty($responseObjectArray)){

	echo "No results found";
}
else{
//var_dump($responseObjectArray);

for($outer=1;$outer<=count($act);$outer++){

$countofres=0;
if(isset($responseObjectArray[$act[$outer]])){
//echo 'count dee '.count($responseObjectArray[$act[$outer]]).'</br>';
	
?>
				<div class="mid-banner">
			  		<div class="wrap">
			  			<h3 style="color:#FFFFFF;"><?	$whichact=$act[$outer];
							echo $act[$outer];
							?>
						</h3>
			  		</div>
			  	</div>
	
	<div class="our-mission" id="team">
		<div class="wrap">
			<div id="ca-container" class="ca-container">

				<!--<div class="ca-nav">
					<span class="ca-nav-prev">Previous</span>
					<span class="ca-nav-next">Next</span>
				</div>-->
				<div class="ca-wrapper" style="overflow:hidden;">

				<?
				$actvalues=$responseObjectArray[$act[$outer]];


//print_r(serialize($actvalues[0]));
				for($z=0;$z<count($actvalues);$z++){
				//echo $z.'dee </br>';
				//var_dump($actvalues[$z]);
				//echo '</br>';

					$outputCatArr=$actvalues[$z]->outputCategory;
					$outputValArr=$actvalues[$z]->outputValue;
					$valInd=-1;
					$descInd=-1;
					$storeInd;
					//var_dump($outputValArr);
					foreach($outputValArr as $index=>$responseValue){
						if(strcmp($outputCatArr[$index],"name")==0){
							$valInd=$index;							
						}
						if(strcmp($outputCatArr[$index],"displaycontent")==0){
							$descInd=$index;							
						}
						if(strcmp($outputCatArr[$index],"store")==0){
						//echo 'deeshen'.$outputValArr[$index];							
							$storeInd=$index;							
						}
					}
					if(strcmp($valInd,-1)!=0){
					$countofres++;
					$yelp_resp=getYelp($outputValArr[$valInd],$city);
					$src="/images/tr4.jpg";
					$jsonIterator = new RecursiveIteratorIterator(
								    new RecursiveArrayIterator(json_decode($yelp_resp, TRUE)),
								    RecursiveIteratorIterator::SELF_FIRST);

					foreach ($jsonIterator as $key => $val) {
						if(is_array($val)){
						}
						else{
					
						if(strcmp($key,"image_url")==0){
							//$ref=$val;
							$src=$val;
							break;
						}
						}
					}				
					

				?>
					<div class="ca-item ca-item-2">
						<div class="ca-item-main">
							<div class="ca-icon" style="background: transparent url(<?echo $src;?>) no-repeat center center; background-size: 80%;"> </div>
							
							<h3><?echo $outputValArr[$valInd];?></h3>
							<h4>
								<span class="ca-quote">&ldquo;</span>
								<span>A good traveler has no fixed plans and is not intent on arriving.</span>
							</h4>
								<a href="#" class="ca-more">+</a>
						</div>
						<div class="ca-content-wrapper">
							<div class="ca-content">
								<h6><?echo $outputValArr[$valInd];?></h6>
								<a href="#" class="ca-close">close</a>
								<div class="ca-content-text">
									<p><?if(strcmp($descInd,-1)==0){echo 'No content';}else{
									$disp_res='';
									$single=$outputValArr[$descInd];	
									for($out=0;$out<count($single);$out++){
									$disp_res=$disp_res.' '.$single[$out];
									}
									echo $disp_res;}?></p>
								</div>
							</div>
							<div class="ca-content" style="height:70px;">
								<ul>
									<form method="get" action="specificinfo.php">
									<input type="hidden" name="city" value="<?echo $city;?>" />
							<input type="hidden" name="activity_name" value="<?echo $act[$outer];?>" />
									<input type="hidden" name="activity" value="<?echo $outputValArr[$valInd];?>" />
									<? $_SESSION[$outputValArr[$valInd]]=serialize($actvalues[$z]);?>				

									<li><a href="#" onclick="$(this).closest('form').submit()">Read More</a></li>
									
									<li><a href="setcookie.php?name=<?echo $outputValArr[$valInd];?>&act=<?echo $whichact;?>" target="_blank">Add to Plan</a></li>
									
									</form>
								</ul>
							</div>	
						</div>
					</div>
					
					<?
					}
					}
if($countofres==0){
	echo 'Sorry, no results found.';
}
					
					?>
							</div>
						</div>
					</div>
				</div>
					
<?
}
}
}
?>

					<!-- the jScrollPane script -->
					<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
					<script type="text/javascript" src="js/jquery.easing.1.3.js"></script>
					<!-- the jScrollPane script -->
					<script type="text/javascript" src="js/jquery.mousewheel.js"></script>
					<script type="text/javascript" src="js/jquery.contentcarousel.js"></script>
					<script type="text/javascript">
						$('#ca-container').contentcarousel();
					</script>



</body>
</hmtl>


