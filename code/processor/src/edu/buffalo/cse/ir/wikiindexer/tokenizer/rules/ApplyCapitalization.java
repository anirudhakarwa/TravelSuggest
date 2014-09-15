package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.CAPITALIZATION)
public class ApplyCapitalization implements TokenizerRule{

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		stream.reset();
		String token="";
		int ii=0;
		boolean isstart=false;
		while(stream.hasNext()){
			token=stream.next();
			token=token.trim();
			if(token.matches("")){
				stream.previous();
				stream.remove();
				return;
			}
			if(ii==0){
				token=(token.charAt(0)+"").toLowerCase()+token.substring(1, token.length());
				ii++;
				stream.previous();
				stream.set(token);
			}
			else if(isstart){
				token=(token.charAt(0)+"").toLowerCase()+token.substring(1, token.length());
				isstart=false;
				stream.previous();
				stream.set(token);
			}
			else if((token.charAt(token.length()-1)+"").equals(".")){
				isstart=true;
			}
		}
		
		/*
		String token1,token2;
		while(stream.hasNext()){
			token1=stream.next();
			if(containshypen(token1)){
				String words[]=token1.split("-");
				if(!isCamelCased(words[0], words[1])){
					stream.set(token1.toLowerCase());
				}
			}
			else{
				token2=stream.next();

				if(isCamelCased(token1,token2)){
					stream.set(token1,token2);
				}
				else{
					if(!isWordCapital(token1)){
						token1=token1.toLowerCase();
					}
					if(!isWordCapital(token2)){
						token2=token2.toLowerCase();
					}
				}
			}
		}
		*/
		//stream.reset();
	}

	public boolean isCamelCased(String a, String b){
		return String.valueOf(a.charAt(0)).matches("[A-Z]*") && String.valueOf(b.charAt(0)).matches("[A-Z]*");
	}
	public boolean isWordCapital(String word){
		return word.matches("[A-Z]*");
	}
	public boolean containshypen(String word){
		return word.contains("-");
	}

}
