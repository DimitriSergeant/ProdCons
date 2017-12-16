package jus.poc.prodcons.v4save;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons._Producteur;

public class Producteur extends Acteur implements _Producteur {
    private int nbMessageATraiter;
    private Aleatoire VAtemps;
    private Aleatoire VAproduction;
    private Aleatoire VAexemplaire;
    private ProdCons tampon;

    protected Producteur(Observateur observateur, ProdCons tampon, int moyenneTempsDeTraitement,
	    int deviationTempsDeTraitement, int nombreMoyenDeProduction, int deviationNombreMoyenDeProduction,
	    int nombreMoyenNbExemplaire, int deviationNombreMoyenNbExemplaire) throws ControlException {
	super(typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);

	// Création de la variable aléatoire générant le nombre de messages à produire
	VAproduction = new Aleatoire(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
	// Création de la variable aléatoire générant le temps de traitement d'un
	// message
	VAtemps = new Aleatoire(moyenneTempsDeTraitement, deviationTempsDeTraitement);
	// Création de la variable aléatoire générant le nombre d'exemplaire à produire
	VAexemplaire = new Aleatoire(nombreMoyenNbExemplaire, deviationNombreMoyenNbExemplaire);

	this.nbMessageATraiter = VAproduction.next();
	this.tampon = tampon;
	this.observateur.newProducteur(this);
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
	int num = 1;

	while (nbMessageATraiter > 0) {

	    m = new MessageX(this.identification(), num++, VAexemplaire.next());
	    int tempsDeTraitement = VAtemps.next();

	    try {
		// On endort le thread pour simuler un temps de traitement
		Thread.sleep(tempsDeTraitement);
	    } catch (InterruptedException e) {
		System.out.println(e.toString());
		e.printStackTrace();
	    }

	    try {
		// Dépot du message
		this.tampon.put(this, m);
		this.observateur.productionMessage(this, m, tempsDeTraitement);
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
