<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Travel Media</title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<link rel="stylesheet" type="text/css" href="style.css" media="screen" />

</head>
<body>


<?
ini_set('display_errors', '1');
?>

<div id="content">
  <div id="left">
    <div id="header">
      <h1><a href="http://localhost/IR/web">Travel Media</a></h1>
      <p>Get to your favorite destination places and enjoy. Specify your preference and roam around the world</p>
      <ul id="tablist">
        
      </ul>
    </div>
    <div class="border">
      <div class="subheader">
        <h2>YOUR PLAN IS AS FOLLOWS</h2>
      </div>
    </div>

<?
$cookie = $_COOKIE['plan'];
$cookie = stripslashes($cookie);
$plans = json_decode($cookie, true);

foreach($plans as $key => &$val){
?>

    <div class="border">
      <div class="subheader">
        <h2><?echo $key;?></h2>
      </div>
    </div>
<?
foreach($val as $key1 => &$val1){
?>

    <div class="left_articles">
      <p><h2><?echo $key1;?>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" value="<?echo $val1;?>"/></h2></p>	
    </div>
<?
}
}
?>    
 <div class="left_box">
      <p></p>
    </div>
    
  </div>
  <div id="right">
    <div class="button">
      
        <a href="tuto1.php" target="_blank"><span class="big">Generate</br>PDF</span></br>for your plan</a>
    </div>
  </div>
  <div id="footer">
    <p class="right">&copy; 2013 Travel Media, Design: badk - Anirudh Karwa, Babu Prasad, Deeshen Shah, Kalpesh Kagresha.</p>
    
  </div>
</div>
</body>
</html>
