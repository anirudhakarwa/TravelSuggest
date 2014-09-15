<?php
ini_set('display_errors', '1');

require_once ('lib/OAuth.php');

// For examaple, search for 'tacos' in 'sf'

function getYelp($term, $location){
	
	$unsigned_url = "http://api.yelp.com/v2/search?term=".urlencode($term)."&location=".urlencode($location)."&limit=1";

	// Set your keys here
	$consumer_key = "8LjXkvQ-lcUe7dSlvIHhAQ";
	$consumer_secret = "7AnAzMD4h6mthw27wT48qZFEJoo";
	$token = "B-j7tOmv_GPGzZsfc_VId-cjRMLlBcCq";
	$token_secret = "Hjq6GZOp61HR_JxUgB9_O7HpqKA";

	// Token object built using the OAuth library
	$token = new OAuthToken($token, $token_secret);

	// Consumer object built using the OAuth library
	$consumer = new OAuthConsumer($consumer_key, $consumer_secret);

	// Yelp uses HMAC SHA1 encoding
	$signature_method = new OAuthSignatureMethod_HMAC_SHA1();

	// Build OAuth Request using the OAuth PHP library. Uses the consumer and token object created above.
	$oauthrequest = OAuthRequest::from_consumer_and_token($consumer, $token, 'GET', $unsigned_url);

	// Sign the request
	$oauthrequest->sign_request($signature_method, $consumer, $token);

	// Get the signed URL
	$signed_url = $oauthrequest->to_url();

	// Send Yelp API Call
	$ch = curl_init($signed_url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_HEADER, 0);
	$data = curl_exec($ch); // Yelp response
	curl_close($ch);

	// Handle Yelp response data
	//$response = json_decode($data);

	// Print it for debugging
	//print_r($response);
	return $data;
}
?>
