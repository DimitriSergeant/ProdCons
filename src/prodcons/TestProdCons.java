package prodcons;

import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class TestProdCons extends Simulateur {
	private static String configurationFile = "conf.v3.xml";
	
	private int nbProd = 0;
	private int nbCons = 0;
	private int nbBuffer = 0;
	private int tempsMoyenProduction = 0;
	private int deviationtempsMoyenProduction = 0;
	private int tempsMoyenConsommation = 0;
	private int deviationtempsMoyenConsommation = 0;
	private int nombreMoyenProduction = 0;
	private int deviationNombreMoyenProduction = 0;
	
	private int signalNombreDeProduction = 0;
	
	private ProdCons buffer;
	
	public TestProdCons(Observateur observateur){super(observateur);}

	@Override
	protected void run() throws Exception {
		// TODO Auto-generated method stub
		}
	public static void main(String[] args){new TestProdCons(new Observateur()).start();}
	
}

