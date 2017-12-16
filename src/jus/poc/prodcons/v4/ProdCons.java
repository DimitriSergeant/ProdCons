package jus.poc.prodcons.v4;

import java.util.HashMap;
import java.util.LinkedList;
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

    // Map associant les id des consommateurs à leur sémaphore les bloquant
    // jusqu'à ce que tous les exemplaires du message retiré soient consommés
    Map<Integer, Semaphore> BloquageCons;

    // Map associant les messages aux consommateurs en attente
    Map<Message, LinkedList<_Consommateur>> MessageCons;

    // Map associant les id des producteurs à leur sémaphore les bloquant
    // jusqu'à la consommation des exemplaires
    // de leurs messages
    Map<Integer, Semaphore> BloquageProd;

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
	BloquageCons = new HashMap<Integer, Semaphore>();
	BloquageProd = new HashMap<Integer, Semaphore>();
	MessageCons = new HashMap<Message, LinkedList<_Consommateur>>();
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
	    // On ajoute ce consommateur à la liste d'attente du message
	    ajoutCons(m, c);
	    // On crée le sémaphore de ce consommateur et on l'ajoute à la map
	    Semaphore s = new Semaphore(0);
	    BloquageCons.put(c.identification(), s);
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
	    // Si tous les exemplaires du message ont été retirés, on libère son
	    // producteur
	    BloquageProd.get(((MessageX) m).getProdMess()).V();
	    // Et on libère les consommateurs en attente sur ce message
	    for (_Consommateur cons : MessageCons.get(m)) {
		if (BloquageCons.get(cons.identification()) == null)
		    System.out.println("sem nule");
		BloquageCons.get(cons.identification()).V();
	    }
	    // On supprime l'entrée dans la map
	    MessageCons.remove(m);
	    // Et on libère un producteur
	    SemP.V();
	} else {
	    // Sinon on libère un consommateur
	    SemC.V();
	    // et on bloque ce consommateur
	    BloquageCons.get(c.identification()).P();
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
	BloquageProd.put(p.identification(), s);
	// On bloque ce producteur jusqu'à ce que tous les
	// exemplaires du messages soient consommés
	s.P();
    }

    public int taille() {
	return this.N;
    }

    private void ajoutCons(Message m, _Consommateur c) {
	if (MessageCons.containsKey(m)) {
	    // Si le massage est dejà dans la map et on ajoute le consommateur
	    MessageCons.get(m).add(c);
	} else {
	    // Sinon on cré l'entrée et on ajoute le consommateur
	    LinkedList<_Consommateur> l = new LinkedList<_Consommateur>();
	    l.add(c);
	    MessageCons.put(m, l);
	}
    }
}
