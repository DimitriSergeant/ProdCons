package jus.poc.prodcons.v2;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {
    // Nombre de cases occupés
    private int nplein = 0;
    // Taille max du tampon
    private int N = 0;
    private Message[] tampon = null;
    // index pour retrait
    private int out = 0;
    // index pour dépot
    private int in = 0;

    // Sémaphore des consommateurs
    Semaphore SemC;

    // Sémaphore des producteurs
    Semaphore SemP;

    public ProdCons(int n) {
	this.N = n;
	this.tampon = new Message[N];
	SemP = new Semaphore(n);
	SemC = new Semaphore(n);
    }

    public int enAttente() {
	return nplein;
    }

    public Message get(_Consommateur c) throws Exception, InterruptedException {
	SemC.P();
	Message m;
	synchronized (this) {
	    m = tampon[out];
	    out = (out + 1) % N;
	    nplein--;
	}
	SemC.V();
	return m;
    }

    public void put(_Producteur p, Message m) throws Exception, InterruptedException {
	SemP.P();
	synchronized (this) {
	    tampon[in] = m;
	    in = (in + 1) % N;
	    nplein++;
	}
	SemP.V();
    }

    public int taille() {
	return this.N;
    }
}
