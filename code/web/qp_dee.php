<?php

include 'spellactivity.php'; 
/*
* Data Structure to define the Query Data Type transferred 
*/
class QueryPacket
{
	// 
	public $id = "Unique ID";
	public $inputValue= array();
	public $inputCategory = array();
	public $outputValue= array();
	public $outputCategory = array();
	public $expectedResponse = array();	
	public $spellCheck = false;
	public $weight = "0";
	public $localWeight = "0";
	public $timeVal = 0;
	public $combinedWeight = 4.5;
	public $facetResults = array();	
	public $spellSuggest = array();
	public $searchFields = array();
	
	function QueryPacket()
	{	
		$this->inputValue[0] = "[* TO *]";
		$this->inputCategory[0] = "section";
		$this->expectedResponse[0] = "city";		
	}	
	
	public function __toString()
	{
		return $this->inputValue[0];
	}
}

// Mapper for Solr - UI Fields
$mapper = array(
	"state" => "state",
	"city" => "title",
	"museums" => "section",
	"restaurants" => "section",
	"content" => "content",
	"displaycontent" => "displaycontent",
	"section" => "section",
	"activity" => "searchfield",
	"name"=>"name",
	"weight"=>"weight",
	"alldetails"=>"see_place_all_details",
	"store" => "store"
);

