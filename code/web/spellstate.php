<?php

//include 'qp.php';
include 'solr.php';

function spellState($keyWord)
{
	$spellStateString = "";
	$numFound = "";
	$origFreq = "";
	$flagnumFound = 0;
	$flagorigFreq = 0;
        $suggestionsFlag = 0; 
	$string = "";
	$initialcheck = "";

	$initialcheck =  spellStateNext($keyWord);
	if ($initialcheck == "true")
	{
		$spellStateString = "correct";
		return $spellStateString;
	}

	$url = SolrServer::getSpellCheckStateUrl()."?q=".urlencode($keyWord)."&wt=json";
        
        //echo $url; 
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
					$spellStateString = "refine";				
					break;
				}
		                
			
				if($origFreq > 0)
				{
					$spellStateString = "correct";				
					break;
				}
				if($numFound > 0)
				{
					if(strcmp($key,"word")==0)
					{
						$spellStateString = $val;
						break;
					}
				}
			}
					
		}
         }
	if ($suggestionsFlag == 0)
	{
		$spellStateString = "refine";
	}
	if ($flagnumFound == 0 && $flagorigFreq ==0)
	{
		$spellStateString = "refine";
	}

	if ($spellStateString == "refine")
	{
		$string = spellStateNext($keyWord);
		if ($string == "true")
		{
			$spellStateString = "correct";
		}
	}

	return $spellStateString;
}

function spellStateNext($keyWord)
{
	$returnstring = "false";
	$urlnext = SolrServer::getSearchUrl().'?q=autosuggeststate:"'.urlencode($keyWord).'"&wt=json';		
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
