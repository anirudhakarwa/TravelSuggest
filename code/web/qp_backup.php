<?php


/*
* DataData Structure to define the Query Data Type transferred 
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
	public $weight = "Weight of the Response Value";
	
	function QueryPacket()
	{	
		$this->inputValue[0] = "[* TO *]";
		$this->inputCategory[0] = "section";
		$this->outputValue[0]="";
		$this->outputCategory[0]="";
		$this->expectedResponse[0] = "city";		
	}	
	
	public function __toString()
	{
		return $this->inputValue[0];
	}
}

class SolrServer
{
	private static $serverAddress = "http://192.168.0.18";
	private static $portNumber = "8983";
	private static $solrCoreName = "collection1";
	private static $searchHandler = "/select";
	private static $updateHandler = "/update";
	private static $spellCheckHandler = "/spell";
	private static $autoSuggestHandler = "/suggest";
	
	public static function getSearchUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$searchHandler;
	}
	
	public static function getUpdateUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$updateHandler;
	}
	
	public static function getSpellCheckUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$spellCheckHandler;
	}
	
	public static function getAutoSuggestUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$autoSuggestHandler;
	}	
}

// Mapper for Solr - UI Fields
$mapper = array(
	"city" => "title",
	"museums" => "section",
	"restaurants" => "section",
	"content" => "content",
	"section" => "section",
	"activity" => "main",
	"name"=>"name",
	"weight"=>"weight",
	"alldetails"=>"see_place_all_details"	
);

/*************************
*Query Processing Function
*************************/
function queryProcessing($requestObject, $groupByCategory="", $groupBy=false, $updateWeight=false)
{			
	$counter = 0;
	$arrayCounter = 0;
	global $mapper;
	
	$responseObjectArray = array();
	$queryParams = "";
	$queryCounter = 0;
	//echo count($requestObject->category);
	//echo count($requestObject->value);	
	if($updateWeight)
	{
		// Finding old Weight
		echo "Weight inside QP :".$requestObject->weight;
		$oldWeight = intval($requestObject->weight);
		$newWeight = $oldWeight + 1;
		//$newWeight = 1;
		echo "Old Weight :".$oldWeight;
		echo "<br/>";
		echo "New Weight :".$newWeight;
		echo "<br/>";
		// IP Address
		$ipAddress = $_SERVER['REMOTE_ADDR'];
		
		
		$urlWeight = SolrServer::getUpdateUrl()."?commit=true";
		//$xml = "&lt;add&gt;&lt;doc&gt;&lt;field name=\"id\"&gt;".$requestObject->id."&lt;/field&gt;&lt;field name=\"weight\" update=\"set\"&gt;".$newWeight."&lt;/field&gt;&lt;/doc&gt;&lt;/add&gt;";
		$xml = "<add><doc><field name=\"id\">".$requestObject->id."</field><field name=\"weight\" update=\"set\">".$newWeight."</field></doc></add>";
		urlencode($xml);
		//$urlWeight .= "&stream.body=".$xml;
		
		// $ch = curl_init();
		// curl_setopt($ch, CURLOPT_HEADER, 0);
		// curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
		// curl_setopt($ch, CURLOPT_URL, $urlWeight);
		// curl_setopt($ch, CURLOPT_POST, 1);
		// curl_setopt($ch, CURLOPT_POSTFIELDS, "XML=".$xml."&commit=true");
		// $content=curl_exec($ch);
		// curl_close($ch);
		// echo "Response : ".$content;
		
		//echo "XML :".$xml."<br/>";
		
		//use key 'http' even if you send the request to https://...
		// $options = array(
			// 'http' => array(
				// 'header'  => "Content-Type: text/xml\r\n",
				// 'method'  => 'POST',
				// 'content' => http_build_query($xml),
			// ),
		// );
		//$context  = stream_context_create($options);
		//$result = file_get_contents($urlWeight, false, $context);
		//http_post_data($urlWeight, $xml);
		//$url = 'http://api.flickr.com/services/xmlrpc/';
		$ch = curl_init($urlWeight);
		
        $header = array("Content-type:text/xml; charset=utf-8");

		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $xml);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_1);
        curl_setopt($ch, CURLINFO_HEADER_OUT, 1);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		 
		$response = curl_exec($ch);
		if (curl_errno($ch)) {
           print "curl_error:" . curl_error($ch);
        } else {
           curl_close($ch);
           print "curl exited okay\n";
           echo "Data returned...\n";
           echo "------------------------------------\n";
           echo $response;
           echo "------------------------------------\n";
        }
		
		echo "<br/>";
		
		echo "<br/>";
		echo $urlWeight;
		echo "<br/>";
		
		//$response = file_get_contents($urlWeight);
		echo "response";		
		echo "<br/>";
		echo $response;
		echo "<br/>";
	}

	if(!$groupBy && $groupByCategory != "")
	{		
		$count = count($requestObject->expectedResponse);
		foreach($requestObject->inputCategory as $index => $inputCategory)
		{	
			$requestObject->expectedResponse[$count+$index] = $inputCategory;
		}
	}			
	
		foreach ($requestObject->inputCategory as $inputCategory)        
	{	
		//echo $inputCategory;
		if($groupByCategory != $inputCategory)
		{
			$queryParams .= $mapper[$inputCategory]. ":\"" . 
				$requestObject->inputValue[$queryCounter]. "\" AND ";			
			$queryCounter++;
		}
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
						//"&df=".$mapper[$requestObject->inputCategory].
						"&rows=100".
						"&sort=weight+desc".											
						"&wt=json&indent=true";						
	if($groupBy)
		$url .= "&group=true&group.field=".$mapper[$requestObject->expectedResponse[0]];

	echo "<br/>";
	echo $url;
	echo "<br/>";
	echo "<br/>";
	
	//$encodedUrl = urlencode($url);
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
	foreach ($jsonIterator as $key => $val) 
	{	
		if(is_array($val)) 
		{	

		
		}
		else
		{
			//echo "Not an Array";
			 //var_dump($key);
			 //var_dump($val);					
			// echo "****************************>".$key." : ". $val; 
			// echo "<br/>";
			// echo "responseCounter : ".$responseCntr;
			// echo "<br/>";
			// echo "count of exp respo : ".count($requestObject->expectedResponse);
			// echo "<br/>";
			
			for($j=$responseCntr;$j<count($requestObject->expectedResponse);$j++)
			{
				$expectedResponse = $requestObject->expectedResponse[$responseCntr];
				//echo "Key :";
				//var_dump($key);
				//echo "Val :";
				//var_dump($val);
				
				// echo "------------------------>".$key." : ". $val; 
				// echo "<br/>";
				
				if($key == "id")
				{
					$id = $val;
				}
				
				if(strcmp($key,$mapper[$expectedResponse])==0)
				{					
					// echo "<br/>*******************************<br/>";
					// echo "mapper : ".$mapper[$expectedResponse];				
					// echo "<br/>";
					// echo "key :".$mapper[$expectedResponse];	
					// echo "<br/>";					
					// echo "Value:".$val;
					// echo "<br/>";		
					// echo "Counter ".$counter;
					// echo "<br/>";		
					//var_dump($val);
					// echo "<br/>*******************************<br/>";
					
					//$tempValues = $val[$mapper[$expectedResponse]];
					//foreach($tempValues as $tempVal)					
					//	$responseObject->outputValue = $tempVal." ";															
					$responseObject->outputValue[$counter] = $val;
					$responseObject->outputCategory[$counter] = $expectedResponse;
					$responseObject->id = $id;					
					$counter++;
					$weightFlag = true;
					//$id = "";
					$responseCntr = $counter;
					// if($flagValueCount > 0)
						// $flagValueCount--;
						
				}							
			}
			
			//echo $counter."<br/>";
			//echo count($requestObject->expectedResponse);			
			
			//if(strcmp($key,$mapper[$expectedResponse])==0)
			if(strcmp($key,"weight")==0 && $weightFlag)
			{			
				//echo "Inside Weight If-- id : ".$responseObject->id;
				$responseObject->weight = $val;					
				$id = "";
				$weightFlag = false;
				$responseObjectFlag = true;
				
			}
			if(strcmp($key,"_version_")==0 && $weightFlag)
			//if($key == "_version_" && $weightFlag)
			{
				//echo "Inside id If -- id : ".$responseObject->id;
				echo "<br/>";
				$responseObject->weight = "0";					
				$id = "";
				$weightFlag = false;
				$responseObjectFlag = true;
			}
			if($counter == count($requestObject->expectedResponse) && $responseObjectFlag)
			{			
				$responseObject->inputCategory = $requestObject->inputCategory;
				$responseObject->inputValue = $requestObject->inputValue;
				$responseObjectArray[$arrayCounter] = $responseObject;				
				$responseObject = new QueryPacket();								
				$responseCntr = 0;
				$counter = 0;
				$responseObjectFlag = false;
				$arrayCounter++;
			}
		}			
		
	}
	$tempArray = array();
	$responseObjectFinalArray = array();
	//var_dump($responseObjectArray);
	if(!$groupBy && $groupByCategory != "")
	{		
		foreach($responseObjectArray as $responseObject)
		{			
			// var_dump($groupByCategory);
			// var_dump($responseObject->inputCategory);
			// var_dump($responseObject->inputValue);
			foreach($responseObject->inputCategory as $index => $inputCategory)
			{
				foreach($responseObject->outputCategory as $indexExp => $outputCategory)
				{					
					//echo "_____i/p_______________category : ".$responseObject->outputCategory[$indexExp]."</br>";
					//echo "_____g/p_______________category : ".$groupByCategory."</br>";
					//echo "_____i/p________________value    : ".$responseObject->inputValue[$index]."</br>";					
					//echo "_____o/p________________value    : ".$responseObject->outputValue[$indexExp]."</br>";
					// var_dump($responseObject->inputCategory[$index]);
					// var_dump($responseObject->inputValue[$index]);
					// var_dump($responseObject->outputCategory[$indexExp]);
					// var_dump($responseObject->outputValue[$indexExp]);
					if(strcasecmp($responseObject->outputCategory[$indexExp],$groupByCategory)==0 && 
									strcasecmp($responseObject->inputValue[$index],$responseObject->outputValue[$indexExp])==0)
					{
						// echo "**********************category : ".$groupByCategory."</br>";
						// echo "**********************value    : ".$responseObject->inputValue[$index]."</br>";
						if(isset($responseObjectFinalArray[$responseObject->inputValue[$index]]))				
						{						
							$tempArray = $responseObjectFinalArray[$responseObject->inputValue[$index]];
							//echo "defined". $responseObject->inputValue[$index]."<br/>";
						}
						else
						{
							//echo "not defined".$responseObject->inputValue[$index]."<br/>";
							$tempArray = array();
						}
						$cnt = count($tempArray);
						if($cnt > 0 && $tempArray[$cnt-1] == $responseObject)
						{
							//echo "Same Object";
						}
						else
						{
							//echo "Count :".count($tempArray)."<br/>";
							$tempArray[$cnt] =	$responseObject;	
							$responseObjectFinalArray[$responseObject->inputValue[$index]] = $tempArray;
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
	
	//print_r($responseObjectFinalArray);
	return $responseObjectFinalArray;
}
?>
