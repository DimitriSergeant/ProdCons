package jus.poc.prodcons.v1;

import java.sql.Date;

import jus.poc.prodcons.Message;

public class MessageX implements Message {
	private int prod;
	private String message;
	private Date date;
	
	public MessageX(int idProd, String message){
		this.prod = idProd;
		this.message = message;
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return ("Message : '" + message + "' créé par " + prod);
	}
	
	public Date getDate(){
		return this.date;
	}
	public void setDate(Date d){
		this.date = d;
	}
	
	public int getProdMess(){
		return this.prod;
	}
	
	
	
}