package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.Category;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.LocationDetail;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikivoyageParser;

public class ParserHandler {

	private String filename;
	private Collection<WikipediaDocument> docs;
	private Collection<String> collectionoftexts;
	private Map<String,edu.buffalo.cse.ir.wikiindexer.wikipedia.LocationDetail> locDetails;

	public ParserHandler(String filename, Collection<WikipediaDocument> docs, Map<String,LocationDetail> locDetails) {
		// TODO Auto-generated constructor stub
		this.filename=filename;
		this.docs=docs;
		this.collectionoftexts=new ArrayList<String>();
		this.locDetails = locDetails;
	}

	boolean pdate = false,auth = false,id = false,title = false,text = false;
	StringBuffer timestamp=new StringBuffer();
	StringBuffer authorname=new StringBuffer();
	StringBuffer idname=new StringBuffer();
	StringBuffer titlename=new StringBuffer();
	StringBuffer textvalue=new StringBuffer();
	int found=0;
	WikipediaDocument doc;

	public Map<String,Category> callSAXParser(Map<String,LocationDetail> citySections){

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {
				public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

					//System.out.println("Start Element :" + qName);

					if (qName.equalsIgnoreCase("timestamp")) {
						pdate = true;
						timestamp.setLength(0);
						String a="";
					}
					if (qName.equalsIgnoreCase("ip") || qName.equalsIgnoreCase("username")) {
						auth = true;
						authorname.setLength(0);
					}
					if (qName.equalsIgnoreCase("id")) {
						if(found<1)
						{
							idname.setLength(0);
							found++;
							id = true;
						}
					}
					if (qName.equalsIgnoreCase("title")) {
						title = true;
						titlename.setLength(0);
					}
					if (qName.equalsIgnoreCase("text")) {
						text = true;
						textvalue.setLength(0);
					}
				}

				public void endElement(String uri, String localName,String qName) throws SAXException {

					if (qName.equalsIgnoreCase("timestamp")) {
						//System.out.println("Published Date : " + timestamp);
						pdate = false;

					}
					if (qName.equalsIgnoreCase("ip") || qName.equalsIgnoreCase("username")) {
						//System.out.println("Author : " + authorname);
						auth = false;
					}
					if (qName.equalsIgnoreCase("id") && id) {
						//System.out.println("Id : " +idname);
						id = false;
					}
					if (qName.equalsIgnoreCase("title")) {
						//System.out.println("Title : " + titlename);
						title = false;

					}
					if (qName.equalsIgnoreCase("text")) {
						//System.out.println("Text : " + textvalue);
						text = false;

					}

					if(qName.equals("page")&&cancreatedoc()){
						found=0;
						try {
							
							if(locDetails.containsKey(titlename.toString().trim()))
							{
								double lat = locDetails.get(titlename.toString()).getLatitude();
								double lon = locDetails.get(titlename.toString()).getLongitude();
								doc = new WikipediaDocument(Integer.parseInt(idname.toString()), timestamp.toString(), authorname.toString(), titlename.toString(),lat,lon);
								docs.add(doc);
								collectionoftexts.add(textvalue.toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

				public void characters(char ch[], int start, int length) throws SAXException {

					if (pdate) {
						timestamp.append(new String(ch, start, length));
					}

					if (auth) {
						authorname.append(new String(ch, start, length));
					}

					if (id) {
						idname.append(new String(ch, start, length));
					}

					if (title) {
						titlename.append(new String(ch, start, length));
					}

					if (text) {
						textvalue.append(new String(ch, start, length));
					}
				}

				public boolean cancreatedoc(){
					return !timestamp.equals("") && !authorname.equals("") && !idname.equals("") && !titlename.equals("") && !textvalue.equals("");
				}
			};

			saxParser.parse(filename, handler);
			Map<String,Category> categoryMap = WikivoyageParser.parsePageText(collectionoftexts, docs, citySections);
			return categoryMap;
			//return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
