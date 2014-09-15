package edu.buffalo.cse.ir.wikiindexer.tokenizer;

public class Node {
	private Node next;
	private Object data;
	private Node prev;
	
	public void setNext(Node n){
		next=n;
	}
	public Node getNext(){
		return next;
	}
	public void setPrev(Node n){
		prev=n;
	}
	public Node getPrev(){
		return prev;
	}
	public void setData(Object d){
		data=d;
	}
	public Object getData() {
		return data;
	}
}

