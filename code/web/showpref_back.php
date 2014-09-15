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
				<a href="#">Travel <span>media</span></a>
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
include 'example.php';

ini_set('display_errors', '1');

$city=$_GET["city"];
echo $city;

$act=array();
$ind=1;
while(true==true){
	$var="activity".$ind;
	if(isset($_GET[$var])){
		$act[$ind]=$_GET[$var];
		echo $act[$ind];
		$ind++;
	}
	else{
		break;
	}	
}

$requestObject = new QueryPacket();

$requestObject->inputCategory[0] = "city";
$requestObject->inputValue[0] = $city;
			
for($i=1;$i<=count($act);$i++)	
{
	$requestObject->inputCategory[$i] = "activity";
	$requestObject->inputValue[$i] = $act[$i];
}

$requestObject->expectedResponse[0] = "name";
$requestObject->expectedResponse[1] = "content";

$responseObjectArray=queryProcessing($requestObject, "activity");
if(empty($responseObjectArray)){
	echo "null";
}
else{

for($outer=1;$outer<=count($act);$outer++){
?>

				<div class="mid-banner">
			  		<div class="wrap">
			  			<h3><?	$whichact=$act[$outer];
							echo $act[$outer];
							?>
						</h3>
			  		</div>
			  	</div>

<div id="ca-container" class="ca-container">
				<div class="ca-wrapper">

				<?
				$actvalues=$responseObjectArray[$act[$outer]];
//count($actvalues)
				for($z=0;$z<count($actvalues);$z++){

					$outputCatArr=$actvalues[$z]->outputCategory;
					$outputValArr=$actvalues[$z]->outputValue;
					$valInd;
					$descInd;
					foreach($outputValArr as $index=>$responseValue){
						if(strcmp($outputCatArr[$index],"name")==0){
							$valInd=$index;							
						}
						if(strcmp($outputCatArr[$index],"content")==0){
							$descInd=$index;							
						}
					}
					$yelp_resp=getYelp($actvalues[$z]->outputValue[$valInd],$city);
					
					$src="/images/tr2.jpg";
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

					//print_r($yelp_resp);
				?>
					
					<div class="ca-item ca-item-2">
						<div class="ca-item-main">
							<div class="ca-icon" style="background: transparent url(<?echo $src;?>) no-repeat center center; background-size: 80%;"> </div>
							
							<h3><?echo $outputValArr[$valInd];?></h3>
							<h4>
								<span class="ca-quote">&ldquo;</span>
								<span>The greatness of a nation and its moral progress can be judged by the way in which its animals are treated.</span>
							</h4>
								<a href="#" class="ca-more">+</a>
						</div>
						<div class="ca-content-wrapper">
							<div class="ca-content">
								<h6><?echo $outputValArr[$valInd];?></h6>
								<a href="#" class="ca-close">close</a>
								<div class="ca-content-text">
									<p><?echo $outputValArr[$descInd];?></p>
								</div>
								<ul>
									<form method="get" action="specificinfo.php">
									<input type="hidden" name="city" value="<?echo $city?>" />
									<input type="hidden" name="activity" value="<?echo $outputValArr[$valInd];?>" />

									<li><a href="#" onclick="$(this).closest('form').submit()">Read More</a></li>
									
									<li><a href="setcookie.php?name=<?echo $outputValArr[$valInd];?>&act=<?echo $whichact;?>" target="_blank">Add to Plan</a></li>
									<li><?echo "<a href=mapcity.php?place=".$outputValArr[$valInd];.">Show on Map</a>";?></li>
									</form>
								</ul>
							</div>
						</div>
					</div>
					
					<?
					}
					?>
							</div>
						</div>
					
<?
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


