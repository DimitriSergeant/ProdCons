package jus.poc.prodcons.v1;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Consommateur;

public class Consommateur extends Acteur implements _Consommateur {
    private int nbMessageTraites;
    private ProdCons tampon;
    private Aleatoire VAtemps;

    protected Consommateur(Observateur observateur, ProdCons tampon, int moyenneTempsDeTraitement,
	    int deviationTempsDeTraitement) throws ControlException {
	super(typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);

	this.nbMessageTraites = 0;
	this.tampon = tampon;
	// Création de la variable aléatoire générant le temps de traitement d'un
	// message
	VAtemps = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see jus.poc.prodcons.Acteur#nombreDeMessages()
     */
    public int nombreDeMessages() {
	return this.nbMessageTraites;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {

	Message m;

	while (true) {
	    try {
		// On endort le thread pour simuler un temps de traitement
		Thread.sleep(VAtemps.next());
	    } catch (InterruptedException e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }
	    try {
		// Retrait d'un message
		m = tampon.get(this);
		this.nbMessageTraites++;
	    } catch (Exception e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }
	}
    }

}
