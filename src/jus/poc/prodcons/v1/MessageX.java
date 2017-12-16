package jus.poc.prodcons.v1;

import jus.poc.prodcons.Message;

public class MessageX implements Message {
    // Producteur de ce message
    private int prod;
    // Numéro du message par rapport au producteur associé
    private int num;

    public MessageX(int idProd, int num) {
	this.prod = idProd;
	this.num = num;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
	return ("[MESSAGE " + this.getNum() + " du producteur "+prod + "]");
    }


    public int getProdMess() {
	return this.prod;
    }

    public int getNum() {
	return this.num;
    }

}