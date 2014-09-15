REM POST files to SOLR

cd %~dp0
D:
java -Ddata=args -Dcommit=true -Durl="http://localhost:8983/solr/travelsearch/update" -jar post.jar "<delete><id>*:*</id></delete>"


