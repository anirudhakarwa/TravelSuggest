package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class ApplySpecialChars implements TokenizerRule{

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
			if(token.equals("&")){
				stream.previous();
				stream.remove();

			}
			else if(token.equals("|")){
				stream.previous();
				stream.remove();
			}
			else if(token.equals("<")){
				stream.previous();
				stream.remove();
			}
			else if(token.equals(">")){
				stream.previous();
				stream.remove();
			}
			else if(token.equals("=")){
				stream.previous();
				stream.remove();
			}
			else if(token.equals("+")){
				stream.previous();
				stream.remove();
			}
			else if(token.equals("__/\\__")){
				stream.previous();
				stream.remove();
			}
			else{
				/*String arroftokens=removespecialchars(token);
				if(!arroftokens.equals(token)){
					stream.previous();
					stream.set(arroftokens);
				}
				 */
				String arroftokens[]=splitspecialchars(token);
				for(int i=0;i<arroftokens.length; i++){
					if(!arroftokens[i].matches("")){
						arroftokens[i]=removespecialchars(arroftokens[i]);
					}
				}
				//System.out.println(token+"-"+arroftokens.length+"--");
				
				if(arroftokens.length==0){
					stream.previous();
					stream.remove();
				}
				else if(!arroftokens[0].equals(token)){
					if(arroftokens[0].matches("") && arroftokens.length==1){
						stream.previous();
						stream.remove();
					}
					else{
						stream.previous();
						stream.set(arroftokens);
						stream.next();
					}
				}
			}
		}
		//stream.reset();
	}

	public String[] splitspecialchars(String token){
		if(token.contains("@")){
			return token.split("@");
		}
		else if(token.contains("^")){
			return token.split("\\^");
		}
		else if(token.contains("*")){
			return token.split("\\*");
		}
		else if(token.contains("&")){
			return token.split("&");
		}
		else if(token.contains("+")){
			return token.split("\\+");
		}
		else if(token.contains("#")){
			return token.split("#");
		}
		else{
			String[] ans={token};
			return ans;
		}
	}

	public String removespecialchars(String token){
		token=token.replaceAll("[#]", "");
		token=token.replaceAll("[:]", "");
		token=token.replaceAll("[|]", "");
		token=token.replaceAll("[$]", "");
		token=token.replaceAll("[%]", "");
		token=token.replaceAll("[(]", "");
		token=token.replaceAll("[)]", "");
		token=token.replaceAll("[~]", "");
		token=token.replaceAll("[<]", "");
		token=token.replaceAll("[>]", "");
		token=token.replaceAll("[_]", "");
		token=token.replaceAll("[;]", "");
		//if(!token.matches("") && token.length()!=1){
			//if((token.charAt(0)+"").equals("-")){	
				//token=token.replaceAll("[-]", "");
			//}
		//}
		token=token.replace("\\", "");
		token=token.replace("/", "");

		return token;

	}
}
