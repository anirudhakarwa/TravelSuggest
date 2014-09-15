<?php
include 'solr.php';
function autoSuggestState($keyWord)
{
	$autosuggestArray = array();

	$url=SolrServer::getAutoSuggestStateUrl();
	$url = $url."?q=".urlencode($keyWord)."&wt=json";

	$content = file_get_contents($url);

	if($content)
       {
		$jsonIterator = new RecursiveIteratorIterator(
    		new RecursiveArrayIterator(json_decode($content, TRUE)),
  		RecursiveIteratorIterator::SELF_FIRST );

		foreach ($jsonIterator as $key => $val)
		{
  			if(strcmp($key,"suggestion")==0)
  			{
                       		$autosuggestArray = $val;
    			}
        	}
       }
	return $autosuggestArray;
}

?>
