package edu.buffalo.cse.ir.wikiindexer.indexer;

public class Test {
	public static void main(String[] args) {
		
		IndexReader ir=new IndexReader(null, INDEXFIELD.TERM);
		System.out.println(ir.getTopK(20));
		System.out.println(ir.getTotalKeyTerms());
		System.out.println(ir.getTotalValueTerms());
		System.out.println(ir.getPostings("name"));
		System.out.println(ir.query("name","also"));
		
	}
	
}
