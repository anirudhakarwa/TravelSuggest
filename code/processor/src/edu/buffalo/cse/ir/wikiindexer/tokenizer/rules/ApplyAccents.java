package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)
public class ApplyAccents implements TokenizerRule{

	Map<String, String> tokens=new HashMap<String, String>();

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		createList();
		stream.reset();
		while(stream.hasNext()){
			String ans=stream.next();
			ans=ans.trim();
			if(ans.matches("")){
				stream.previous();
				stream.remove();
				return;
			}
			Object[] key=tokens.keySet().toArray();
			for(int i=0 ;i<tokens.size(); i++){
				if(ans.contains(key[i].toString())){
					ans=ans.replaceAll(key[i].toString(), tokens.get(key[i].toString()));
				}
			}

			stream.previous();
			stream.set(ans);
			stream.next();

		}
		//stream.reset();
	}

	private void createList(){
		tokens.put("â", "a");
		tokens.put("ô", "o");
		tokens.put("é", "e");
		tokens.put("а̀", "a");
		tokens.put("à", "a");
		tokens.put("è", "e");
		tokens.put("û", "u");
		tokens.put("ü", "u");
		tokens.put("ë", "e");
		tokens.put("nа̀ра", "naра");
		tokens.put("nара̀", "napa");
		
	}
}
