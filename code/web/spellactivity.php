<?php
include 'spellstate.php'; 

function spellActivity($keyWord)
{
	$spellActivityString = "";
	$numFound = "";
	$origFreq = "";
	$flagnumFound = 0;
	$flagorigFreq = 0;
        $suggestionsFlag = 0; 
	$initialcheck = "";
		
	
	$initialcheck =  spellActivityNext($keyWord);
	if ($initialcheck == "true")
	{
		$spellActivityString = "correct";
		return $spellActivityString;
	}


	$url = SolrServer::getSpellCheckActivityUrl()."?q=".urlencode($keyWord)."&wt=json";
        
     	//echo "URL ".$url; 
	$content = file_get_contents($url);

	if($content)
       {
		$jsonIterator = new RecursiveIteratorIterator(
    		new RecursiveArrayIterator(json_decode($content, TRUE)),
		RecursiveIteratorIterator::SELF_FIRST );

		foreach ($jsonIterator as $key => $val)
		{
			if(strcmp($key,"suggestions")==0)
			{
				$suggestionsFlag = 1;
			} 
			if($suggestionsFlag == 1)
			{ 			
				
				if(strcmp($key,"numFound")==0)
	  			{
					$numFound = $val;
					$flagnumFound = 1;
		         	}
			
				if(strcmp($key,"origFreq")==0)
	  			{
					$origFreq = $val;
					$flagorigFreq = 1;
	    			}

				if($origFreq == 0 && $numFound ==0 && $flagnumFound == 1 && $flagorigFreq == 1)
				{
					$spellActivityString = "refine";				
					break;
				}
		                
			
				if($origFreq > 0)
				{
					$spellActivityString = "correct";				
					break;
				}
				if($numFound > 0)
				{
					if(strcmp($key,"word")==0)
					{
						$spellActivityString = $val;
						break;
					}
				}
			}
					
		}
         }
	if ($suggestionsFlag == 0)
	{
		$spellActivityString = "refine";
	}
	if ($flagnumFound == 0 && $flagorigFreq ==0)
	{
		$spellActivityString = "refine";
	}

	if ($spellActivityString == "refine")
	{
		$string = spellActivityNext($keyWord);
		if ($string == "true")
		{
			$spellStateString = "correct";
		}
	}

	return $spellActivityString;
}

function spellActivityNext($keyWord)
{
	$returnstring = "false";
	$urlnext = SolrServer::getSearchUrl()."?q=main:".$keyWord."&wt=json";		
		$contentnext = file_get_contents($urlnext);

		if($contentnext)
       		{
			$jsonIteratornext = new RecursiveIteratorIterator(
    			new RecursiveArrayIterator(json_decode($contentnext, TRUE)),
			RecursiveIteratorIterator::SELF_FIRST );

			foreach ($jsonIteratornext as $key => $val)
			{
				if(strcmp($key,"numFound")==0)
				{
					$nextnumFound = $val;
					if ($nextnumFound > 0)
					{
						$returnstring = "true";	
					}
				}	 
			}
		}
	return $returnstring;
}

?>
