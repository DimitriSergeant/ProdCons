package jus.poc.prodcons.v1;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class Producteur extends Acteur implements _Producteur {
    
    private int nbMessageATraiter;

    protected Producteur(int type, Observateur observateur, int moyenneTempsDeTraitement,
	    int deviationTempsDeTraitement) throws ControlException {
	super(type, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
	this.nbMessageATraiter = Aleatoire.;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see jus.poc.prodcons.Acteur#nombreDeMessages()
     */
    public int nombreDeMessages() {
	return this.nbMessageATraiter;
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
	this.nbMessageATraiter--;
    }

}
