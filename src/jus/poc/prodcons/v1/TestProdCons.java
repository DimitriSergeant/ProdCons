package jus.poc.prodcons.v1;

import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class TestProdCons extends Simulateur {
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

    public TestProdCons(Observateur observateur) {
	super(observateur);
    }

    @Override
    /*
     * (non-Javadoc)
     * @see jus.poc.prodcons.Simulateur#run()
     */
    protected void run() throws Exception {
	Properties properties = new Properties();
	properties.loadFromXML(ClassLoader.getSystemResourceAsStream("jus/poc/prodcons/options/" + configurationFile));
	String key;
	int value;
	Class<?> thisOne = getClass();
	for (Map.Entry<Object, Object> entry : properties.entrySet()) {
	    key = (String) entry.getKey();
	    value = Integer.parseInt((String) entry.getValue());
	    thisOne.getDeclaredField(key).set(this, value);
	}
	if(false){
	    for(Map.Entry<Object, Object> entry : properties.entrySet()){
		key = (String) entry.getKey();
		System.out.println(key + "=" + thisOne.getDeclaredField(key).get(this));
	    }
	}
	observateur.init(nbProd, nbCons, nbBuffer);
    }

    public static void main(String[] args) {
	new TestProdCons(new Observateur()).start();
    }

}
