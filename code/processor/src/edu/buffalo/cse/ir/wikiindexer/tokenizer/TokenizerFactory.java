/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyAccents;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyApostrophe;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyCapitalization;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyDates;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyHypen;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyNumbers;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyPunctuation;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplySpecialChars;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyStopwords;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApplyWhitespace;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer;

/**
 * Factory class to instantiate a Tokenizer instance
 * The expectation is that you need to decide which rules to apply for which field
 * Thus, given a field type, initialize the applicable rules and create the tokenizer
 * @author nikhillo
 *
 */
public class TokenizerFactory {
	//private instance, we just want one factory
	private static TokenizerFactory factory;
	
	//properties file, if you want to read soemthing for the tokenizers
	private static Properties props;
	
	/**
	 * Private constructor, singleton
	 */
	private TokenizerFactory() {
		//TODO: Implement this method
	}
	
	/**
	 * MEthod to get an instance of the factory class
	 * @return The factory instance
	 */
	public static TokenizerFactory getInstance(Properties idxProps) {
		if (factory == null) {
			factory = new TokenizerFactory();
			props = idxProps;
		}
		
		return factory;
	}
	
	/**
	 * Method to get a fully initialized tokenizer for a given field type
	 * @param field: The field for which to instantiate tokenizer
	 * @return The fully initialized tokenizer
	 */
	public Tokenizer getTokenizer(INDEXFIELD field) {
		//TODO: Implement this method
		/*
		 * For example, for field F1 I want to apply rules R1, R3 and R5
		 * For F2, the rules are R1, R2, R3, R4 and R5 both in order
		 * So the pseudo-code will be like:
		 * if (field == F1)
		 * 		return new Tokenizer(new R1(), new R3(), new R5())
		 * else if (field == F2)
		 * 		return new TOkenizer(new R1(), new R2(), new R3(), new R4(), new R5())
		 * ... etc
		 */
		if(field==INDEXFIELD.AUTHOR){
			try {
				return new Tokenizer(new ApplySpecialChars());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(field==INDEXFIELD.CATEGORY){
			try {
				return new Tokenizer(/*new ApplyApostrophe(),*/ 
						new ApplySpecialChars(), new ApplyStopwords());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(field==INDEXFIELD.LINK){
			try {
				return new Tokenizer(new ApplyWhitespace());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(field==INDEXFIELD.TERM){
			try {
				return new Tokenizer(new ApplyWhitespace(), new ApplyCapitalization(), new ApplyPunctuation(), new ApplyApostrophe(), 
						new ApplyDates(), new ApplyNumbers(), new ApplyHypen(), new ApplySpecialChars(), new ApplyAccents(), new ApplyStopwords(), new EnglishStemmer());
			} catch (TokenizerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
