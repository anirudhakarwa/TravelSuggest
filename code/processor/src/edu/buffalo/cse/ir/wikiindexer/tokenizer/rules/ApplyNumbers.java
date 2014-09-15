package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.NUMBERS)
public class ApplyNumbers implements TokenizerRule{

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
			//System.out.println(token+" -- ");
			if(token.split(" ").length>2){
				if(checknumber(token)){
					token=token.replaceAll("[0-9]+[.|,]*[0-9]*", "");
					token=token.replaceAll("  ", " ");
					stream.previous();
					stream.set(token);
					stream.next();
				}
			}
			else{
				if(checknumber(token)){
					stream.previous();
					stream.remove();
					stream.next();
				}
			}
		}
		//stream.reset();
	}

	public boolean checknumber(String word){
		
		if(word.matches("[0-9]+")&&word.length()==8){
			if(Integer.parseInt(word.substring(4, 6))>0&&Integer.parseInt(word.substring(4, 6))<13 && Integer.parseInt(word.substring(6, 8))>0&&Integer.parseInt(word.substring(6, 8))<32){
				return false;
			}
		}
		Pattern p = Pattern.compile("[0-9]+[.|,]*[0-9]*");
		Matcher m = p.matcher(word);

		if (m.find()) {
			return true;
		}
		else{
			return false;
		}
	}

}
