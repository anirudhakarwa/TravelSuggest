package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class ApplyDates implements TokenizerRule{

	Map<String, String> months=new HashMap<String, String>();
	String tocopy="";
	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub
		createList();
		stream.reset();
		while(stream.hasNext()){
			String month=stream.next();
			month=month.trim();
			if(month.matches("")){
				stream.previous();
				stream.remove();
				return;
			}
			if(month.split(" ").length>1){
				String tokens[]=month.split(" ");
				TokenStream ss=new TokenStream(tokens[0]);
				for(int i=1; i<tokens.length; i++){
					ss.append(tokens[i]);
				}
				ss=sol(ss);
				ss.reset();
				String ans="";
				while(ss.hasNext()){
					ans=ans+ss.next()+" ";
				}
				ans=ans.substring(0, ans.length()-1);
				stream.previous();
				stream.set(ans);
				stream.next();
			}
			else{
				stream.previous();
				stream=sol(stream);
			}

		}
		//stream.reset();
	}

	private TokenStream sol(TokenStream stream){

		while(stream.hasNext()){
			String month=stream.next();
			//System.out.print(month+"--");
			if(month.matches("[0-9]+") && month.length()==4){
				//System.out.println(month+"");
				month=month+"0101";
				//System.out.println(month+"");
				stream.previous();
				stream.set(month);
				stream.next();
			}
			else if(month.contains("Ð")){
				String ini=month.split("Ð")[0];
				ini=ini+"0101";
				String end=month.split("Ð")[1];
				end=end.substring(0, end.length()-1);
				end="20"+end+"0101.";
				stream.previous();
				stream.set(ini+"Ð"+end);
				stream.next();
			}
			else if(months.containsKey(month)){

				String year=stream.next();
				if(year.length()==4){
					stream.previous();//year
					stream.previous();//month
					String day=stream.previous();
					if(day.length()==1){
						day="0"+day;
					}
					String token=year+months.get(month)+day;
					if(tocopy.matches("")){
						stream.set(token);
					}
					else{
						stream.set(token+" "+tocopy);
					}
					stream.next();//combined
					stream.remove();
					stream.remove();

					break;
				}
				else{
					String day=year;
					year=stream.next();
					//System.out.println("yeaarrr "+year);
					day=day.replace(",", "");
					if(day.length()==1){
						day="0"+day;
					}
					boolean ans=false;
					if((year.charAt(year.length()-1)+"").equals(",")){
						ans=true;
					}
					year=year.replace(",", "");
					boolean notyear=false;
					if(!year.matches("[0-9]+")){
						//System.out.println("11111"+year);
						year="1900";
						notyear=true;
					}
					String token="";
					if(ans){
						token=year+months.get(month)+day+",";
					}
					else{
						token=year+months.get(month)+day;
						//System.out.println(token+" hehehe");
					}
					stream.previous();
					stream.previous();
					stream.previous();
					stream.set(token);
					stream.next();
					if(!notyear){
						stream.remove();
					}
					stream.remove();
				}
			}
			else{
				if(month.matches("[[0-9]+[:]*]+")){

					if(month.contains(":")){
						String UTC=stream.next();
						if(UTC.equals("UTC")){
							//System.out.println("hi");
							tocopy=month;
							stream.previous();
							stream.previous();
							stream.remove();
							stream.remove();
							stream.remove();
							stream.remove();

						}
						else{
							stream.previous();
							month=appendtime(month);
							stream.previous();
							stream.set(month);
							stream.next();
							stream.remove();
						}
					}
					else{
						if(month.length()==4){
							stream.previous();
							stream.set(month+"0101");
							stream.next();
						}
						else{
							String year=month;
							String dec=stream.next();
							if(dec.contains("BC")){
								year=appendzero(year);
								String token="";
								token="-"+year+"0101";
								stream.previous();
								stream.previous();
								stream.set(token);
								stream.next();
								stream.remove();
							}
							else if(dec.contains("AD")){

								year=appendzero(year);
								//System.out.println(year);
								String token="";

								if(dec.contains(".")){
									token=year+"0101.";
								}
								else{
									token=year+"0101";
								}
								stream.previous();
								stream.previous();
								stream.set(token);
								stream.next();
								stream.remove();
							}
							else{
								stream.previous();
							}
						}
					}
				}
				else{
					
					Pattern pattern = Pattern.compile("[[0-9]+[:]*]+PM");
					Matcher matcher = pattern.matcher(month);
					boolean b=false;
					if (matcher.find()) {
					    b=true;
					}
					
					if(month.contains("PM") && month.length()>6 && month.length()<9 && b){

						month=month.replace("PM", "");
						month=month.replace(".", "");
						String[] all=month.split(":");
						int tt=12+Integer.parseInt(all[0]);
						String token=tt+":"+all[1]+":00.";
						stream.previous();
						stream.set(token);
						stream.next();
					}

				}
			}
		}
		return stream;

	}

	private String appendtime(String word){
		if(word.length()==5){
			word=word+":00.";
		}
		return word;
	}

	private String appendzero(String word){
		if(word.length()==1){
			word="000"+word;
		}
		else if(word.length()==2){
			word="00"+word;
		}
		else if(word.length()==3){
			word="0"+word;
		}
		return word;
	}
	private void createList(){
		months=new HashMap<String, String>();
		months.put("January", "01");
		months.put("February", "02");
		months.put("March", "03");
		months.put("April", "04");
		months.put("May", "05");
		months.put("June", "06");
		months.put("July", "07");
		months.put("August", "08");
		months.put("September", "09");
		months.put("October", "10");
		months.put("November", "11");
		months.put("December", "12");

	}

}