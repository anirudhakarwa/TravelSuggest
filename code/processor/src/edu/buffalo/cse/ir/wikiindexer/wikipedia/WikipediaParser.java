/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.awt.image.TileObserver;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument.Section.Template;

/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
	public static List<String> categories;
	public static Set<String> linkSet;
	//public static Map<String,String> templateData;
	//public static Set<Map<String,String>> templateSet;
	//public static Set<Template> templates;

	public static void initializeCatList(){
		categories = new ArrayList<String>();
		linkSet = new HashSet<String>();
	}


	/* TODO */
	/**
	 * Method to parse section titles or headings.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * @param titleStr: The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public static String parseSectionTitle(String titleStr) {
		if(titleStr==null)
			return null;
		titleStr = titleStr.replaceAll("[=]+", "");
		return titleStr.trim();

	}

	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * @param itemText: The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public static String parseListItem(String itemText) {
		if(itemText==null)
			return null;
		if(itemText=="")
			return "";

		itemText = itemText.replaceAll("[*]+", "");
		itemText = itemText.replaceAll("[#]+", "");
		itemText = itemText.replaceAll("[:]+", "");
		return itemText.trim();
	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting(String text) {
		if(text==null)
			return null;
		if(text=="")
			return "";
		text = text.replaceAll("[']+", "");
		return text;
	}

	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public static String parseTagFormatting(String text) {
		if(text==null)
			return null;

		if(text=="")
			return "";

		text = text.replaceAll("<[^>]*>[ ]*", "");
		//text = text.replaceAll("\\\"", "");//TODO confirm whether &quotes; tag to be removed or not
		if(text.contains("&lt;") && text.contains("&gt;"))
			text = text.replaceAll("&[A-Za-z]+;[^&[A-Za-z]+;]*&[A-Za-z]+;[ ]*","");

		return text.trim();
	}

	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags
	 * For most cases, simply removing the tags should work.
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTemplates_old(String text) {
		if(text==null)
			return null;
		if(text=="")
			return "";
		//text = text.replaceAll("\\{\\{.*\\}\\}", "");
		text = text.replaceAll("\n", "");
		text = text.replaceAll("\\{\\{[^\\}\\}]*\\}\\}*", "");
		return text;
	}

	public static String parseTemplates_old1(String text)
	{
		if(text==null)
			return null;
		if(text=="")
			return "";
		else
		{
			Stack<Integer> indexes = new Stack<Integer>();
			int index=-1, posi=0;
			while((index = text.indexOf("{{",posi))!=-1)
			{
				if(index < text.indexOf("}}",posi))
					indexes.push(index);
				if(text.indexOf("{{",index+2)!=-1)
				{
					if(text.indexOf("{{",index+2) > text.indexOf("}}",index+2))
						text = text.replace(text.substring(indexes.pop(),text.indexOf("}}",index)+2),"");
					else 
						posi = text.indexOf("{{",index+2);
				}
				else
					text = text.replace(text.substring(indexes.pop(), text.indexOf("}}",index)+2), "");
			}
			//System.out.println(indexes.size()+" "+text.indexOf("}}"));
			if(indexes.size()>0)
				text = text.replace(text.substring(indexes.pop(), text.indexOf("}}")+2), "");
			return text;
		}
	}

	public static String parseTemplates(String text,Set<Map<String,String>> templateSet)
	{
		if(text==null)
			return null;
		if(text=="")
			return "";
		else
		{
			/*Stack<Integer> indexes = new Stack<Integer>();
			int index = -1;
			while((index = text.indexOf("{{"))!=-1)
			{
				indexes.push(index);
				while(!indexes.isEmpty())
				{
					int a = text.indexOf("{{",indexes.peek()+2);
					int b = text.indexOf("}}",indexes.peek()+2);
					if(a<b && a!=-1)
						indexes.push(a);
					else if(b!=-1)
					{
						//System.out.println(a+" "+b);
						//System.out.println(text);
						text = text.replace(text.substring(indexes.pop(), b+2), "");
					}
					//In case of missing {{ and }}
					if(a==-1 && b==-1)
					{
						//System.out.println("Stack Size:"+indexes.size());
						indexes.setSize(0);
						text = text.replace("{{", "");
						text = text.replace("}}", "");
					}
				}
			}
			return text;*/
			text = parseDiffTemplates(text, "{{","}}",2,templateSet);
			if(text.contains("{"))
			{
				//text = parseDiffTemplates(text, "{","}",1);
				//System.out.println(text);
				text = text.replaceAll("\n", " ");
				text = text.replaceAll("\\{[^\\}]*\\}", "");
				//System.out.println(text);
			}
			return text;
		}
	}

	public static String parseDiffTemplates(String text, String startMarkup, String endMarkup,int diff,Set<Map<String,String>>  templateSet)
	{
		Stack<Integer> indexes = new Stack<Integer>();
		int index = -1;
		while((index = text.indexOf(startMarkup))!=-1)
		{
			indexes.push(index);
			while(!indexes.isEmpty())
			{
				int a = text.indexOf(startMarkup,indexes.peek()+diff);
				int b = text.indexOf(endMarkup,indexes.peek()+diff);
				if(a<b && a!=-1)
					indexes.push(a);
				else if(b!=-1)
				{
					//System.out.println(a+" "+b);
					//System.out.println(text);
					String templateData = text.substring(indexes.pop(), b+diff);
					//WikipediaParser.templateData = new HashMap<String, String>();
					parseTemplateData(templateData,templateSet);
					text = text.replace(templateData, "");
				}
				//In case of missing {{ and }}
				if(a==-1 && b==-1)
				{
					//System.out.println("Stack Size:"+indexes.size());
					indexes.setSize(0);
					text = text.replace(startMarkup, "");
					text = text.replace(endMarkup, "");
				}
			}
		}
		return text;
	}

	public static void parseTemplateData(String templateData,Set<Map<String,String>> templateSet)
	{
		if(templateData!=null && !templateData.matches("") && templateData.contains("name"))
		{
			System.out.println(templateData);
			Map<String,String> templateProp = new HashMap<String, String>();
			templateData = templateData.replace("{{", "");
			templateData = templateData.replace("}}", "");
			String[] templateCont = templateData.split("\\|");
			for(String s:templateCont)
				if(s.split("=").length==2)
				{
					String key = s.split("=")[0].trim();
					String value = s.split("=")[1].trim();
					if(!key.matches("") && !value.matches(""))
						templateProp.put(key,value);
				}
			templateSet.add(templateProp);
		}
	}

	/* TODO */
	/**
	 * Method to parse links and URLs.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * @param text: The text to be parsed
	 * @return An array containing two elements as follows - 
	 *  The 0th element is the parsed text as visible to the user on the page
	 *  The 1st element is the link url
	 */
	public static String[] parseLinks(String text) 
	{
		if(text=="" || text==null)
			return new String[]{"",""};

		text = text.replaceAll("<[^>]*>", "");
		//text = text.replace(".", "");

		if(text.contains("[[") && text.contains("]]"))
		{
			int indexOfLinkMarUpStart = text.indexOf("[[");
			int indexOfLinkMarUpEnd = text.indexOf("]]");

			String linkTxt = text.substring(indexOfLinkMarUpStart+2,indexOfLinkMarUpEnd);

			String display = linkTxt;
			String link = linkTxt;

			if(linkTxt.contains("|"))
			{
				String[] linkSplit = linkTxt.split("\\|"); 
				if(linkSplit.length>1)
					display = linkSplit[linkSplit.length-1].trim();
				else
					display = "";
				link = linkSplit[0].trim();
			}

			boolean flag=false;
			if(link.startsWith("File:") || link.startsWith("media:"))
			{
				link="";
				flag=true;
				if(!linkTxt.contains("|"))
					display="";
			}
			else if(link.startsWith("Wiktionary:"))
			{
				if(display=="")
				{
					display = link.substring("Wikitionary:".length()-1);
				}
				link="";
				flag=true;
			}
			else if(link.startsWith("Wikipedia:"))
			{
				if(link.contains("#"))
					display = link;
				if(display=="")
					display = link.substring(link.indexOf(":")+1);
				if(display.contains("(") && display.contains(")"))
					display = display.replaceAll("\\([A-Za-z0-9]+\\)","").trim();	
				link = "";
				flag=true;
			}
			else if(link.startsWith("Category:") || link.startsWith(":"))
			{
				if(!linkTxt.contains("|"))
					display = link.substring(link.indexOf(":")+1);

				if(link.startsWith("Category:"))
				{
					if(categories==null)
						categories=new ArrayList<String>();
					//if(display!=null && !display.matches(""))
					categories.add(display);
				}
				link="";
			}
			else if(link.contains(":") && link.substring(0, link.indexOf(":")).length()==2)//Language Link
			{
				display = link;
				link = "";
			}
			//System.out.println(link+" "+display);
			if((display=="" || display.length()==0) && !flag)
			{
				char splitBy= ' ';
				if(display.contains(",") || linkTxt.contains(","))
					splitBy = ',';
				display = linkTxt.substring(0,linkTxt.indexOf(splitBy));
			}

			if(!link.matches(""))
			{
				link = link.replace(" ", "_");
				//link = link.replace(link.substring(0, 1),link.substring(0,1).toUpperCase())
				link = Character.toUpperCase(link.charAt(0)) + link.substring(1);
			}
			text = text.replace("[["+linkTxt+"]]",display);
			return new String[]{text,link};
		}
		else if(text.contains("[") && text.contains("]"))
		{
			String link = text.substring(text.indexOf("[")+1, text.indexOf("]"));
			String display="";
			if(link.contains(" "))
			{
				display = link.substring(link.indexOf(" ")+1);
				//display = link.split(" ")[1].trim();
			}
			text = text.replace("["+link+"]",display);
			return new String[]{text,""};
		}
		return new String[]{"",""};
	}

	public static void parsePageText(Collection<String> pageText, Collection<WikipediaDocument> wikiDocs)
	{
		Iterator<String> pageItr = pageText.iterator();
		Iterator<WikipediaDocument> wikiDocsItr = wikiDocs.iterator();

		while(pageItr.hasNext() && wikiDocsItr.hasNext())
		{
			String pgTxt = pageItr.next();
			WikipediaDocument wikiDoc = wikiDocsItr.next();
			//System.out.println(wikiDoc.getAuthor());
			//Parse all html tags
			pgTxt = parseTagFormatting(pgTxt);
			//Parse Templates tag
			//pgTxt = parseTemplates(pgTxt);
			//System.out.println(pgTxt);
			//Parse Bold and italics
			pgTxt = parseTextFormatting(pgTxt);
			//Parse all links
			//Set<String> linkSet  = new HashSet<String>();
			//Initialize Category List
			initializeCatList();
			pgTxt = parseLinksText(pgTxt);
			wikiDoc.addLInks(linkSet);
			//System.out.println(linkSet);
			wikiDoc.addCategories(categories);

			//Parse all lists
			pgTxt = parseListItem(pgTxt);

			//Section
			int indexSectnMarkup = -1;
			String parsedSectnTitle="Default";
			String sectnText="";
			//Check for default section
			if(pgTxt.indexOf("==")==-1)
			{
				sectnText = pgTxt.substring(0, pgTxt.length());
				sectnText = sectnText.replaceAll("\n", " ");
				//
				//templateData = new HashMap<String,String>();
				//templates = new HashSet<Template>();
				Set<Map<String,String>> templateSet = new HashSet<Map<String,String>>();
				sectnText = parseTemplates(sectnText,templateSet);
				//
				wikiDoc.addSection(parsedSectnTitle, sectnText,templateSet);
				
			}
			else
			{
				if(pgTxt.indexOf("==")>0)
				{
					sectnText = pgTxt.substring(0, pgTxt.indexOf("=="));
					sectnText=sectnText.replaceAll("\n", " ");
					//
					//templateData = new HashMap<String,String>();
					Set<Map<String,String>>  templateSet = new HashSet<Map<String,String>>();
					sectnText = parseTemplates(sectnText,templateSet);
					//
					wikiDoc.addSection(parsedSectnTitle, sectnText,templateSet);
					//System.out.println("Section Title:"+parsedSectnTitle+" \nText:"+sectnText);
				}

				//First find index of "=="
				while((indexSectnMarkup  = pgTxt.indexOf("=="))!=-1)
				{
					int countMarkups = 0;
					int i = indexSectnMarkup;

					//Parse the "==" markup completely
					for(i=indexSectnMarkup;;i++){
						if(pgTxt.charAt(i)!='=')break;
						countMarkups++;
					}
					//Find the end of "==" markup
					int endIndex = pgTxt.indexOf("==", i);

					//Extract the section title 
					parsedSectnTitle = parseSectionTitle(pgTxt.substring(i, endIndex));
					parsedSectnTitle = parsedSectnTitle.trim();

					//Parse the end markup completely
					while(pgTxt.charAt(endIndex)=='=' && countMarkups>0){endIndex++;countMarkups--;}

					//System.out.println(endIndex+" "+pgTxt.indexOf("=="));
					//Extract the section text
					if(pgTxt.indexOf("==",endIndex)!=-1)
						sectnText = pgTxt.substring(endIndex,pgTxt.indexOf("==",endIndex));
					else//This indicates you reached end of page
						sectnText = pgTxt.substring(endIndex,pgTxt.length());

					//Replace the section title in-order to avoid looping back for finding "==" i.e. "==Section==" with "Section" 
					pgTxt = pgTxt.replace(pgTxt.substring(indexSectnMarkup, endIndex), parsedSectnTitle);
					sectnText=sectnText.replaceAll("\n", " ");
					//
					//templateData = new HashMap<String,String>();
					//templates = new HashSet<Template>();
					Set<Map<String,String>> templateSet = new HashSet<Map<String,String>>();
					sectnText = parseTemplates(sectnText,templateSet);
					//
					wikiDoc.addSection(parsedSectnTitle, sectnText,templateSet);
					//System.out.println("Section Title:"+parsedSectnTitle+" \nText:"+sectnText);
				}
			}
			//System.out.println("Sections:"+wikiDoc.getSections().size());
		}
		//System.out.println("Parsing Over");
	}

	public static String parseLinksText(String text)
	{
		int indexOfLink = -1;
		String link="";
		//System.out.println(text);
		while((indexOfLink = text.indexOf("["))!=-1)
		{
			int endIndex = text.indexOf("]",indexOfLink);
			//System.out.println(indexOfLink+" "+endIndex);
			if(text.charAt(endIndex+1)==']')
				link = text.substring(indexOfLink, endIndex+2);
			else
				link = text.substring(indexOfLink, endIndex+1);
			//System.out.println(link);
			String[] parsedLink = parseLinks(link);
			//
			parsedLink[0] = parsedLink[0].replace("[", "");
			parsedLink[0] = parsedLink[0].replace("]", "");
			//
			text = text.replace(link," "+parsedLink[0]+" ");

			if(parsedLink[1]!=null && !parsedLink[1].matches(""))
				linkSet.add(parsedLink[1]);
			//System.out.println(text);
		}
		return text;
	}
}
