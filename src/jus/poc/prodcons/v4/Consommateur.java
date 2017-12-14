package jus.poc.prodcons.v4;

import java.util.Date;

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
	VAtemps = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
	this.observateur.newConsommateur(this);
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
	int tempsDeTraitement;

	while (true) {
	    tempsDeTraitement = VAtemps.next();
	    try {
		Thread.sleep(tempsDeTraitement);
	    } catch (InterruptedException e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }
	    try {
		m = tampon.get(this);
		this.observateur.consommationMessage(this, m, tempsDeTraitement);
		this.nbMessageTraites++;
		Date d = new Date();
		System.out.println(m.toString() + " et consommé par " + this.identification() + " à la date " + d.getTime());
	    } catch (Exception e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }
	}
    }

}
