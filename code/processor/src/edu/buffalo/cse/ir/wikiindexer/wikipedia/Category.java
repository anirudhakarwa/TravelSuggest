package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.Doc;

public class Category {
	private String catName;
	//private String catContent;
	//private double latitude;
	//private double longtitude;
	//private String title;
	private List<Category> categories;
	private Set<Document> docSet;
	
	
	/*public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setCatContent(String catContent) {
		this.catContent = catContent;
	}*/

	public String getCatName() {
		return catName;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public Set<Document> getDocSet() {
		return docSet;
	}
	
	public Category(String catName,Set<Document> docSet)
	{
		this.catName = catName;
		this.docSet = docSet;
	}
	
	public Category(String catName)
	{
		this.catName = catName;
		//docSet = new HashSet<Document>();
	}

	public void setDocSet(Set<Document> docSet) {
		this.docSet = docSet;
	}
	public void addCategory(Category c){
		if(categories==null)
			categories = new ArrayList<Category>();
		categories.add(c);
	}
	
	public void addDocument(String title, String name, String content, Map<String,String> properties){
		if(docSet==null)
			docSet = new HashSet<Document>();
		docSet.add(new Document(title, name, content,properties));
	}
	
	/*public String getCatContent() {
		return catContent;
	}*/

	public class Document{
		private String title;
		private String name;
		private String content;
		private Map<String,String> properties;
		public Document(String title, String name, String content,Map<String,String> properties)
		{
			this.title = title;
			this.name = name;
			this.content = content;
			this.properties = properties;
		}
		public String getTitle()
		{
			return title;
		}
		public String getName() {
			return name;
		}
		
		public String getContent() {
			return content;
		}
		public Map<String, String> getProperties() {
			return properties;
		}
		public void setProperties(Map<String, String> properties) {
			this.properties = properties;
		}
	}
}