/*************************
*Query Processing Function
*************************/
function queryProcessing($requestObject, $groupByCategory="", $groupBy=false, $updateWeight=false ,$facetBy="" ,$sortByTime = false)
{			
	$counter = 0;
	$arrayCounter = 0;
	global $mapper;
	
	$responseObjectArray = array();
	$queryParams = "";
	$queryCounter = 0;

	////var_dump($requestObject);
	
	//Spell Check	
	
	if($updateWeight)
	{
		// IP Address
		$ipAddress = $_SERVER['REMOTE_ADDR'];
		
		// Finding old Weight		
		$oldGlobalWeight = intval($requestObject->weight);
		$newGlobalWeight = $oldGlobalWeight + 1;		
		
		$oldLocalWeight = intval($requestObject->localWeight);
		$newLocalWeight = $oldLocalWeight + 1;			
		$urlWeight = SolrServer::getUpdateUrl()."?commit=true";

		////echo "<br/>OLD Weight : ".$oldGlobalWeight ;
		////echo "----- Local Weight : ".$oldLocalWeight;
		
		////echo "<br/>NEW Weight : ".$newGlobalWeight ;
		////echo "----- Local Weight : ".$newLocalWeight;
		
		$time = time();
		////echo "Time --- >".$time;
		$xml = "<add><doc><field name=\"id\">".$requestObject->id."</field><field name=\"weight\" update=\"set\">".$newGlobalWeight."</field></doc>".
					"<doc><field name=\"id\">".$requestObject->id."-".$ipAddress."</field><field name=\"ip_address\" update=\"set\">".$ipAddress."</field>".
					"<field name=\"time\" update=\"set\">".$time."</field>".
					"<field name=\"weight\" update=\"set\">".$newLocalWeight."</field></doc></add>";
		//echo "<br/> Local ID : ".$requestObject->id."-".$ipAddress;
		
		////echo "XML :".$xml;
		//////echo "update weight---------------------->".$xml;
		$ch = curl_init($urlWeight);		
        $header = array("Content-type:text/xml; charset=utf-8");
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $xml);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_1);
        curl_setopt($ch, CURLINFO_HEADER_OUT, 1);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		 
		$response = curl_exec($ch);
		if (curl_errno($ch)) 
		{
           //print "curl_error:" . curl_error($ch);
        } else {
           curl_close($ch);
           //print "curl exited okay\n";
           ////echo "Data returned...\n";
           ////echo "------------------------------------\n";
           ////echo $response;
           ////echo "------------------------------------\n";
        }
				

	}
	
	//Spell Check
	$spellCheckResult = "";
	//if(isset($responseObjectArray["spell"]))
	$responseObjectArray["spell"] = new QueryPacket();					
	foreach($requestObject->inputCategory as $index => $category)
	{
		if(strcmp($category,"activity")==0)
		{
			//////echo "inside activity";
			$spellCheckResult = spellActivity($requestObject->inputValue[$index]);
			////echo "<br/>".$requestObject->inputValue[$index]."----->";
		}
		else if(strcmp($category,"state")==0)
		{	
			$spellCheckResult = spellState($requestObject->inputValue[$index]);
			////echo "<br/>".$requestObject->inputValue[$index]."----->";
		}
		
		if(strcmp($spellCheckResult,"correct")==0)
		{				
			////echo "Correctly Spelled<br/>";
			$responseObjectArray["spell"]->spellSuggest[$requestObject->inputValue[$index]] = "correct_spell";
		}
		
		else if(strcmp($spellCheckResult,"refine")==0)
		{				
			////echo "Refine your search<br/>";
			$responseObjectArray["spell"]->spellSuggest[$requestObject->inputValue[$index]] = "refine_search";
		}
		else if(!empty($spellCheckResult))
		{			
			////echo "Misspelt<br/> Did you mean ?".$spellCheckResult;
			$responseObjectArray["spell"]->spellSuggest[$requestObject->inputValue[$index]] = $spellCheckResult;
			////echo "<br/>Before".$requestObject->inputValue[$index];
			$requestObject->inputValue[$index] = $spellCheckResult;
			////echo "<br/>After ".$requestObject->inputValue[$index];
		}
	}
	
	////echo "<br/>";
	
	$originalExpCount = count($requestObject->expectedResponse);
	if(!$groupBy && $groupByCategory != "")
	{	
		$requestObject->expectedResponse[$originalExpCount] = $groupByCategory;
	}			
	
	//$stateValue = "";
	foreach ($requestObject->inputCategory as $inputCategory)        
	{	
		if($groupByCategory != $inputCategory)
		{
			$queryParams .= $mapper[$inputCategory]. ":\"". 
				$requestObject->inputValue[$queryCounter]. "\" AND ";			
			$queryCounter++;
		}
//		if(strcmp($mapper[$inputCategory],"state")==0)
	//		$stateValue = $requestObject->inputValue[$queryCounter];
	}	
		
	$queryParams = trim($queryParams, " AND ");
	$queryParams = urlencode($queryParams);
	
	$expectedResponses = "";
	for($i=0;$i<count($requestObject->expectedResponse);$i++)
	{
		$expectedResponses .= $mapper[$requestObject->expectedResponse[$i]]. ",";
	}
	$expectedResponses = trim($expectedResponses, ",");
	$expectedResponses = urlencode($expectedResponses);	

	
	$url = SolrServer::getSearchUrl()."?q=".$queryParams.
						"&fl=id,weight,".$expectedResponses.",_version_".																						
						"&rows=100".
						"&sort=weight+desc".											
						"&wt=json&indent=true";						
						
	if($groupBy && strcmp($groupByCategory,"city")==0)
	{
		$url .= "&group=true&group.field=".$mapper[$requestObject->expectedResponse[0]];
				
		//if(empty($facetBy))
			
	}
	else if(!$groupBy && $groupByCategory != "")
		$url .= "&fq=-level:1";
		
	if(!empty($facetBy))
	{		
		$url .= "&facet=true&facet.field=".$mapper[$facetBy];
	}
	
	//echo "<br/>";
	//echo $url."<br/>";
	
	$responseContent = file_get_contents($url);

	if($responseContent) 
	{
		$serializedResult = json_decode($responseContent,true);		 
	}

	$jsonIterator = new RecursiveIteratorIterator(
		new RecursiveArrayIterator(json_decode($responseContent, TRUE)),
		RecursiveIteratorIterator::SELF_FIRST);

	$j=0;
	$flagValueCount = 0;
	$responseObject = new QueryPacket();								
	$responseCntr = 0;
	$weightFlag = false;
	$responseObjectFlag = false;	
	$id = "";
	$responseObjectArray["facet"] = new QueryPacket();
	foreach ($jsonIterator as $key => $val) 
	{
		
			for($j=0;$j<count($requestObject->expectedResponse);$j++)
			{
				$expectedResponse = $requestObject->expectedResponse[$j];
			
				if(strcmp($key,"id")==0)
				{
					$id = $val;
					////echo 'id found '.$val;					
				
				}
				if(strcmp($key,$mapper[$expectedResponse])==0)
				{	
					$exe=false;
					if(strcmp($key,"title") == 0){
						if(!is_array($val))
						{					
							$exe=true;
						}
					}
					else{
						$exe=true;
					}
					if($exe)
					{
					
						//if(!checkInArray("activity",$responseObject->outputCategory))
						//{
							if($id == "87985")
							{
								//echo " 1. Expected Response :".$expectedResponse." count :";							
								//var_dump(($responseObject->outputValue)); 
								//echo "<br/>";
							}
							$responseObject->outputValue[count($responseObject->outputCategory)] = $val;
							$responseObject->outputCategory[count($responseObject->outputCategory)] = $expectedResponse;							
							$responseObject->id = $id;
							//echo $j.' step '.$val.' val id '.$id.'</br>';					
							$counter++;
							$weightFlag = true;		
							$responseCntr = $counter;
							//break;					
						//}
					}
				}							
			}
			
			if(strcmp($key,"searchfield") == 0)
			{
				if(!checkInArray("activity",$responseObject->outputCategory))
				{
					if($id == "87985")
					{
						//echo " 2. Expected Response :".$expectedResponse." count :";							
						//var_dump(($responseObject->outputValue)); 
						//echo "<br/>";
					}
					$responseObject->outputCategory[count($responseObject->outputCategory)] = "activity";
					$responseObject->outputValue[count($responseObject->outputValue)] = $val;
				}
			}
			
			if(strcmp($key,"weight")==0 && $weightFlag)
			{						
				$responseObject->weight = $val;					
				$weightFlag = false;
			}
			if(strcmp($key,"_version_")==0)			
			{
				if($weightFlag)
					$responseObject->weight = "0";					
				$id = "";
				$weightFlag = false;
				$responseObjectFlag = true;
				////echo 'counter deeshen'.$counter.' '.count($requestObject->expectedResponse).'</br>'; 
			}
			$facetIndex = "";
			if(!empty($facetBy))
			{
				for($j=0;$j<count($requestObject->expectedResponse);$j++)
				{	
					if(strcmp($key, $mapper[$facetBy])==0)
					{
						if(is_array($val))
						{
							foreach($val as $i => $v)
							{				
								if($i % 2 == 0)
								{
									$facetIndex = $v;
								}	
								else
								{
									$responseObjectArray["facet"]->facetResults[$facetIndex] = $v;									
								}
							}
						}
					}			
				}

			}
			////echo 'counter '.$counter.' '.count($requestObject->expectedResponse).' '.$responseObjectFlag.'</br>'; 
			if($counter == (count($requestObject->expectedResponse)) && $responseObjectFlag)
			{			
				$responseObject->inputCategory = $requestObject->inputCategory;
				$responseObject->inputValue = $requestObject->inputValue;				
				$responseObjectArray[$arrayCounter] = $responseObject;
				//echo 'response obj::::::::</br>';				
				//print_r($responseObject);						
				$responseObject = new QueryPacket();								
				$responseCntr = 0;
				$counter = 0;
				$responseObjectFlag = false;
				$arrayCounter++;
			}
	}
	
	// Get Local Weight	
	
	//IP Address
	$ipAddress = $_SERVER['REMOTE_ADDR'];
	////echo "IP Address :".$ipAddress;
	
	$urlLocalWeight = SolrServer::getSearchUrl()."?q=ip_address:".$ipAddress.
					"&fl=id,weight,time,_version_".									
					"&rows=100".					
					"&sort=weight+desc".											
					"&wt=json&indent=true";								

	////echo "<br/>";
	////echo $urlLocalWeight;
	////echo "<br/>";
	$responseContent = file_get_contents($urlLocalWeight);
	if($responseContent) 
	{
		$serializedResult = json_decode($responseContent,true);		 
	}

	$jsonIteratorWt = new RecursiveIteratorIterator(
		new RecursiveArrayIterator(json_decode($responseContent, TRUE)),
		RecursiveIteratorIterator::SELF_FIRST);
	
	$userWeightCache = array();
	$userTimeCache = array();
	$idLocal = "";
	foreach ($jsonIteratorWt as $key => $val) 
	{	
		if(is_array($val)) 
		{	
			//Json Iterator Wt
		}
		else
		{		
			if($key == "id")
			{
				$idLocalArr = explode("-",$val);
				$idLocal = $idLocalArr[0];
			}
			if($key == "weight")
			{
				$userWeightCache[$idLocal] = $val;
			}
			if($key == "time")
			{
				$userTimeCache[$idLocal] = $val;
			}
			if($key == "_version_")
			{
				$idLocal = "";
			}			
		}
	}
	
	////var_dump($userWeightCache);
	
	foreach($responseObjectArray as $index => $responseObject)
	{
		if(isset($userWeightCache[$responseObjectArray[$index]->id]))
			$responseObjectArray[$index]->localWeight = $userWeightCache[$responseObjectArray[$index]->id];
		else
			$responseObjectArray[$index]->localWeight = "0";
		$responseObjectArray[$index]->combinedWeight = pow(intval($responseObjectArray[$index]->localWeight),0.9) + pow($responseObjectArray[$index]->weight,0.1);		
		
		if(isset($userTimeCache[$responseObjectArray[$index]->id]))
			$responseObjectArray[$index]->timeVal = $userTimeCache[$responseObjectArray[$index]->id];
		else
			$responseObjectArray[$index]->timeVal = 0;		
	}
	
	if($sortByTime)	
	{		
		uasort($responseObjectArray,'compareTime');
	}
	else
	{
		uasort($responseObjectArray,'compareWeights');
	}
	
	$tempArray = array();
	$responseObjectFinalArray = array();	
	////var_dump($responseObjectArray);
	//////echo "deeshan <br/>";
	if(!$groupBy && $groupByCategory != "")
	{		
		foreach($responseObjectArray as $responseObject)
		{			
			foreach($responseObject->inputCategory as $index => $inputCategory)
			{
				foreach($responseObject->outputCategory as $indexExp => $outputCategory)
				{	
					if(strcasecmp($responseObject->outputCategory[$indexExp],$responseObject->inputCategory[$index])==0)
					{	
						$bChckFlag = false;
						if(is_array($responseObject->outputValue[$indexExp]))
						{
							if(strcmp($responseObject->id,"87985")==0)
							{
								//echo "Array <br/>";
								//var_dump($responseObject);								
							}
							
							if(checkInArray($responseObject->inputValue[$index],$responseObject->outputValue[$indexExp]))			//							 // contains array operation -->	
							{
								//echo "True <br/>";
								$bChckFlag = true;
							}
							
						}
						else
						{
							if(strcmp($responseObject->id,"87985")==0)
							{
								//echo "Not Array <br/>";
								//var_dump($responseObject);
							}
							if(strcasecmp($responseObject->inputValue[$index],$responseObject->outputValue[$indexExp])==0 ||							
								strlen(stristr($responseObject->outputValue[$indexExp],$responseObject->inputValue[$index]) > 0))
							{
								$bChckFlag = true;
							}							
						}
						if($bChckFlag)
						{
							if(isset($responseObjectFinalArray[$responseObject->inputValue[$index]]))				
							{						
								$tempArray = $responseObjectFinalArray[$responseObject->inputValue[$index]];
							}
							else
							{
								$tempArray = array();
							}
							$cnt = count($tempArray);
							if($cnt > 0 && $tempArray[$cnt-1] == $responseObject)
							{
								//////echo "Same Object";
							}
							else
							{							
								$tempArray[$cnt] =	$responseObject;	
								$responseObjectFinalArray[$responseObject->inputValue[$index]] = $tempArray;
							}
						}
					}
				}
			}
		}
	}
	else
	{			
		$responseObjectFinalArray = $responseObjectArray;
	}
	
	// $temp = array("by car","by air","");
	// if(checkInArray("car",$temp))
		// //echo "Going in car <br/>";
	// else
		// //echo "Not Going in car <br/>";
	////var_dump($responseObjectFinalArray["car"]);
	
	return $responseObjectFinalArray;
}

function compareWeights($responseObj1, $responseObj2)
{
	if($responseObj1->combinedWeight == $responseObj2->combinedWeight)
		return 0;
	else
	return ($responseObj1->combinedWeight > $responseObj2->combinedWeight) ? -1 : 1;
}

function compareTime($responseObj1, $responseObj2)
{
	if($responseObj1->timeVal == $responseObj2->timeVal)
		return 0;
	else
	return ($responseObj1->timeVal > $responseObj2->timeVal) ? -1 : 1;
}


function checkInArray($needle, $haystack)
{
	foreach($haystack as $hay)
	{		
		//if(strcasecmp($hay,$needle)==0 || strlen(stristr($hay,$needle))>0)//
			//return true;
		if(strcasecmp($hay,$needle)==0 || strpos($hay,$needle) !== false)
			return true;
	}
	return false;
}
?>
