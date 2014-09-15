REM POST files to SOLR

cd %~dp0
D:
java -Durl="http://localhost:8983/solr/travelsearch/update" -jar post.jar *.xml
