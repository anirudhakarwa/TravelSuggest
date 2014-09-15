/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * @author nikhillo
 * An abstract class that represents a dictionary object for a given index
 */
public abstract class Dictionary implements Writeable {

	private INDEXFIELD field;
	private Properties props;
	private TreeMap<String,Integer> dict;
	private static int id=0;

	public Dictionary (Properties props, INDEXFIELD field) {
		this.props = props;
		this.field = field;
		dict = new TreeMap<String, Integer>();
	}



	public static int getId() {
		return id;
	}



	public static void setId(int id) {
		Dictionary.id = id;
	}



	public TreeMap<String, Integer> getDict() {
		return dict;
	}

	public void setDict(TreeMap<String, Integer> dict) {
		this.dict = dict;
	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#writeToDisk()
	 */
	public void writeToDisk() throws IndexerException {
		File dictDir = new File("files/dict");
		if(dictDir.exists())
			dictDir.delete();		
		dictDir.mkdirs();

		if(field==INDEXFIELD.TERM)
		{
			File dictFile = null;
			String firstKey = dict.keySet().iterator().next();
			int partitionNum = Partitioner.getPartitionNumber(firstKey);
			if(partitionNum>0)
			{
				char ch = (char)(partitionNum+96);
				dictFile = new File("files/dict/"+ch+".dat");
			}
			else
				dictFile = new File("files/dict/non_alpha.dat");
			writeIndexes(dictFile);
		}
		else if(field==INDEXFIELD.AUTHOR)
		{
			File dictFile = new File("files/dict/author.dat");
			writeIndexes(dictFile);
		}
		else if(field==INDEXFIELD.CATEGORY)
		{
			File dictFile = new File("files/dict/category.dat");
			writeIndexes(dictFile);
		}
		else if(field==INDEXFIELD.LINK)
		{
			File dictFile = new File("files/dict/doc.dat");
			writeIndexes(dictFile);
		}
	}

	public void writeIndexes(File indexFile)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile));

			//Dictionary: Term,TermId
			String line="";
			for(String term:dict.keySet())
			{
				line = term+","+dict.get(term);
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
	public void cleanUp() 
	{
		dict.clear();
		dict=null;
	}

	/**
	 * Method to check if the given value exists in the dictionary or not
	 * Unlike the subclassed lookup methods, it only checks if the value exists
	 * and does not change the underlying data structure
	 * @param value: The value to be looked up
	 * @return true if found, false otherwise
	 */
	public boolean exists(String value) {
		if(dict.containsKey(value))
			return true;
		else
			return false;
	}

	/**
	 * MEthod to lookup a given string from the dictionary.
	 * The query string can be an exact match or have wild cards (* and ?)
	 * Must be implemented ONLY AS A BONUS
	 * @param queryStr: The query string to be searched
	 * @return A collection of ordered strings enumerating all matches if found
	 * null if no match is found
	 */
	public Collection<String> query(String queryStr) {
		Collection<String> resultSet = new HashSet<String>();
		queryStr = queryStr.replace("*",".*");
		for(String s:dict.keySet())
		{
			if(s.equals(queryStr)||s.matches(queryStr))
				resultSet.add(s);
		}
		if(resultSet.size()>0)
			return resultSet;
		else
			return null;
	}

	/**
	 * Method to get the total number of terms in the dictionary
	 * @return The size of the dictionary
	 */
	public int getTotalTerms() {
		return dict.size();
	}
}
