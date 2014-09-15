/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;

/**
 * A Callable document transformer that converts the given WikipediaDocument object
 * into an IndexableDocument object using the given Tokenizer
 * @author nikhillo
 *
 */
public class DocumentTransformer implements Callable<IndexableDocument> {

	private Map<INDEXFIELD, Tokenizer> tknizerMap;
	private WikipediaDocument doc;

	/**
	 * Default constructor, DO NOT change
	 * @param tknizerMap: A map mapping a fully initialized tokenizer to a given field type
	 * @param doc: The WikipediaDocument to be processed
	 */
	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap, WikipediaDocument doc) {
		//TODO: Implement this method
		this.tknizerMap=tknizerMap;
		this.doc=doc;
	}

	/**
	 * Method to trigger the transformation
	 * @throws TokenizerException Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this method

		try{
			IndexableDocument inddoc=new IndexableDocument();
			
			Tokenizer authtk=tknizerMap.get(INDEXFIELD.AUTHOR);
			String auth=doc.getAuthor();
			//System.out.println("AUTHORRRRRR "+auth);
			TokenStream ts=new TokenStream(auth);
			authtk.tokenize(ts);
			inddoc.addField(INDEXFIELD.AUTHOR, ts);

			Tokenizer cattk=tknizerMap.get(INDEXFIELD.CATEGORY);
			List<String> cat=doc.getCategories();
			Iterator<String> itercat=cat.iterator();
			TokenStream ts1=null;
			if(itercat.hasNext()){
				ts1=new TokenStream(itercat.next());
			}
			while(itercat.hasNext()){
				ts1.append(itercat.next());
			}
			if(ts1!=null){
				cattk.tokenize(ts1);
			}
			inddoc.addField(INDEXFIELD.CATEGORY, ts1);


			Tokenizer linktk=tknizerMap.get(INDEXFIELD.LINK);
			Set<String> setoflinks=doc.getLinks();
			List<String> links=new ArrayList<String>(setoflinks);
			Iterator<String> iterlink=links.iterator();
			TokenStream ts2=null;
			if(iterlink.hasNext()){
				ts2=new TokenStream(iterlink.next());
			}
			while(iterlink.hasNext()){
				ts2.append(iterlink.next());
			}
			if(ts2!=null){
				linktk.tokenize(ts2);
			}
			inddoc.addField(INDEXFIELD.LINK, ts2);


			Tokenizer termtk=tknizerMap.get(INDEXFIELD.TERM);
			List<Section> section=doc.getSections();
			Iterator<Section> itersection=section.iterator();
			TokenStream ts3=null;
			if(itersection.hasNext()){
				ts3=new TokenStream(itersection.next().getText());
			}
			while(itersection.hasNext()){
				ts3.append(itersection.next().getText());
			}
			if(ts3!=null){
				termtk.tokenize(ts3);
			}
			inddoc.addField(INDEXFIELD.TERM, ts3);

			/*TokenStream x=inddoc.indexablemap.get(INDEXFIELD.AUTHOR);
			TokenStream x1=inddoc.indexablemap.get(INDEXFIELD.CATEGORY);
			TokenStream x2=inddoc.indexablemap.get(INDEXFIELD.LINK);
			TokenStream x3=inddoc.indexablemap.get(INDEXFIELD.TERM);
			
			System.out.println("IDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"+inddoc.getDocumentIdentifier());
			
			if(x!=null){
				System.out.println("AUTHOR-----------------------------");
				if(x!=null)
				x.display();
				System.out.println("CATEGORY----------------------");
				if(x1!=null)
				x1.display();
				System.out.println("LINK--------------------------");
				if(x2!=null)
				x2.display();
				System.out.println("TERM---------------------------");
				if(x3!=null)
				x3.display();
				System.out.println("---------------------------------------------------");
			}
			else{
				System.out.println("TERMMMMMMMMMMMMMMMMMMM null");
			}*/
			return inddoc;
		}
		catch(Exception e){
			System.out.println("ERRORRR");
			e.printStackTrace();
		}
		return null;

	}

}
