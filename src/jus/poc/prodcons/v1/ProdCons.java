package jus.poc.prodcons.v1;

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

    public ProdCons(int n) {
	this.N = n;
	this.tampon = new Message[taille()];
    }

    public synchronized int enAttente() {
	return nplein;
    }

    public synchronized Message get(_Consommateur c) throws Exception, InterruptedException {
	// On attend que la buffer ne soit plus vide
	while (nplein <= 0)
	    wait();
	// Retrait d'un message
	Message m = tampon[out];
	if (TestProdCons.TRACE)
	    System.out.println("Retrait " + m);
	out = (out + 1) % taille();
	nplein--;
	// On réveille les producteurs
	notifyAll();
	return m;
    }

    public synchronized void put(_Producteur p, Message m) throws Exception, InterruptedException {
	// On attend que le buffer ne soit plus plein
	while (nplein >= taille())
	    wait();
	// Dépot d'un message
	tampon[in] = m;
	if (TestProdCons.TRACE)
	    System.out.println("Dépot " + m);
	in = (in + 1) % taille();
	nplein++;
	// On réveille les consommateurs
	notifyAll();
    }

    public synchronized int taille() {
	return this.N;
    }
}
