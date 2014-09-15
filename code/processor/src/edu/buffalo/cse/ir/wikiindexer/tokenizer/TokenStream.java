/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{
	
	private LinkedList stream;
	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(StringBuilder bldr) {
		//TODO: Implement this method
		stream=new LinkedList();
		if(!(bldr+"").matches("")&&!(bldr+"").equals(null+"")){
			stream.add(bldr);
		}
	}
	
	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		//TODO: Implement this method
		stream=new LinkedList();
		if(!(string+"").matches("")&&!(string+"").equals(null+"")){
			//System.out.println("constructor"+string+" 1");
			stream.add(string);
		}
	}
	
	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		//TODO: Implement this method
		if(tokens!=null){
			for(int i=0; i<tokens.length; i++){
				if(tokens[i]!=null){
					if(!tokens[i].matches("")&&!tokens[i].matches("null")){
						//System.out.println(tokens[i]);
						stream.add(tokens[i]);
					}
				}
			}
		}
	}
	
	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		//TODO: Implement this method
		stream.resetall();

		Map<String, Integer> result=new HashMap<String, Integer>();;
		String data=null;
		while(!(data=stream.getelement()+"").equals(null+"")){
			if(result.containsKey(data)){
				result.put(data, result.get(data)+1);
			}
			else{
				result.put(data, 1);
			}
		}
		if(result.size()==0){
			return null;
		}
		else{
			return result;
		}
	}
	
	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		//TODO: Implement this method
		stream.resetall();
		List<String> ans=new ArrayList<String>();
		String data=null;
		while(!(data=(stream.getelement()+"")).equals(null+"")){
			ans.add(data);
		}
		if(ans.size()==0){
			return null;
		}
		else if(ans.get(0).equals("##NO_ELEMENT##")){
			return null;
		}
		return ans;
	}
	
	/**
	 * Method to query for the given token within the stream
	 * @param token: The token to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		//TODO: Implement this method
		if(token!=null){
			if(!(token+"").equals(null+"") && !(token+"").matches("")){
				Map<String, Integer> ans=getTokenMap();
				if(ans!=null){
					if(ans.containsKey(token)){
						return ans.get(token);
					}
					else{
						return 0;
					}
				}
				else{
					return 0;
				}
			}
			else{
				return 0;
			}
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		// TODO: Implement this method
		return stream.hasNextfortoken();
	}
	
	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		//TODO: Implement this method
		return stream.hasPrev();
	}
	
	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String next() {
		// TODO: Implement this method
		Object ans=stream.nextfortoken();
		if(ans==null){
			return null;
		}
		else{
			return ans.toString();
		}
	}
	
	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		//TODO: Implement this method
		Object ans=stream.prev();
		if(ans==null){
			return null;
		}
		else{
			return ans.toString();
		}
	}
	
	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		// TODO: Implement this method
		stream.remove();
	}
	
	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		//TODO: Implement this method
		return stream.mergewithprev();
	}
	
	/**
	 * Method to merge the current token with the next token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		//TODO: Implement this method
		return stream.mergewithnext();
	}
	
	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		//TODO: Implement this method
		stream.set(newValue);
	}
	
	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */
	public void reset() {
		//TODO: Implement this method
		stream.reset();
	}
	
	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */
	public void seekEnd() {
		stream.seekend();
	}
	
	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		//TODO: Implement this method
		if(other!=null){
			stream.merge(other);
		}
	}
	
	public void display(){
		reset();
		int count=0;
		while(hasNext()){
			count++;
			System.out.print(next()+" ");
		}
		reset();
		System.out.println();
		System.out.println("count "+count);
	}
}
