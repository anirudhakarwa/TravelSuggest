package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.HYPHEN)
public class ApplyHypen implements TokenizerRule{

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
			if(token.trim().equals("-")){
				stream.previous();
				stream.remove();
			}
			else if(token.trim().equals("--")){
				stream.previous();
				stream.remove();
			}
			else{
				token=checkHypen(token);
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

	public String checkHypen(String word){

		if(word.contains("--")){
			return word.replaceAll("--", "");
		}
		else if(word.contains("-")){
			String[] neighbour=word.split("-");
			if(neighbour.length==2){
				if(neighbour[0].matches("[A-Za-z]*") && neighbour[1].matches("[A-Za-z]*")){
					return neighbour[0]+" "+neighbour[1];
				}
				else{
					return word;
				}
			}
			else if(neighbour.length==1){
				if(neighbour[0].matches("[A-Za-z]*")){
					return neighbour[0];
				}
				else{
					return "";
				}
			}
			else{
				return "";
			}
			//else if((neighbour[0].matches("[A-Za-z]*") && neighbour[1].matches("[ ]*")) || (neighbour[1].matches("[A-Za-z]*") && neighbour[0].matches("[ ]*"))){
			//	return neighbour[0]+neighbour[1];
			//}
		}
		return word;
	}
}
