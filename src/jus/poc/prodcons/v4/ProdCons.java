package jus.poc.prodcons.v4;

import java.util.HashMap;
import java.util.Map;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v4.TestProdCons;

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

    // Stockage du nombre d'exemplaires retirés associé à un message
    Map<Message, Integer> nbRetires;

    // Sémaphores bloquant les Producteurs pendant la consommation des exemplaires
    // de leurs messages
    Map<Integer, Semaphore> SemExemplaires;

    public ProdCons(int n, Observateur o) {
	this.obs = o;
	this.N = n;
	this.tampon = new Message[N];
	SemP = new Semaphore(n);
	SemC = new Semaphore(0);
	nbRetires = new HashMap<Message, Integer>();
	SemExemplaires = new HashMap<Integer, Semaphore>();
    }

    public int enAttente() {
	return nplein;
    }

    public Message get(_Consommateur c) throws Exception, InterruptedException {
	SemC.P();
	Message m;
	int n;
	boolean exemplairesEpuises;
	synchronized (this) {
	    m = (MessageX) tampon[out];
	    if (TestProdCons.TRACE)
		System.out.println("Retrait " + m);
	    n = nbRetires.get(m) + 1;
	    if (n >= ((MessageX) m).getExemplaire()) {
		nbRetires.remove(m);
		out = (out + 1) % N;
		nplein--;
		exemplairesEpuises = true;
	    } else {
		nbRetires.put(m, nbRetires.get(m) + 1);
		exemplairesEpuises = false;
	    }
	}
	if (exemplairesEpuises) {
	    SemExemplaires.get(((MessageX) m).getProdMess()).V();
	    SemP.V();
	} else {
	    SemC.V();
	}

	obs.retraitMessage(c, m);
	return m;
    }

    public void put(_Producteur p, Message m) throws Exception, InterruptedException {
	obs.depotMessage(p, m);
	SemP.P();
	synchronized (this) {
	    tampon[in] = m;
	    if (TestProdCons.TRACE)
		System.out.println("Dépot " + m);
	    in = (in + 1) % N;
	    nplein++;
	    nbRetires.put(m, 0);
	}
	SemC.V();

	Semaphore s = new Semaphore(0);
	SemExemplaires.put(p.identification(), s);
	s.P();
    }

    public int taille() {
	return this.N;
    }
}
