package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;

public class MessageX implements Message {
    private int prod;
    private java.util.Date date;
    private int nbExemplaire;

    public MessageX(int idProd, java.util.Date d, int n) {
	this.prod = idProd;
	this.date = d;
	this.nbExemplaire = n;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return ("MESSAGE : dépot à la date " + date.getTime() + " par " + prod);
    }

    public java.util.Date getDate() {
	return this.date;
    }

    public void setDate(java.util.Date d) {
	this.date = d;
    }

    public int getProdMess() {
	return this.prod;
    }

    public int getExemplaire() {
	return this.nbExemplaire;
    }

}