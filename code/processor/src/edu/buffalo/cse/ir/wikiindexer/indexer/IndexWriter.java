/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author nikhillo
 * This class is used to write an index to the disk
 * 
 */
public class IndexWriter implements Writeable {

	private Properties props;
	//invertedIndex: Map<TermId, Map<DocId,Occurence>>
	//forwardIndex: Map<DocId,Map<term,Occurence>>
	private TreeMap<Integer,TreeMap<Integer,Integer>> indexMap;
	private LocalDictionary localDict;

	private INDEXFIELD keyField;
	private INDEXFIELD valueField;
	private boolean isForward;
	private int partitionNum = -1;

	/**
	 * Constructor that assumes the underlying index is inverted
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField) {
		this(props, keyField, valueField, false);
	}

	/**
	 * Overloaded constructor that allows specifying the index type as
	 * inverted or forward
	 * Every index (inverted or forward), has a key field and the value field
	 * The key field is the field on which the postings are aggregated
	 * The value field is the field whose postings we are accumulating
	 * For term index for example:
	 * 	Key: Term (or term id) - referenced by TERM INDEXFIELD
	 * 	Value: Document (or document id) - referenced by LINK INDEXFIELD
	 * @param props: The Properties file
	 * @param keyField: The index field that is the key for this index
	 * @param valueField: The index field that is the value for this index
	 * @param isForward: true if the index is a forward index, false if inverted
	 */
	public IndexWriter(Properties props, INDEXFIELD keyField, INDEXFIELD valueField, boolean isForward) {
		this.props = props;
		this.keyField = keyField;
		this.valueField = valueField;
		this.isForward = isForward;
		indexMap = new TreeMap<Integer, TreeMap<Integer,Integer>>();
		localDict = new LocalDictionary(props, keyField);
	}

	/**
	 * Method to make the writer self aware of the current partition it is handling
	 * Applicable only for distributed indexes.
	 * @param pnum: The partition number
	 */
	public void setPartitionNumber(int pnum) {
		partitionNum = pnum;
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException 
	{
		if(indexMap.containsKey(keyId))
		{
			if(indexMap.get(keyId).containsKey(valueId))
				indexMap.get(keyId).put(valueId,indexMap.get(keyId).get(valueId)+numOccurances);
			else
				indexMap.get(keyId).put(valueId,numOccurances);
		}
		else
		{
			TreeMap<Integer, Integer> docOccurence = new TreeMap<Integer, Integer>();
			docOccurence.put(valueId, numOccurances);
			indexMap.put(keyId, docOccurence);
		}
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * @param keyId: The id for the key field, pre-converted
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(int keyId, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param valueId: The id for the value field, pre-converted
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException {
		key = key.trim();
		if(!key.matches(""))
		{
			key=key.toLowerCase();
			int keyId = localDict.lookup(key);
			addToIndex(keyId, valueId, numOccurances);
		}
	}

	/**
	 * Method to add a given key - value mapping to the index
	 * @param key: The key for the key field
	 * @param value: The value for the value field
	 * @param numOccurances: Number of times the value field is referenced
	 *  by the key field. Ignore if a forward index
	 * @throws IndexerException: If any exception occurs while indexing
	 */
	public void addToIndex(String key, String value, int numOccurances) throws IndexerException {
		//TODO: Implement this method
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException 
	{
		File indexDir = new File("files/index");
		if(indexDir.exists())
			indexDir.delete(); 
		indexDir.mkdirs();

		if(keyField==INDEXFIELD.TERM)
		{
			File indexFile = null;
			if(partitionNum>0)
			{
				char ch = (char)(partitionNum+96);
				indexFile = new File("files/index/"+ch+".idx");
			}
			else
				indexFile = new File("files/index/non_alpha.idx");
			writeIndexes(indexFile);
			localDict.writeToDisk();
		}
		else if(keyField==INDEXFIELD.AUTHOR)
		{
			File indexFile = new File("files/index/author.idx");
			writeIndexes(indexFile);
			localDict.writeToDisk();
		}
		else if(keyField==INDEXFIELD.CATEGORY)
		{
			File indexFile = new File("files/index/category.idx");
			writeIndexes(indexFile);
			localDict.writeToDisk();
		}
		else if(keyField==INDEXFIELD.LINK)
		{
			File indexFile = new File("files/index/doc.idx");
			writeIndexes(indexFile);
		}
	}

	public void writeIndexes(File indexFile)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));

			//Counting the total no. of documents
			int countValues = 0;
			for(Integer keyId:indexMap.keySet())
				countValues+=indexMap.get(keyId).keySet().size();

			//First Line in index file
			String line = "TotalKey:"+indexMap.keySet().size()+",TotalValue:"+countValues;
			bw.write(line);
			bw.newLine();

			//Postings TermId#Count->DocId1,DocId2
			for(Integer keyId:indexMap.keySet())
			{
				line = "";
				for(Integer valueId:indexMap.get(keyId).keySet())
				{
					line+=valueId+"#"+indexMap.get(keyId).get(valueId)+",";
				}
				line = keyId+"#"+indexMap.get(keyId).keySet().size()+"->"+line;
				line = line.substring(0,line.length()-1);
				bw.write(line);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		indexMap.clear();
		indexMap=null;
	}

}
