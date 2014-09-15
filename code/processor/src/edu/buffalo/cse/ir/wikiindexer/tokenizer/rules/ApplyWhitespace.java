package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.WHITESPACE)
public class ApplyWhitespace implements TokenizerRule{

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub

		stream.reset();
		while(stream.hasNext()){
			String nextStream=stream.next();
			String as=nextStream;
			//System.out.println("whitespace "+nextStream);
			nextStream = nextStream.trim();
			if(nextStream.matches("")){
				//System.out.println("del:"+nextStream+":as:"+as+":");
				stream.previous();
				stream.remove();
				return;
			}
			if(nextStream.contains("\n")){
				nextStream=nextStream.replace("\n", "");
			}
			if(nextStream.contains("\r")){
				nextStream=nextStream.replace("\r", "");
			}

			String[] splittedtext=nextStream.split(" ");
			stream.previous();
			for(int i=0;i<splittedtext.length; i++){
				splittedtext[i] = splittedtext[i].trim();
			}
			stream.set(splittedtext);
			stream.next();
			//stream.remove();

		}
		//stream.reset();
	}
}
