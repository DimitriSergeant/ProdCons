package jus.poc.prodcons.v1;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class TestProdCons extends Simulateur {
    final private boolean DEBUG = true;
    private static String configurationFile = "options.xml";

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

    private int signalNombreDeProduction = 0;

    private ProdCons buffer;

    protected ArrayList<Consommateur> consommateurs;
    protected ArrayList<Producteur> producteurs;

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
	Properties properties = new Properties();
	properties.loadFromXML(ClassLoader.getSystemResourceAsStream("jus/poc/prodcons/options/" + configurationFile));
	String key;
	int value;
	consommateurs = new ArrayList<Consommateur>();
	producteurs = new ArrayList<Producteur>();
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

	buffer = new ProdCons(nbBuffer);

	for (int i = 0; i < nbProd; i++) {
	    Producteur p = new Producteur(observateur, buffer, tempsMoyenProduction, deviationTempsMoyenProduction,
		    nombreMoyenNbExemplaire, deviationNombreMoyenNbExemplaire);
	    producteurs.add(p);
	    p.start();
	    if (DEBUG) {
		System.out.println("Création du producteur " + p.getId());
	    }
	}

	for (int i = 0; i < nbCons; i++) {
	    Consommateur c = new Consommateur(observateur, buffer, tempsMoyenConsommation,
		    deviationTempsMoyenConsommation);
	    consommateurs.add(c);
	    c.start();
	    if (DEBUG) {
		System.out.println("Création du consommateur " + c.getId());
	    }
	}

	for (Producteur producteur : producteurs) {
	    producteur.join();
	}
	if (DEBUG) {
	    System.out.println("Messages produits");
	}

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
