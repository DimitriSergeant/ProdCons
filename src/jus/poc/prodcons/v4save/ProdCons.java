package jus.poc.prodcons.v4save;

import java.util.HashMap;
import java.util.Map;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v4save.TestProdCons;

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

    // Map associant les id des producteurs à leur sémaphore les bloquant
    // pendant la consommation des exemplaires
    // de leurs messages
    Map<Integer, Semaphore> SemExemplaires;

    public ProdCons(int n, Observateur o) {
	this.obs = o;
	this.N = n;
	this.tampon = new Message[N];
	// Les producteurs peuvent déposer initialement n messages
	SemP = new Semaphore(n);
	// Les consommateurs peuvent retirer initialement 0 message car le
	// buffer est vide
	SemC = new Semaphore(0);
	nbRetires = new HashMap<Message, Integer>();
	SemExemplaires = new HashMap<Integer, Semaphore>();
    }

    public int enAttente() {
	return nplein;
    }

    public Message get(_Consommateur c) throws Exception, InterruptedException {
	// On attend que le buffer ne soit plus vide
	SemC.P();
	Message m;
	int n;
	boolean exemplairesEpuises;
	// Section critique
	synchronized (this) {
	    // Retrait d'un message
	    m = (MessageX) tampon[out];
	    // Notification à l'observateur
	    obs.retraitMessage(c, m);
	    if (TestProdCons.TRACE)
		System.out.println("Retrait " + m);
	    // Si le nombre de retraits vaut le nombre de'exemplaire du message,
	    // on retire le dit message de la map et du buffers
	    n = nbRetires.get(m) + 1;
	    if (n >= ((MessageX) m).getExemplaire()) {
		nbRetires.remove(m);
		out = (out + 1) % N;
		nplein--;
		exemplairesEpuises = true;
	    } else {
		// Sinon on met à jour le nombre de retraits du message
		nbRetires.put(m, nbRetires.get(m) + 1);
		exemplairesEpuises = false;
	    }
	}
	if (exemplairesEpuises) {
	    // Si tous les exemplaires du message ont été retirés, on libère son producteur
	    SemExemplaires.get(((MessageX) m).getProdMess()).V();
	    // Et on libère un producteur
	    SemP.V();
	} else {
	    // Sinon on libère un consommateur
	    SemC.V();
	}

	return m;
    }

    public void put(_Producteur p, Message m) throws Exception, InterruptedException {
	// On attend que le buffer ne soit pas plein
	SemP.P();
	// Section critique
	synchronized (this) {
	    // Retrait d'un message
	    tampon[in] = m;
	    // Notification à l'observateur
	    obs.depotMessage(p, m);
	    if (TestProdCons.TRACE)
		System.out.println("Dépot " + m);
	    in = (in + 1) % N;
	    nplein++;
	    // On ajoute le message à la map stockant le nombre de retrait qui
	    // vaut initialement 0
	    nbRetires.put(m, 0);
	}
	// On libère un consommateur
	SemC.V();
	// On ajoute le sémaphore bloquant ce producteur à la map
	Semaphore s = new Semaphore(0);
	SemExemplaires.put(p.identification(), s);
	// On bloque ce producteur jusqu'à ce que tous les
	// exemplaires du messages soient consommés
	s.P();
    }

    public int taille() {
	return this.N;
    }
}
