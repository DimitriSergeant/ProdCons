package jus.poc.prodcons.v5;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class TestProdCons extends Simulateur {
    final private boolean DEBUG = false;
    final static boolean TRACE = true;
    final private static String configurationFile = "options.xml";

    private int nbProd = 0;
    private int nbCons = 0;
    private int nbBuffer = 0;
    private int tempsMoyenProduction = 0;
    private int deviationTempsMoyenProduction = 0;
    private int tempsMoyenConsommation = 0;
    private int deviationTempsMoyenConsommation = 0;
    private int nombreMoyenDeProduction = 0;
    private int deviationNombreMoyenDeProduction = 0;
    private int nombreMoyenNbExemplaire = 0;
    private int deviationNombreMoyenNbExemplaire = 0;

    private ProdCons buffer;

    public TestProdCons(Observateur observateur) {
	super(observateur);
    }

    @Override
    /*
     * (non-Javadoc)
     * 
     * @see jus.poc.prodcons.Simulateur#run()
     */
    protected void run() throws Exception {
	// Récupération des paramètres de la simulation dans le fichier xml
	Properties properties = new Properties();
	properties.loadFromXML(ClassLoader.getSystemResourceAsStream("jus/poc/prodcons/options/" + configurationFile));
	String key;
	int value;
	List<Consommateur> consommateurs = new LinkedList<Consommateur>();
	List<Producteur> producteurs = new LinkedList<Producteur>();

	Class<?> thisOne = getClass();
	for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	    key = (String) entry.getKey();
	    value = Integer.parseInt((String) entry.getValue());
	    thisOne.getDeclaredField(key).set(this, value);
	}
	if (DEBUG) {
	    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
		key = (String) entry.getKey();
		System.out.println(key + "=" + thisOne.getDeclaredField(key).get(this));
	    }
	}

	// Création du buffer
	buffer = new ProdCons(nbBuffer, observateur);

	this.observateur.init(nbProd, nbCons, nbBuffer);

	// Création des producteurs
	for (int i = 0; i < nbProd; i++) {
	    Producteur p = new Producteur(observateur, buffer, tempsMoyenProduction, deviationTempsMoyenProduction,
		    nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
	    producteurs.add(p);
	    p.start();
	    if (DEBUG) {
		System.out.println("Création du producteur " + p.getId());
	    }
	}

	// Création des consommateurs
	for (int i = 0; i < nbCons; i++) {
	    Consommateur c = new Consommateur(observateur, buffer, tempsMoyenConsommation,
		    deviationTempsMoyenConsommation);
	    consommateurs.add(c);
	    // La JVM s'arrête quand il ne reste que des thread consommateurs
	    c.setDaemon(true);
	    c.start();
	    if (DEBUG) {
		System.out.println("Création du consommateur " + c.getId());
	    }
	}

	// On attend la fin des productions de messages
	for (Producteur p : producteurs) {
	    p.join();
	}
	if (DEBUG) {
	    System.out.println("Messages produits");
	}

	// Tant qu'il reste des messages dans le buffer on laisse travailler les
	// thread deamon des consommateurs en ne terminant pas ce thread
	do {
	    Thread.yield();
	} while (buffer.enAttente() > 0);
	if (DEBUG) {
	    System.out.println("Messages consommés");
	}
    }

    public static void main(String[] args) {
	new TestProdCons(new Observateur()).start();
    }

}
