package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class ApplyPunctuation implements TokenizerRule {

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		stream.reset();
		while(stream.hasNext()){
			String token=stream.next();
			token=token.trim();
			if(token.matches("")){
				stream.previous();
				stream.remove();
				return;
			}
			if(token.length()==1 && token.equals(".")){
				token="";
			}
			//System.out.println(token);
			if(!token.matches("[[0-9]+[.]+]+") && !checkboolean(token) && !inmiddle(token)){
				if(!token.matches("[[.]+[0-9]+]+[?]")){
					token=token.replaceAll("[.]", "");
				}
				token=token.replaceAll("[?]+", "");
				token=token.replaceAll("[!]+", "");
				token=token.replaceAll("[,]+", "");
				if(token.matches("")){
					stream.previous();
					stream.remove();
				}
				else{
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}
		}
		//stream.reset();
	}

	public boolean inmiddle(String word){
		if((word.lastIndexOf("?")<word.length()-1 && word.lastIndexOf("?")>0)&&(word.lastIndexOf("!")!=(word.length()-1))){
			return true;
		}
		return false;
	}
	public boolean checkboolean(String word){
		try{
			if(word.contains("true")|| word.contains("false")){
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception e){
			return false;
		}
	}
	/*
	public boolean checkpunctuation(String word){
		String ans=String.valueOf(word.charAt(word.length()-1));
		if(ans.equals(".") || ans.equals("?") || ans.equals("!"))
			return true;
		else
			return false;
		return word.contains(".") || word.contains("?") || word.contains("!");
	}
	 */

}
