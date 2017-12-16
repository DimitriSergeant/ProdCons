package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;

public class MessageX implements Message {

    // Producteur de ce message
    private int prod;

    // Nombre d'exemplaire produit de ce message
    private int nbExemplaire;

    // Numéro du message par rapport au producteur associé
    private int num;

    public MessageX(int idProd, int num, int n) {
	this.prod = idProd;
	this.nbExemplaire = n;
	this.num = num;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return ("[MESSAGE " + this.getNum() + " du producteur " + prod + "]");
    }

    public int getProdMess() {
	return this.prod;
    }

    public int getExemplaire() {
	return this.nbExemplaire;
    }

    public int getNum() {
	return this.num;
    }

}