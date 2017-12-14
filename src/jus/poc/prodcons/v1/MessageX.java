package jus.poc.prodcons.v1;

import jus.poc.prodcons.Message;

public class MessageX implements Message {
	private int prod;
	private java.util.Date date;
	
	public MessageX(int idProd, java.util.Date d){
		this.prod = idProd;
		this.date = d;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return ("MESSAGE : dépot à la date " + date.getTime() + " par " + prod);
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
	
	
	
}