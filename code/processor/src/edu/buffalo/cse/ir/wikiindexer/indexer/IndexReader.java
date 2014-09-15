/**`
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author nikhillo
 * This class is used to introspect a given index
 * The expectation is the class should be able to read the index
 * and all associated dictionaries.
 */
public class IndexReader {

	private INDEXFIELD field;
	/**
	 * Constructor to create an instance 
	 * @param props: The properties file
	 * @param field: The index field whose index is to be read
	 */
	public IndexReader(Properties props, INDEXFIELD field) {
		//TODO: Implement this method
		this.field=field;
	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		//TODO: Implement this method
		return getTotal(0);
	}

	private int getTotal(int index){
		BufferedReader br = null;
		int total=0;
		if(field==INDEXFIELD.TERM){
			try {
				String sCurrentLine;
				for(int i=0;i<26; i++){
					br = new BufferedReader(new FileReader("files\\index\\"+(char)(i+97)+".idx"));
					if((sCurrentLine = br.readLine()) != null) {
						if(sCurrentLine.contains("TotalKey")){
							total+=Integer.parseInt(sCurrentLine.split(",")[index].split(":")[1].trim());
						}
					}
				}
				br = new BufferedReader(new FileReader("files\\index\\non_alpha.idx"));
				if((sCurrentLine = br.readLine()) != null) {
					if(sCurrentLine.contains("TotalKey")){
						total+=Integer.parseInt(sCurrentLine.split(",")[index].split(":")[1].trim());
					}
				}
				br.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			String file="";
			if(field==INDEXFIELD.AUTHOR){
				file="author";
			}
			else if(field==INDEXFIELD.CATEGORY){
				file="category";
			}
			else if(field==INDEXFIELD.LINK){
				file="doc";
				if(index==0)
                {
                        try {
                                String sCurrentLine="";
                                br = new BufferedReader(new FileReader("files\\dict\\"+file+".dat"));
                                int count=0;
                                while((sCurrentLine=br.readLine())!=null)
                                        count++;
                                br.close();
                                return count;
                        }
                        catch (IOException e) {
                                e.printStackTrace();
                        }
                }
			}
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader("files\\index\\"+file+".idx"));
				if((sCurrentLine = br.readLine()) != null) {
					if(sCurrentLine.contains("TotalKey")){
						total+=Integer.parseInt(sCurrentLine.split(",")[index].split(":")[1]);
					}
				}
				br.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return total;
	}
	/**
	 * Method to get the total number of terms in the value dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		//TODO: Implement this method
		return getTotal(1);
	}

	private String getTermID(String key,String file){
		String sCurrentLine="";
		try{
			BufferedReader br = new BufferedReader(new FileReader("files\\dict\\"+file+".dat"));
			String arr[]=null;
			while((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.split(",")[0].trim().equals(key)) //Should we consider case sensitive or not
				{
					break;
				}
			}
			br.close();
			if(sCurrentLine!=null){
				return sCurrentLine.split(",")[1].trim();
			}
			else{
				return null;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * @param key: The dictionary term to be queried
	 * @return The postings list with the value term as the key and the
	 * number of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
		key=key.toLowerCase();
		BufferedReader br = null;
		int total=0;
		Map<String, Integer> mapping=new HashMap<String, Integer>();


		String file="";
		if(field==INDEXFIELD.TERM){
			if(file.matches("[0-9]+")){
				file="non_alpha";
			}
			else{
				file=key.charAt(0)+"";
			}
		}
		if(field==INDEXFIELD.AUTHOR){
			file="author";
		}
		else if(field==INDEXFIELD.CATEGORY){
			file="category";
		}
		else if(field==INDEXFIELD.LINK){
			file="doc";
		}
		try {
			String search=getTermID(key,file);
			if(search==null){
				return null;
			}
			String sCurrentLine;
			br = new BufferedReader(new FileReader("files\\index\\"+file+".idx"));
			String arr[]=null;
			while((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains("->")){
					arr=sCurrentLine.split("->");
					if(search.equals(arr[0].split("#")[0])){
						break;
					}
				}
			}
			String[] list=arr[1].split(",");
			for(int i=0;i<list.length; i++){
				mapping.put(list[i].split("#")[0].trim(), Integer.parseInt(list[i].split("#")[1].trim()));
			}
			br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		if(mapping.size()==0){
			return null;
		}
		return mapping;
	}

	/**
	 * Method to get the top k key terms from the given index
	 * The top here refers to the largest size of postings.
	 * @param k: The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the requirement
	 * If k is more than the total size of the index, return the full index and don't 
	 * pad the collection. Return null in case of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		//TODO: Implement this method
		if(k<=0){
			return null;
		}
		Map<String, Integer> ans=new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(ans);
		Map<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
		BufferedReader br=null;
		String file="";
		if(field==INDEXFIELD.TERM){
			try {
				String sCurrentLine;
				for(int i=0;i<26; i++){
					br = new BufferedReader(new FileReader("files\\index\\"+(char)(i+97)+".idx"));
					int co=0;
					while((sCurrentLine = br.readLine()) != null) {
						if(co!=0){
							ans.put(sCurrentLine.split("->")[0].split("#")[0].trim(), Integer.parseInt(sCurrentLine.split("->")[0].split("#")[1].trim()));
						}
						else{
							co++;
						}
					}
				}
				br.close();
				br = new BufferedReader(new FileReader("files\\index\\non_alpha.idx"));
				int co=0;
				while((sCurrentLine = br.readLine()) != null) {
					if(co!=0){
						ans.put(sCurrentLine.split("->")[0].split("#")[0].trim(), Integer.parseInt(sCurrentLine.split("->")[0].split("#")[1].trim()));
					}
					else{
						co++;
					}
				}
				br.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			sorted_map.putAll(ans);
			//System.out.println(sorted_map);
			return topk(sorted_map,k);
		}
		if(field==INDEXFIELD.AUTHOR){
			file="author";
		}
		else if(field==INDEXFIELD.CATEGORY){
			file="category";
		}
		else if(field==INDEXFIELD.LINK){
			file="doc";
		}

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("files\\index\\"+file+".idx"));
			int co=0;
			while((sCurrentLine = br.readLine()) != null) {
				if(co!=0){
					ans.put(sCurrentLine.split("->")[0].split("#")[0], Integer.parseInt(sCurrentLine.split("->")[0].split("#")[1]));
				}
				else{
					co++;
				}
			}
			br.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		sorted_map.putAll(ans);
		return topk(sorted_map,k);
	}

	private Collection<String> topk(Map<String,Integer> sorted, int k){
		List<String> ans=new ArrayList<String>();
		Object[] keys=sorted.keySet().toArray();

		for(int i=0;i<k; i++){
			ans.add(getTerm(keys[i].toString()));

		}
		return ans;
	}

	private String getTerm(String id){
		BufferedReader br=null;
		String file="";
		if(field==INDEXFIELD.TERM){
			try {
				String sCurrentLine;
				String ans="";
				boolean b=false;
				here: for(int i=0;i<26; i++){
					br = new BufferedReader(new FileReader("files\\dict\\"+(char)(i+97)+".dat"));
					while((sCurrentLine = br.readLine()) != null) {
						if(sCurrentLine.split(",")[1].toString().trim().equals(id)){
							ans= sCurrentLine.split(",")[0];
							b=true;
							break here;
						}
					}
				}
				br.close();
				if(b){
					return ans;
				}
				else{
					br = new BufferedReader(new FileReader("files\\dict\\non_alpha.dat"));
					b=false;
					ans="";
					while((sCurrentLine = br.readLine()) != null) {
						if(sCurrentLine.split(",")[1].toString().trim().equals(id)){
							ans= sCurrentLine.split(",")[0];
							b=true;
							break;
						}
					}
					br.close();
					if(b){
						return ans;
					}
					else{
						return null;
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			if(field==INDEXFIELD.AUTHOR){
				file="author";
			}
			else if(field==INDEXFIELD.CATEGORY){
				file="category";
			}
			else if(field==INDEXFIELD.LINK){
				file="doc";
			}

			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader("files\\dict\\"+file+".dat"));
				while((sCurrentLine = br.readLine()) != null) {
					if(sCurrentLine.split(",")[1].toString().trim().equals(id)){
						String ans= sCurrentLine.split(",")[0].toString();
						br.close();
						return ans;
					}
				}
				br.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * Method to execute a boolean AND query on the index
	 * @param terms The terms to be queried on
	 * @return An ordered map containing the results of the query
	 * The key is the value field of the dictionary and the value
	 * is the sum of occurrences across the different postings.
	 * The value with the highest cumulative count should be the
	 * first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		//TODO: Implement this method (FOR A BONUS)
		for(int i=0;i<terms.length; i++){
			terms[i]=terms[i].toLowerCase();
		}
		Map<String, Integer> ans=new HashMap<String,Integer>();
		ValueComparator bvc =  new ValueComparator(ans);
		Map<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);

		for(int i=0;i<terms.length; i++){
			Map<String, Integer> inter=getPostings(terms[i]);
			if(inter!=null){
				Object arr[]=inter.keySet().toArray();
				for(int j=0;j<arr.length; j++){
					if(i==0){
						ans.put(arr[j].toString(), inter.get(arr[j]));
					}
					else{
						if(ans.containsKey(arr[j].toString())){
							ans.put(arr[j].toString(), ans.get(arr[j].toString())+inter.get(arr[j].toString()));
						}
						else{
							ans.remove(arr[j].toString());
						}
					}
				}
			}
		}
		sorted_map.putAll(ans);
		return sorted_map;
	}

	class ValueComparator implements Comparator<String> {

		Map<String, Integer> base;
		public ValueComparator(Map<String, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with equals.    
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
