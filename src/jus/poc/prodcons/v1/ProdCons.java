package jus.poc.prodcons.v1;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {
    // Nombre de cases occupés
    private int nplein;
    // Taille max du tampon
    private int N;
    private Message[] tampon;
    // index pour retrait
    private int out;
    // index pour dépot
    private int in;

    public ProdCons(int n) {
	this.N = n;
	this.tampon = new Message[N];
    }

    public int enAttente() {
	return nplein;
    }

    public synchronized Message get(_Consommateur c) throws Exception, InterruptedException {
	while (nplein < 0)
	    wait();
	Message m = tampon[out];
	out = (out + 1) % N;
	nplein--;
	notifyAll();
	return m;
    }

    public synchronized void put(_Producteur p, Message m) throws Exception, InterruptedException {
	while (nplein >= N)
	    wait();
	tampon[in] = m;
	in = (in + 1) % N;
	nplein++;
	notifyAll();
    }

    public int taille() {
	return this.N;
    }
}
