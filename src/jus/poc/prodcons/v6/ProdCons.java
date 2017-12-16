package jus.poc.prodcons.v6;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v6.TestProdCons;

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

    // Observateur
    Observateur obs;

    // MyObservateur
    MyObservateur mo;

    public ProdCons(int n, Observateur o, MyObservateur mo) {
	this.obs = o;
	this.mo = mo;
	this.N = n;
	this.tampon = new Message[N];
	SemP = new Semaphore(n);
	SemC = new Semaphore(0);
    }

    public int enAttente() {
	return nplein;
    }

    public Message get(_Consommateur c) throws Exception, InterruptedException {
	SemC.P();
	Message m;
	// Section critique pour manipuler les variables partagées
	synchronized (this) {
	    m = tampon[out];
	    if (TestProdCons.TRACE)
		System.out.println("Retrait " + m);
	    out = (out + 1) % N;
	    nplein--;
	    mo.retraitMessage(c, m);
	    obs.retraitMessage(c, m);
	}
	SemP.V();
	return m;
    }

    public void put(_Producteur p, Message m) throws Exception, InterruptedException {
	SemP.P();
	// Section critique pour manipuler les variables partagées
	synchronized (this) {
	    mo.depotMessage(p, m);
	    obs.depotMessage(p, m);
	    tampon[in] = m;
	    if (TestProdCons.TRACE)
		System.out.println("Dépot " + m);
	    in = (in + 1) % N;
	    nplein++;
	}
	SemC.V();
    }

    public int taille() {
	return this.N;
    }
}
