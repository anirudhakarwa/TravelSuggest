package edu.buffalo.cse.ir.wikiindexer.tokenizer;

public class LinkedList{
	private Node root;
	private Node insert;

	private Node temp;
	private Node tempfortoken;
	private Node prevfortoken;

	private boolean isstarting=true;
	private boolean isstarting_fortoken=true;

	public void resetall(){
		temp=root;
		isstarting=true;
	}

	public void reset(){
		tempfortoken=root;
		temp=root;
		isstarting=true;
		isstarting_fortoken=true;
	}
	public void add(Object element){

		Node newelement=new Node();
		newelement.setData(element);

		if(root==null) {
			root=newelement;

			//adding null element at the end 
			Node nullele=new Node();
			nullele.setData("##NO_ELEMENT##");
			newelement.setNext(nullele);
			nullele.setPrev(newelement);
			nullele.setNext(null);
			insert=newelement;
			temp=root;
			tempfortoken=root;
		}
		else {
			Node tp=insert.getNext();
			insert.setNext(newelement);
			newelement.setNext(tp);
			newelement.setPrev(insert);
			tp.setPrev(newelement);
			insert=newelement;
		}
	}

	public Node getRoot(){
		return root;
	}

	public Object getelement() {
		if(isstarting){
			if(root!=null){
				isstarting=false;
				return temp.getData();
			}
			else{
				return null;
			}
		}
		else{
			if(hasNext()){
				Object ans=next();
				if(ans.equals("##NO_ELEMENT##")){
					return null;
				}
				else{
					return ans;
				}
			}
			else{
				return null;
			}
		}
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		if(temp.getNext()!=null){
			return true;
		}
		else{
			return false;
		}
	}

	public Object next() {
		// TODO Auto-generated method stub
		temp=temp.getNext();
		return temp.getData();
	}

	public boolean hasNextfortoken() {
		// TODO Auto-generated method stub
		if(isstarting_fortoken){
			if(root!=null){
				isstarting_fortoken=false;
				return true;
			}
			else{
				return false;
			}
		}
		else{
			if(tempfortoken==null){
				return false;
			}
			else if(tempfortoken.getNext()!=null){
				return true;
			}
			else{
				return false;
			}
		}
	}

	public Object nextfortoken() {
		// TODO Auto-generated method stub
		if(tempfortoken!=null){
			Node t=tempfortoken.getNext();
			Object ans=tempfortoken.getData();
			tempfortoken=t;
			if(ans.toString().equals("##NO_ELEMENT##")){
				return null;
			}
			else{
				return ans;
			}

		}
		else{
			return null;
		}
	}

	public Object prev(){
		if(tempfortoken!=null){
			if(tempfortoken.getPrev()!=null){
				tempfortoken=tempfortoken.getPrev();
				if(tempfortoken!=null){
					return tempfortoken.getData();
				}
				else{
					return null;
				}
			}
			else{
				return null;
			}
		}
		else{
			return null;
		}
	}

	public boolean hasPrev(){
		if(tempfortoken!=null){
			if(tempfortoken.getPrev()!=null){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public void remove() {
		// TODO Auto-generated method stub
		if(tempfortoken!=null){
			Node temp=tempfortoken.getPrev();
			if(temp!=null){
				Node store=tempfortoken.getNext();
				if(store==null){
					//temp.setNext(null);
				}
				else{
					temp.setNext(store);
					store.setPrev(temp);
					tempfortoken=store;
				}
			}
			else{
				root=root.getNext();
				root.setPrev(null);
				tempfortoken=root;
				this.temp=root;
			}
		}
	}

	public boolean mergewithprev(){
		Node temp;
		if(hasPrev()){
			temp=tempfortoken.getPrev();
			if(temp!=null){
				if(tempfortoken.getData().equals("##NO_ELEMENT##")){
					return false;
				}
				else{
					temp.setData(temp.getData().toString()+" "+tempfortoken.getData());
					temp.setNext(tempfortoken.getNext());
					tempfortoken.getNext().setPrev(temp);
					tempfortoken=temp;
					return true;
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public boolean mergewithnext(){
		Node temp;
		if(tempfortoken!=null){
			if(tempfortoken.getNext()!=null){
				if(tempfortoken.getNext().getData().equals("##NO_ELEMENT##")){
					return false;
				}
				else{
					temp=tempfortoken.getNext();
					tempfortoken.setData(tempfortoken.getData()+" "+temp.getData().toString());
					tempfortoken.setNext(temp.getNext());
					temp.getNext().setPrev(tempfortoken);
					return true;
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public void seekend(){
		if(tempfortoken!=null){
			while(tempfortoken.getNext()!=null){
				tempfortoken=tempfortoken.getNext();
			}
		}
		isstarting_fortoken=false;
		//System.out.println("lastttt "+tempfortoken.getData());
	}
	
	public void merge(TokenStream other) {
		//TODO: Implement this method
		while(other.hasNext()){
			String add=other.next();
			if(add!=null){
				if(!add.equals("null")&& !add.matches("")){
					add(add);
				}
			}
		}
	}

	public void set(String... newvalue){
		Node newnode=null;
		Node newelement1=null;
		for(int i=0;i<newvalue.length; i++){

			if(!(newvalue[i]+"").matches("")&&!(newvalue[i]+"").equals(null+"")){
				newelement1=new Node();
				newelement1.setData(newvalue[i]);
				newelement1.setNext(null);
				newelement1.setPrev(null);
				if(newnode==null) {
					newnode=newelement1;
				}
				else {
					Node n=newnode;
					while(n.getNext()!=null) {
						n=n.getNext();
					}
					n.setNext(newelement1);
					newelement1.setPrev(n);
				}
			}
		}
		if(newnode!=null){
			if(tempfortoken!=null){
				if(tempfortoken.getPrev()!=null){
					Node pp=tempfortoken.getPrev();
					pp.setNext(newnode);
					newnode.setPrev(pp);
					if(tempfortoken.getNext()!=null){
						newelement1.setNext(tempfortoken.getNext());
						tempfortoken.getNext().setPrev(newelement1);
						tempfortoken=newelement1;
					}
					else{
						newelement1.setNext(tempfortoken);
						tempfortoken.setPrev(newelement1);
						tempfortoken.setNext(null);
						tempfortoken=newelement1;
						
					}
				}
				else{
					root=newnode;
					newelement1.setNext(tempfortoken.getNext());
					tempfortoken.getNext().setPrev(newelement1);
					tempfortoken=newelement1;
				}
			}
		}
		/*
		else{
			root=newnode;
			tempfortoken=newelement1;
		}
		 */
	}
}
