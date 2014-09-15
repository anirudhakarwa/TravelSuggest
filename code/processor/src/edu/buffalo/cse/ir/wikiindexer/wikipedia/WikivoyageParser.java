package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.print.Doc;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.Category.Document;

public class WikivoyageParser {
	public static List<String> categories;
	public static Set<String> linkSet;
	public static Set<String> boldItemsSection; 

	public static void intializeBoldItemsSectn()
	{
		String[] temp = {"see","buy","drink","eat","sleep","theatre_cinema"};
		boldItemsSection = new HashSet<String>(Arrays.asList(temp));
	}

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
		//itemText = itemText.replaceAll("[:]+", "");
		return itemText.trim();
	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics.
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * @param text: The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public static String parseTextFormatting_old(String text) {
		if(text==null || text.matches(""))
			return text;
		text = text.replaceAll("[']+", "");
		text = parseListItem(text);
		return text;
	}

	public static String parseTextFormatting(String text) {
		if(text==null || text.matches(""))
			return text;
		int index = 0;
		int newindex=0;
		//System.out.println(text);
		while(text.indexOf("* '", index)!=-1)
		{
			newindex = text.indexOf("* '", index);
			//System.out.println(text.substring(index,newindex).trim());
			index = newindex;
		}
		text = parseListItem(text);
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

	public static String parseTemplates(String text,Category cat,String title)
	{
		if(text==null)
			return null;
		if(text=="")
			return "";
		else
		{
			text = parseDiffTemplates(text, "{{","}}",2,cat,title);
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

	public static String parseDiffTemplates(String text, String startMarkup, String endMarkup,int diff,Category cat,String title)
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
					parseTemplateData(templateData,cat,title);
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

	public static void parseTemplateData(String templateData,Category cat,String title)
	{
		if(templateData!=null && !templateData.matches("") && templateData.contains("name="))
		{
			Map<String,String> templateProp = new HashMap<String, String>();
			templateData = templateData.replace("{{", "");
			templateData = templateData.replace("}}", "");
			String[] templateCont = templateData.split("\\|");
			if(templateCont.length>0)
			{
				String name = null;
				for(int i=1;i<templateCont.length;i++)
				{
					String s = templateCont[i];
					if(s.split("=").length==2)
					{
						String key = s.split("=")[0].trim();
						String value = s.split("=")[1].trim();
						if(!key.matches("") && !value.matches("")){
							if(key.contains("name"))
								name = value;
							else if(!key.contains("name"))
								templateProp.put(key,value);
						}
					}
				}
				//System.out.println(cat.getCatName()+":new doc added:"+templateProp);
				cat.addDocument(title, name, null, templateProp);
			}
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


	static class CategoryIndex{
		Category c;
		int sectionLevel;
		public Category getC() {
			return c;
		}
		public void setC(Category c) {
			this.c = c;
		}
		public int getSectionLevel() {
			return sectionLevel;
		}
		public void setSectionLevel(int sectionLevel) {
			this.sectionLevel = sectionLevel;
		}
		public CategoryIndex(Category c, int sectionLevel) {
			super();
			this.c = c;
			this.sectionLevel = sectionLevel;
		}


	}
	public static Map<String,Category> parsePageText(Collection<String> pageText, Collection<WikipediaDocument> wikiDocs, Map<String,LocationDetail> citySections)
	{
		Iterator<String> pageItr = pageText.iterator();
		Iterator<WikipediaDocument> wikiDocsItr = wikiDocs.iterator();
		Map<String,Category> categoryMap = new HashMap<String, Category>();

		intializeBoldItemsSectn();
		LinkedList<CategoryIndex> catIndex = null;
		while(pageItr.hasNext() && wikiDocsItr.hasNext())
		{
			String pgTxt = pageItr.next();
			pgTxt = replaceEscapeChars(pgTxt);
			WikipediaDocument wikiDoc = wikiDocsItr.next();
			System.out.println(wikiDoc.getTitle());
			//citySections.put(wikiDoc.getTitle(),null);
			//System.out.println(wikiDoc.getAuthor());
			//Parse all html tags
			pgTxt = parseTagFormatting(pgTxt);
			//Parse Templates tag
			//pgTxt = parseTemplates(pgTxt);
			//System.out.println(pgTxt);
			//Parse Bold and italics
			//Parse all links
			pgTxt = parseTextFormatting_old(pgTxt);
			//Set<String> linkSet  = new HashSet<String>();
			//Initialize Category List
			initializeCatList();
			pgTxt = parseLinksText(pgTxt);
			wikiDoc.addLInks(linkSet);
			//System.out.println(linkSet);
			wikiDoc.addCategories(categories);

			//Parse all lists
			//pgTxt = parseListItem(pgTxt);

			//Section
			int indexSectnMarkup = -1;
			String parsedSectnTitle="Default";
			String sectnText="";
			//Check for default section
			Category cat=null;
			parsedSectnTitle = parsedSectnTitle.toLowerCase();
			if(pgTxt.indexOf("==")==-1)
			{
				sectnText = pgTxt.substring(0, pgTxt.length());
				sectnText = sectnText.replaceAll("\n", " ");
				if(categoryMap.containsKey(parsedSectnTitle))
					cat = categoryMap.get(parsedSectnTitle);
				else{
					cat = new Category(parsedSectnTitle);
					categoryMap.put(parsedSectnTitle, cat);
				}
				sectnText = addCategories(parsedSectnTitle, sectnText, wikiDoc.getTitle(), cat);
				addLatLongProp(wikiDoc.getTitle(),cat,parsedSectnTitle,wikiDoc.getLatitude(),wikiDoc.getLongitude(), sectnText);
			}
			else
			{
				if(pgTxt.indexOf("==")>0)
				{
					sectnText = pgTxt.substring(0, pgTxt.indexOf("=="));
					sectnText = sectnText.replaceAll("\n", " ");
					//System.out.println(sectnText);
					if(categoryMap.containsKey(parsedSectnTitle))
						cat = categoryMap.get(parsedSectnTitle);
					else{
						cat = new Category(parsedSectnTitle);
						categoryMap.put(parsedSectnTitle, cat);
					}
					sectnText = addCategories(parsedSectnTitle, sectnText, wikiDoc.getTitle(), cat);
					addLatLongProp(wikiDoc.getTitle(), cat, parsedSectnTitle, wikiDoc.getLatitude(), wikiDoc.getLongitude(), sectnText);
				}

				//First find index of "=="
				while((indexSectnMarkup  = pgTxt.indexOf("=="))!=-1)
				{
					int countMarkups = 0;
					int i = indexSectnMarkup;

					//Parse the "==" markup completely
					for(i=indexSectnMarkup;pgTxt.charAt(i)=='=';i++)
						countMarkups++;

					//Find the end of "==" markup
					int endIndex = pgTxt.indexOf("==", i);

					//Extract the section title
					//System.out.println(i+" "+endIndex);
					parsedSectnTitle = parseSectionTitle(pgTxt.substring(i, endIndex));
					parsedSectnTitle = parsedSectnTitle.trim().toLowerCase();
					//System.out.println(citySections.keySet());
					String sectnStr = citySections.get(wikiDoc.getTitle()).getAllSections()+","+parsedSectnTitle;
					citySections.get(wikiDoc.getTitle()).setAllSections(sectnStr);

					//
					if(countMarkups==2){

						if(categoryMap.containsKey(parsedSectnTitle))
							cat = categoryMap.get(parsedSectnTitle);
						else{
							cat = new Category(parsedSectnTitle);
							categoryMap.put(parsedSectnTitle, cat);
						}
						//System.out.println("Main cat:"+cat.getCatName());
						//Adding new Category Index
						catIndex = new LinkedList<CategoryIndex>();
						catIndex.add(new CategoryIndex(cat, 2));
					}
					else{

						Category newcat = new Category(parsedSectnTitle);
						//Find the category just above it
						/*System.out.println("--------");
						System.out.println(wikiDoc.getTitle());
						System.out.println(parsedSectnTitle);
						System.out.println(countMarkups);
						System.out.println(catIndex.peekLast().getSectionLevel());*/
						while(catIndex.peekLast().getSectionLevel() >= countMarkups)
							catIndex.pollLast();
						//System.out.println("Added to cat:"+catIndex.peekLast().getC().getCatName());
						catIndex.peekLast().getC().addCategory(newcat);
						catIndex.add(new CategoryIndex(newcat,countMarkups));
						cat = newcat;
					}
					//

					//Parse the end markup completely
					while(pgTxt.charAt(endIndex)=='=' && countMarkups>0){endIndex++;countMarkups--;}

					//Extract the section text
					if(pgTxt.indexOf("==",endIndex)!=-1)
						sectnText = pgTxt.substring(endIndex,pgTxt.indexOf("==",endIndex));
					else//This indicates you reached end of page
						sectnText = pgTxt.substring(endIndex,pgTxt.length());

					//Replace the section title in-order to avoid looping back for finding "==" i.e. "==Section==" with "Section" 
					//pgTxt = pgTxt.replace(pgTxt.substring(indexSectnMarkup, endIndex), parsedSectnTitle);
					pgTxt = pgTxt.substring(0,indexSectnMarkup)+"\n"+parsedSectnTitle+"\n"+pgTxt.substring(endIndex,pgTxt.length());

					sectnText=sectnText.replaceAll("\n", " ");

					sectnText = addCategories(parsedSectnTitle, sectnText, wikiDoc.getTitle(), cat);
					
				}
			}
		}
		return categoryMap;
	}


	private static String replaceEscapeChars(String str){
		if(str==null)return null;
		str = str.replace("&", "<![CDATA[&]]>");
		str = str.replace("&mdash;","<![CDATA[&mdash;]]>");
		str = str.replace("&nbsp;","<![CDATA[&nbsp;]]>");
		return str;
	}

	private static void addLatLongProp(String title, Category c,
			String parsedSectnTitle, double latitude, double longitude, String sectionText) {

		if(c!=null){
			Map<String,String> prop = new HashMap<String,String>();
			prop.put("store",latitude+","+longitude);
			c.addDocument(title, parsedSectnTitle, sectionText, prop);
			/*if(c.getDocSet()==null)
			{
				System.out.println("addLatLongProp:"+title);
				Map<String,String> prop = new HashMap<String,String>();
				prop.put("store",latitude+","+longitude);
				c.addDocument(title, parsedSectnTitle, sectionText, prop);
			}
			else{
				for(Document d:c.getDocSet()){
					if(d.getTitle().equalsIgnoreCase(title)){
						if(d.getProperties()==null)
							d.setProperties(new HashMap<String,String>());
						d.getProperties().put("store",latitude+","+longitude);
					}
				}
			}*/
		}
	}

	public static String addCategories(String parsedSectnTitle,String sectnText, String title, Category cat)
	{
		/*Category c = null;
		if(categoryMap.containsKey(parsedSectnTitle))
			c = categoryMap.get(parsedSectnTitle);
		else
		{
			c = new Category(parsedSectnTitle);
			categoryMap.put(parsedSectnTitle,c);
		}*/
		sectnText = parseTemplates(sectnText,cat,title);
		/*System.out.println(sectnText);
			if(boldItemsSection.contains(parsedSectnTitle.toLowerCase()) && sectnText.contains("* '"))
			sectnText = parseTextFormatting(sectnText);
		else
			sectnText = parseTextFormatting_old(sectnText);*/
		sectnText = sectnText.trim();
		//System.out.println(parsedSectnTitle);
		if(sectnText.length()>0 && !parsedSectnTitle.equalsIgnoreCase("default")){
			cat.addDocument(title, parsedSectnTitle, sectnText, null);
			//cat.setCatContent(sectnText);
			//cat.setTitle(title);
		}
		return sectnText;
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
