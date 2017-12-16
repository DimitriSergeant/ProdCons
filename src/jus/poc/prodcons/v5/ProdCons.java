package jus.poc.prodcons.v5;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v5.TestProdCons;

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

	// Lock permettant de bloquer la lecture ou l'écriture
	private final Lock verrou = new ReentrantLock();

	//Condition qui bloque l'ecriture du producteur 
	Condition pasPlein = verrou.newCondition();
	//Condition qui bloque la lecture du consommateur 
	Condition pasVide = verrou.newCondition();

	// Observateur
	Observateur obs;

	public ProdCons(int n, Observateur o) {
		this.obs = o;
		this.N = n;
		this.tampon = new Message[N];

	}

	public int enAttente() {
		return nplein;
	}

	public Message get(_Consommateur c) throws Exception, InterruptedException {
		//on ferme le verrou
		this.verrou.lock();
		Message m;
		try {
			/* On attend tant qu'il n'y a pas de message */
			while (nplein <= 0) {
				this.pasVide.await();
			}
			m = tampon[out];
			if (TestProdCons.TRACE)
				System.out.println("Retrait " + m);
			out = (out + 1) % N;
			nplein--;
			obs.retraitMessage(c, m);
			// on réveille un producteur
			this.pasPlein.signal();
			return m;
		} finally {
			//on réouvre le verrou
			this.verrou.unlock();
		}
	}

	public void put(_Producteur p, Message m) throws Exception, InterruptedException {
		//on ferme le verrou
		this.verrou.lock();
		try {
			/* On attend tant qu'il y a trop de messages */
			while (nplein >= N){
				this.pasPlein.await();	
			}
			tampon[in] = m;
			if (TestProdCons.TRACE)
				System.out.println("Dépot " + m);
			in = (in + 1) % N;
			nplein++;
			obs.depotMessage(p, m);
			// on réveille un consommateur
			this.pasVide.signal();
		} finally {
			//on réouvre le verrou
			this.verrou.unlock();
		}
	}

	public int taille() {
		return this.N;
	}
}
