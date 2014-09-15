package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class ApplyApostrophe implements TokenizerRule{

	private Map<String,String> contractions;

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		createList();
		stream.reset();
		String ans[]=null;
		while(stream.hasNext()){
			String token=stream.next();
			token=token.trim();
			if(token.matches("")){
				stream.previous();
				stream.remove();
				return;
			}
			token=removeAposThere(token);
			token=token.replaceAll("\"", "");
			token=token.replaceAll("“", "");
			token=token.replaceAll("”", "");
			
			//System.out.println("token- "+token);
			ans=removecontractions(token);
			//System.out.println("token1- "+ans);

			if(ans!=null){
				stream.previous();
				stream.set(ans);
				stream.next();
				ans=null;
			}
			else{
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

	public String removeAposThere(String word){
		String a="'s";
		String b="s'";
		if(word.contains(a)){
			if(!contractions.containsKey(word)){
				return word.replace(a, "");
			}
		}
		else if(word.contains(b)){
			if(!contractions.containsKey(word)){
				return word.replace(b, "s");
			}
		}
		else if(word.contains("\'")){
			if(!contractions.containsKey(word)){
				return word.replace("\'", "");
			}
		}
		else if(word.contains("’")){
			if(!contractions.containsKey(word)){
				return word.replace("’", "");
			}
		}
		return word;
	}

	public String[] removecontractions(String word){
		String[] ans=null;
		if(contractions.containsKey(word)){
			ans=contractions.get(word).split(" ");
		}
		return ans;
	}

	public void createList(){
		contractions=new HashMap<String,String>();
		contractions.put("aren't","are not");
		contractions.put("'em","them");
		contractions.put("Should've","Should have");
		contractions.put("can't","cannot");
		contractions.put("couldn't","could not");
		contractions.put("didn't","did not");
		contractions.put("doesn't","does not");
		contractions.put("don't","do not");
		contractions.put("hadn't","had not");
		contractions.put("hasn't","has not");
		contractions.put("haven't","have not");
		contractions.put("he'd","he would");
		contractions.put("he'll","he will");
		contractions.put("he's","he is");
		contractions.put("I'd","I would");
		contractions.put("I'll","I will");
		contractions.put("I'm","I am");
		contractions.put("I've","I have");
		contractions.put("isn't","is not");
		contractions.put("it's","it is");
		contractions.put("let's","let us");
		contractions.put("mightn't","might not");
		contractions.put("mustn't","must not");
		contractions.put("shan't","shall not");
		contractions.put("she'd","she would");
		contractions.put("She'll","She will");
		contractions.put("she's","she is");
		contractions.put("shouldn't","should not");
		contractions.put("Put 'em","Put them");
		contractions.put("that's","that is");
		contractions.put("there's","there is");
		contractions.put("They'd","They would");
		contractions.put("they'll","they will");
		contractions.put("they're","they are");
		contractions.put("they've","they have");
		contractions.put("we'd","we would");
		contractions.put("we're","we are");
		contractions.put("we've","we have");
		contractions.put("weren't","were not");
		contractions.put("what'll","what will");
		contractions.put("what're","what are");
		contractions.put("what's","what is");
		contractions.put("what've","what have");
		contractions.put("where's","where is");
		contractions.put("who'd","who had");
		contractions.put("who'll","who will");
		contractions.put("who're","who are");
		contractions.put("who's","who is");
		contractions.put("who've","who have");
		contractions.put("won't","will not");
		contractions.put("wouldn't","would not");
		contractions.put("you'd","you would");
		contractions.put("you'll","you will");
		contractions.put("you're","you are");
		contractions.put("you've","you have");
	}
}
