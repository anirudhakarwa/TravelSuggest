<?php
class SolrServer
{
	private static $serverAddress = "http://localhost";
	private static $portNumber = "8983";
	private static $solrCoreName = "travelsearch";
	private static $searchHandler = "/select";
	private static $updateHandler = "/update";
	private static $spellCheckActivityHandler = "/spellactivity";
	private static $spellCheckStateHandler = "/spellstate";
	private static $autoSuggestActivityHandler = "/suggestactivity";
	private static $autoSuggestStateHandler = "/suggeststate";
	
	public static function getSearchUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$searchHandler;
	}
	
	public static function getUpdateUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$updateHandler;
	}
	
	public static function getSpellCheckActivityUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$spellCheckActivityHandler;
	}
	
	public static function getSpellCheckStateUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$spellCheckStateHandler;
	}
	
	public static function getAutoSuggestActivityUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$autoSuggestActivityHandler;
	}	

	public static function getAutoSuggestStateUrl()
	{
		return self::$serverAddress.":".self::$portNumber."/solr/".self::$solrCoreName.self::$autoSuggestStateHandler;
	}	
} 
?>
