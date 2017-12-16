package jus.poc.prodcons.v1;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class Producteur extends Acteur implements _Producteur {
    private int nbMessageATraiter;
    private Aleatoire VAtemps;
    private Aleatoire VAproduction;
    private ProdCons tampon;

    protected Producteur(Observateur observateur, ProdCons tampon, int moyenneTempsDeTraitement,
	    int deviationTempsDeTraitement, int nombreMoyenDeProduction, int deviationNombreMoyenDeProduction)
	    throws ControlException {
	super(typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);

	// Création de la variable aléatoire générant le nombre de messages à produire
	VAproduction = new Aleatoire(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
	// Création de la variable aléatoire générant le temps de traitement d'un
	// message
	VAtemps = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);

	this.nbMessageATraiter = VAproduction.next();
	this.tampon = tampon;
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

	MessageX m;
	int cpt = 1;

	while (nbMessageATraiter > 0) {

	    // Création d'un message
	    m = new MessageX(this.identification(), cpt++);

	    try {
		// On endort le thread pour simuler un temps de traitement
		Thread.sleep(VAtemps.next());
	    } catch (InterruptedException e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }

	    try {
		// Dépot du message dans le buffer
		this.tampon.put(this, m);
		this.nbMessageATraiter--;
	    } catch (InterruptedException e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    } catch (Exception e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }
	}
    }
}
