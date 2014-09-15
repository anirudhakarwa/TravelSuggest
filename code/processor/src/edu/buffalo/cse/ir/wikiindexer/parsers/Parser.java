/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.Category;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.LocationDetail;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 *
 */
public class Parser {
	/* */
	private final Properties props;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 */

	
	public Map<String,Category> parse(String filename, Collection<WikipediaDocument> docs, Map<String,LocationDetail> citySections) {
		if(filename!=null && filename!="" && new File(filename).exists())
		{
			getLocDetails(new File("files/latlon_state_country_final.txt"),citySections);
			ParserHandler ph=new ParserHandler(filename, docs, citySections);
			return ph.callSAXParser(citySections);
		}
		return null;
	}

	private Map<String, LocationDetail> getLocDetails(File file, Map<String,LocationDetail> locDetails) {
		//Map<String,LocationDetail> locDetails = new HashMap<String,LocationDetail>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line="";
			//Line format: City, Latitude, Longitude, State, Country
			while((line=br.readLine())!=null){
				String[] lineCont = line.trim().split(",");
				if(lineCont.length>=5){
					String city = "";
					int i=0;
					for(i=0;i<=lineCont.length-5;i++)
						city+=lineCont[i];
					double latitude = Double.parseDouble(lineCont[i++]);
					double longitude = Double.parseDouble(lineCont[i++]);
					String state = lineCont[i++];
					String country = lineCont[i];

					LocationDetail ld = new LocationDetail(latitude, longitude, state, country);
					//Double[] latLong = {Double.parseDouble(lineCont[1].trim()),Double.parseDouble(lineCont[2].trim())}; 
					locDetails.put(city,ld);
				}
			}
		}catch(Exception e){e.printStackTrace();}
		return locDetails;
	}

	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	private synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	}
}
