package jus.poc.prodcons.v2;

import jus.poc.prodcons.Message;

public class MessageX implements Message {
	private int prod;
	private java.util.Date date;
	private int num;
	
	public MessageX(int idProd, int num, java.util.Date d){
		this.prod = idProd;
		this.date = d;
		this.num = num;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return ("MESSAGE"+this.getNum()+" : dépot à la date " + date.getTime() + " par " + prod);
	}
	
	public java.util.Date getDate(){
		return this.date;
	}
	public void setDate(java.util.Date d){
		this.date = d;
	}
	
	public int getProdMess(){
		return this.prod;
	}
	
	public int getNum(){
		return this.num;
	}
	
	
	
}