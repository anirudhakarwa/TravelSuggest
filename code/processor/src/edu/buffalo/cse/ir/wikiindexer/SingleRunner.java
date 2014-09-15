package edu.buffalo.cse.ir.wikiindexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
/**
 * 
 */







import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants.RequiredConstant;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.SharedDictionary;
import edu.buffalo.cse.ir.wikiindexer.parsers.Parser;
import edu.buffalo.cse.ir.wikiindexer.test.AllTests;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactory;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.Category;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.Category.Document;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.DocumentTransformer;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.IndexableDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.LocationDetail;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section.Template;

/**
 * @author nikhillo
 *
 */
public class SingleRunner {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	private static int docCount = 1;
	private static String[] sectionStr = {"section","sub-section","sub-sub-section","sub-sub-sub-section"}; 
	public static void main(String[] args) throws InterruptedException {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		} else {
			if (args[0] != null && args[0].length() > 0) {
				String filename = args[0];
				Properties properties = loadProperties(filename);
				if (properties == null) {
					System.err.println("Error while loading the Properties file. Please check the messages above and try again");
					System.exit(2);
				} else  {
					if (args[1] != null && args[1].length() == 2) {
						String mode = args[1].substring(1).toLowerCase();

						if ("t".equals(mode)) {
							runTests(filename);
						} else if ("i".equals(mode)) {
							runIndexer(properties);
						} else if ("b".equals(mode)) {
							runTests(filename);
							runIndexer(properties);
						} else {
							System.err.println("Invalid mode specified!");
							printUsage();
							System.exit(4);
						}	
					} else {
						System.err.println("Invalid or no mode specified!");
						printUsage();
						System.exit(5);
					}
				}
			} else {
				System.err.println("The provided properties filename is empty or could not be read");
				printUsage();
				System.exit(3);
			}	
		}

	}
	private static void prepareSolrXml(Map<String,Category> categoryMap)
	{
		//File solrXml = new File("files/solrData.xml");
		try{
			int docCount = 1;
			for(String catName:categoryMap.keySet()){
				//if(catName.contains("/"))catName = catName.replace("/", "_");
				File catSolrXml = new File("solrxml/"+catName.replace("/", "_")+".xml");
				BufferedWriter bw = new BufferedWriter(new FileWriter(catSolrXml));
				bw.write("\n<add>");
				Category category = categoryMap.get(catName);
				catName = catName.toLowerCase();
				catName = catName.replace(" ", "_");
				for(Document document:category.getDocSet()){
					bw.write("\n\t<doc>");
					bw.write("\n\t\t<field name=\"id\">"+(docCount++)+"</field>");
					bw.write("\n\t\t<field name=\"title\">"+document.getTitle()+"</field>");
					if(document.getProperties()!=null && document.getProperties().size()>0){
						bw.write("\n\t\t<field name=\""+catName+"_name\">"+document.getName()+"</field>");
						for(String key:document.getProperties().keySet()){
							bw.write("\n\t\t<field name=\""+catName+"_"+key+"\">"+document.getProperties().get(key).trim()+"</field>");
						}
					}
					else{
						bw.write("\n\t\t<field name=\""+catName+"_name\"></field>");
						if(document.getName()!=null)
							bw.write("\n\t\t<field name=\""+catName+"_content\">"+document.getName().trim()+"</field>");
					}
					bw.write("\n\t\t<field name=\"weight\">1</field>");
					bw.write("\n\t\t<field name=\"clicks\">0</field>");
					bw.write("\n\t</doc>");
				}
				bw.write("\n</add>");
				bw.close();
			}
		}catch(Exception e){e.printStackTrace();}
	}

	static class CategoryIndex{
		Category c;
		int index;
		public Category getC() {
			return c;
		}
		public void setC(Category c) {
			this.c = c;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public CategoryIndex(Category c, int index) {
			super();
			this.c = c;
			this.index = index;
		}
	}

	private static void prepareSolrXmlNew(Map<String,Category> categoryMap){
		try{
			for(String category:categoryMap.keySet()){
				Category cat = categoryMap.get(category);
				category = category.replace(" ", "_");
				File catSolrXml = new File("solrxml/"+category.replace("/", "_")+".xml");
				BufferedWriter bw = new BufferedWriter(new FileWriter(catSolrXml));
				bw.write("<add>");
				category = category.toLowerCase();

				LinkedList<CategoryIndex> catList = new LinkedList<CategoryIndex>();
				catList.addFirst(new CategoryIndex(cat,-1));
				while(catList.size()>0){
					CategoryIndex ci = catList.peekLast();
					Category c = ci.getC();
					if(c.getCategories()==null){
						if(c.getDocSet()!=null){
							addDocs(bw, catList, c);
							/*for(Document d:c.getDocSet()){
								if(d.getProperties()!=null || d.getContent()!=null){
									bw.write("\n\t<doc>");
									bw.write("\n\t\t<field name=\"id\">"+(docCount++)+"</field>");
									bw.write("\n\t\t<field name=\"title\">"+d.getTitle()+"</field>");

									//Iterate all sections of linkedlist
									bw.write("\n\t\t<field name=\"main\">"+catList.get(0).getC().getCatName()+"</field>");
									for(int i=1;i<catList.size()-1;i++)
										bw.write("\n\t\t<field name=\""+sectionStr[i-1]+"\">"+catList.get(i).getC().getCatName()+"</field>");

									if(d.getProperties()!=null && catList.size()>=2)
										bw.write("\n\t\t<field name=\""+sectionStr[catList.size()-2]+"\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");

									if(d.getName()==null)
										bw.write("\n\t\t<field name=\"name\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");
									else
										bw.write("\n\t\t<field name=\"name\">"+d.getName().trim()+"</field>");

									//Iterating all properties
									if(d.getProperties()!=null && d.getProperties().size()>0){
										if(d.getProperties().containsKey("lat") && d.getProperties().containsKey("long")){
											String lat = d.getProperties().get("lat");
											String lon = d.getProperties().get("long");
											d.getProperties().put("store",lat+","+lon);
											d.getProperties().remove("lat");
											d.getProperties().remove("long");
										}

										for(String key:d.getProperties().keySet()){
											bw.write("\n\t\t<field name=\""+key+"\">"+d.getProperties().get(key).trim()+"</field>");
										}
									}

									if(d.getContent()!=null)
										bw.write("\n\t\t<field name=\"content\">"+d.getContent().trim()+"</field>");

									bw.write("\n\t\t<field name=\"weight\">1</field>");
									bw.write("\n\t\t<field name=\"clicks\">0</field>");
									bw.write("\n\t</doc>");
								}
							}*/


						}
						catList.pollLast();
					}
					else{
						if(c.getCategories().size()>(ci.getIndex()+1)){
							CategoryIndex cindex = new CategoryIndex(c.getCategories().get(ci.getIndex()+1),-1);
							ci.setIndex(ci.getIndex()+1);
							catList.add(cindex);
						}
						else {
							if(catList.peekLast().getC().getDocSet()!=null){
								c = catList.peekLast().getC();
								addDocs(bw, catList, c);
								/*for(Document d:c.getDocSet()){
									if(d.getProperties()!=null || d.getContent()!=null){
										//System.out.println("----------");
										bw.write("\n\t<doc>");
										bw.write("\n\t\t<field name=\"id\">"+(docCount++)+"</field>");
										bw.write("\n\t\t<field name=\"title\">"+d.getTitle()+"</field>");

										//Iterate all sections of linkedlist
										bw.write("\n\t\t<field name=\"main\">"+catList.get(0).getC().getCatName()+"</field>");
										for(int i=1;i<catList.size()-1;i++)
											bw.write("\n\t\t<field name=\""+sectionStr[i-1]+"\">"+catList.get(i).getC().getCatName()+"</field>");

										if(d.getProperties()!=null && catList.size()>=2)
											bw.write("\n\t\t<field name=\""+sectionStr[catList.size()-2]+"\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");

										if(d.getName()==null)
											bw.write("\n\t\t<field name=\"name\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");
										else
											bw.write("\n\t\t<field name=\"name\">"+d.getName().trim()+"</field>");

										//Iterating all properties
										if(d.getProperties()!=null && d.getProperties().size()>0){
											if(d.getProperties().containsKey("lat") && d.getProperties().containsKey("long")){
												String lat = d.getProperties().get("lat");
												String lon = d.getProperties().get("long");
												d.getProperties().put("store",lat+","+lon);
												d.getProperties().remove("lat");
												d.getProperties().remove("long");
											}

											for(String key:d.getProperties().keySet()){
												bw.write("\n\t\t<field name=\""+key+"\">"+d.getProperties().get(key).trim()+"</field>");
											}
										}

										if(d.getContent()!=null)
											bw.write("\n\t\t<field name=\"content\">"+d.getContent().trim()+"</field>");

										bw.write("\n\t\t<field name=\"weight\">1</field>");
										bw.write("\n\t\t<field name=\"clicks\">0</field>");
										bw.write("\n\t</doc>");
									}
								}*/
							}
							catList.pollLast();
						}
					}
				}
				bw.write("\n</add>");
				bw.close();
			}
		}catch(Exception e){e.printStackTrace();}
	}

	private static void addDocs(BufferedWriter bw, LinkedList<CategoryIndex> catList, Category c){
		try{
			for(Document d:c.getDocSet()){
				if(d.getProperties()!=null || d.getContent()!=null){
					bw.write("\n\t<doc>");
					bw.write("\n\t\t<field name=\"id\">"+(docCount++)+"</field>");
					bw.write("\n\t\t<field name=\"title\">"+d.getTitle()+"</field>");

					//Iterate all sections of linkedlist
					bw.write("\n\t\t<field name=\"main\">"+catList.get(0).getC().getCatName()+"</field>");
					for(int i=1;i<catList.size()-1;i++)
						bw.write("\n\t\t<field name=\""+sectionStr[i-1]+"\">"+catList.get(i).getC().getCatName()+"</field>");

					if(d.getProperties()!=null && catList.size()>=2)
						bw.write("\n\t\t<field name=\""+sectionStr[catList.size()-2]+"\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");

					if(d.getName()==null)
						bw.write("\n\t\t<field name=\"name\">"+catList.get(catList.size()-1).getC().getCatName()+"</field>");
					else
						bw.write("\n\t\t<field name=\"name\">"+d.getName().trim()+"</field>");

					//Iterating all properties
					if(d.getProperties()!=null && d.getProperties().size()>0){
						if(d.getProperties().containsKey("lat") && d.getProperties().containsKey("long")){
							String lat = d.getProperties().get("lat").trim();
							String lon = d.getProperties().get("long").trim();
							if(!lat.matches(".*([a-zA-Z]+|[°]|[ ]).*") && !lon.matches(".*([a-zA-Z]+|[°]|[ ]).*"))
								d.getProperties().put("store",lat+","+lon);
							d.getProperties().remove("lat");
							d.getProperties().remove("long");
						}

						for(String key:d.getProperties().keySet()){
							bw.write("\n\t\t<field name=\""+key+"\">"+d.getProperties().get(key).trim()+"</field>");
						}
					}

					if(d.getContent()!=null)
						bw.write("\n\t\t<field name=\"content\">"+d.getContent().trim()+"</field>");

					bw.write("\n\t\t<field name=\"weight\">1</field>");
					bw.write("\n\t\t<field name=\"clicks\">0</field>");
					bw.write("\n\t</doc>");
				}
			}
		}catch(Exception e){e.printStackTrace();}
	}

	private static void prepareCitySolrXml(Map<String,LocationDetail> citySections, Set<Document> docSet){
		try{
			//System.out.println(citySections.size());
			File citySolrXml = new File("solrxml/citieSolr.xml");
			BufferedWriter bw = new BufferedWriter(new FileWriter(citySolrXml));
			bw.write("<add>");
			for(String title:citySections.keySet()){
				String content="";
				for(Document d:docSet){
					if(d.getTitle().equalsIgnoreCase(title)){
						content = d.getContent();
						break;
					}
				}
				bw.write("\n\t<doc>");
				bw.write("\n\t\t<field name=\"id\">"+(docCount++)+"</field>");
				bw.write("\n\t\t<field name=\"title\">"+title+"</field>");
				bw.write("\n\t\t<field name=\"main\">"+citySections.get(title).getAllSections()+"</field>");
				if(content!=null && !content.matches(""))bw.write("\n\t\t<field name=\"content\">"+content+"</field>");
				bw.write("\n\t\t<field name=\"state\">"+citySections.get(title).getState()+"</field>");
				bw.write("\n\t\t<field name=\"country\">"+citySections.get(title).getCountry()+"</field>");
				bw.write("\n\t\t<field name=\"store\">"+citySections.get(title).getLatitude()+","+citySections.get(title).getLongitude()+"</field>");
				bw.write("\n\t\t<field name=\"level\">"+1+"</field>");
				bw.write("\n\t</doc>");
			}
			bw.write("\n</add>");
			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}

	private static void runIndexer(Properties properties) throws InterruptedException {
		long start;
		System.out.println("Starting .......");
		ArrayList<WikipediaDocument> list = new ArrayList<WikipediaDocument>();
		Parser parser = new Parser(properties);
		start = System.currentTimeMillis();
		Map<String,LocationDetail> citySections = new HashMap<String,LocationDetail>();
		Map<String,Category> categoryMap = parser.parse(FileUtil.getDumpFileName(properties), list, citySections);
		prepareCitySolrXml(citySections,categoryMap.get("default").getDocSet());
		prepareSolrXmlNew(categoryMap);
		System.out.println("Finished parsing: " + (System.currentTimeMillis() - start));
		String s;

		/*Map<INDEXFIELD, Tokenizer> tknizerMap;
		ExecutorService svc = Executors.newSingleThreadExecutor();
		CompletionService<IndexableDocument> pool = new ExecutorCompletionService<IndexableDocument>(svc);
		int numdocs = list.size();

		start = System.currentTimeMillis();
		System.out.println("Starting tokenization");
		for (WikipediaDocument doc : list) {
			tknizerMap = initMap(properties);
			pool.submit(new DocumentTransformer(tknizerMap, doc));
		}

		System.out.println("Submitted tokenization: " + (System.currentTimeMillis() - start));

		IndexableDocument idoc;
		SharedDictionary docDict = new SharedDictionary(properties, INDEXFIELD.LINK);
		int currDocId;
		ThreadedIndexerRunner termRunner = new ThreadedIndexerRunner(properties);
		SingleIndexerRunner authIdxer = new SingleIndexerRunner(properties, INDEXFIELD.AUTHOR, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner catIdxer = new SingleIndexerRunner(properties, INDEXFIELD.CATEGORY, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner linkIdxer = new SingleIndexerRunner(properties, INDEXFIELD.LINK, INDEXFIELD.LINK, docDict, true);
		Map<String, Integer> tokenmap;

		System.out.println("Starting indexing.....");
		start = System.currentTimeMillis();
		double pctComplete = 0;
		for (int i = 0; i < numdocs; i++) {
			try {
				idoc = pool.take().get();
				if (idoc != null) {
					currDocId = docDict.lookup(idoc.getDocumentIdentifier());
					TokenStream stream;
					try {
						for (INDEXFIELD fld : INDEXFIELD.values()) {
							stream = idoc.getStream(fld);

							if (stream != null) {
								tokenmap = stream.getTokenMap();

								if (tokenmap != null) {
									switch (fld) {
									case TERM:
										termRunner.addToIndex(tokenmap,
												currDocId);
										break;
									case AUTHOR:
										authIdxer.processTokenMap(
												currDocId, tokenmap);
										break;
									case CATEGORY:
										catIdxer.processTokenMap(currDocId,
												tokenmap);
										break;
									case LINK:
										linkIdxer.processTokenMap(
												currDocId, tokenmap);
										break;
									}
								}
							}

						}
					} catch (IndexerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			pctComplete = (i * 100.0d) / numdocs;

			if (pctComplete % 10 == 0) {
				System.out.println(pctComplete+ "% submission complete");
			}
		}

		System.out.println("Submitted all tasks in: " + (System.currentTimeMillis() - start));

		try {
			termRunner.cleanup();
			authIdxer.cleanup();
			catIdxer.cleanup();
			linkIdxer.cleanup();
			docDict.writeToDisk();
			docDict.cleanUp();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Waiting for all tasks to complete");
		while (termRunner.isFinished() && authIdxer.isFinished() && catIdxer.isFinished() && linkIdxer.isFinished()) {
			//do nothing
			Thread.sleep(3000);
		}

		System.out.println("Process complete: " + (System.currentTimeMillis() - start));
		svc.shutdown();

		 */



	}

	private static Map<INDEXFIELD, Tokenizer> initMap(Properties props) {
		HashMap<INDEXFIELD, Tokenizer> map = new HashMap<INDEXFIELD, Tokenizer>(INDEXFIELD.values().length);
		TokenizerFactory fact = TokenizerFactory.getInstance(props);
		for (INDEXFIELD fld : INDEXFIELD.values()) {
			map.put(fld, fact.getTokenizer(fld));
		}

		return map;
	}

	/**
	 * Method to print the correct usage to run this class.
	 */
	private static void printUsage() {
		System.err.println("The usage is: ");
		System.err.println("java edu.buffalo.cse.ir.wikiindexer.Runner <filename> <flag>");
		System.err.println("where - ");
		System.err.println("filename: Fully qualified file name from which to load the properties");
		System.err.println("flag: one amongst the following -- ");
		System.err.println("-t: Only execute tests");
		System.err.println("-i: Only run the indexer");
		System.err.println("-b: Run both, tests first then indexer");

	}

	/**
	 * Method to execute all tests
	 * @param filename: Filename for the properties file
	 */
	private static void runTests(String filename) {
		System.setProperty("PROPSFILENAME", filename);
		JUnitCore core = new JUnitCore();
		core.run(new Computer(), AllTests.class);

	}

	/**
	 * Method to load the Properties object from the given file name
	 * @param filename: The filename from which to load Properties
	 * @return The loaded object
	 */
	private static Properties loadProperties(String filename) {

		try {
			Properties props = FileUtil.loadProperties(filename);

			if (validateProps(props)) {
				return props;
			} else {
				System.err.println("Some properties were either not loaded or recognized. Please refer to the manual for more details");
				return null;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: " + filename);
		} catch (IOException e) {
			System.err.println("Error while reading properties from the specified file: " + filename);
		}

		return null;
	}

	/**
	 * Method to validate that the properties object has been correctly loaded
	 * @param props: The Properties object to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validateProps(Properties props) {
		/* Validate size */
		if (props != null && props.entrySet().size() == IndexerConstants.NUM_PROPERTIES) {
			/* Get all required properties and ensure they have been set */
			Field[] flds = IndexerConstants.class.getDeclaredFields();
			boolean valid = true;
			Object key;

			for (Field f : flds) {
				if (f.isAnnotationPresent(RequiredConstant.class) ) {
					try {
						key = f.get(null);
						if (!props.containsKey(key) || props.get(key) == null) {
							System.err.println("The required property " + f.getName() + " is not set");
							valid = false;
						}
					} catch (IllegalArgumentException e) {

					} catch (IllegalAccessException e) {

					}
				}
			}

			return valid;
		}

		return false;
	}

}
