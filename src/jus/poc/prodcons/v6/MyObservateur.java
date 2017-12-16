package jus.poc.prodcons.v6;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jus.poc.prodcons.*;

public class MyObservateur{
	
	private boolean estCoherent = true;
	
	private int nbProd = 0;
	private int nbCons = 0;
	private int nbBuffer = 0;

	private HashSet<_Producteur> producteurSet;
	private HashSet<_Consommateur> consommateurSet;
	private Queue<Message> fileMessages;
	
	private Hashtable<Message, _Producteur> messagesProduit;
	private Hashtable<Message,_Consommateur> messagesDejaRetire;
	
	private final Lock verrou = new ReentrantLock();

	
	boolean	coherent() {
		return this.estCoherent; 
	}
	
	void consommationMessage(_Consommateur c, Message m, int tempsDeTraitement) throws ControlException {
		verrou.lock();
		//test des arguments
		if(c == null){
			throw new ControlException(null, "consommationMessage Paramètre: Consommateur null ");
		}
		if(m == null){
			throw new ControlException(c.getClass(), "consommationMessage Paramètre: Message null");
		}
		if(tempsDeTraitement<=0){
			throw new ControlException(c.getClass(), "consommationMessage Paramètre: tempsDeTraitement négatif");
		}
		// On teste si le consommateur existe bien  
		if (consommateurSet.contains(c) == false) {
			throw new ControlException(c.getClass(), "consommationMessage: Consommateur inexistant");
		}
		// On regarde si le message a bien été retiré par le bon consommateur, si oui on le retire du buffer.
		if (messagesDejaRetire.remove(m, c) == false) {
			throw new ControlException(c.getClass(), "consommationMessage: Le message n'a pas été retiré par ce consommateur");
		}
		verrou.unlock();
	}
	
	void depotMessage(_Producteur p, Message m) throws ControlException{
		verrou.lock();
		//test des arguments
		if(p == null){
			throw new ControlException(null, "depotMessage Paramètre: Producteur null");
		}
		if(m == null){
			throw new ControlException(p.getClass(), "depotMessage Paramètre: Message null");
		}
		// On teste si le consommateur existe bien  
		if (producteurSet.contains(p) == false) {
			throw new ControlException(p.getClass(), "depotMessage: Producteur inexistant");
		}
		// On regarde si le message a bien été produit par le bon producteur, si oui on le dépose dans le buffer.
		if (messagesProduit.remove(m, p) == false) {
			throw new ControlException(p.getClass(), "depotMessage: Le message n'a pas été produit par ce producteur");
		}
		// On verifie que la taille  du buffer ne soit pas dépassée
		if (fileMessages.size() >= nbBuffer) {
			throw new ControlException(p.getClass(), "depotMessage: Impossible de déposer ce message, le buffer est plein ");
		}
		fileMessages.add(m);
		verrou.unlock();
	}
	
	void init(int nbproducteurs, int nbconsommateurs, int nbBuffers) throws ControlException{
		verrou.lock();
		// Test des arguments
		if ((nbproducteurs <= 0) || (nbconsommateurs <= 0) || (nbBuffers <= 0)) {
			throw new ControlException(this.getClass(), "Init : Erreur dans les paramètres");
		}
		this.nbProd = nbproducteurs;
		this.nbCons = nbconsommateurs;
		this.nbBuffer = nbBuffers;
		
		this.producteurSet = new HashSet<_Producteur>();
		this.consommateurSet = new HashSet<_Consommateur>();
		this.fileMessages = new LinkedBlockingQueue<Message>();
		
		this.messagesProduit = new Hashtable<Message,_Producteur>();
		this.messagesDejaRetire = new Hashtable<Message, _Consommateur>();
		verrou.unlock();
	}
	
	void newConsommateur(_Consommateur c) throws ControlException{
		verrou.lock();
		//test des arguments
		if(c == null){
			throw new ControlException(null, "newConsommateur Paramètre: Consommateur null ");
		}
		// On verifie que le nombre de consommateurs soit respécté
		if (consommateurSet.size() >= nbCons) {
			throw new ControlException(c.getClass(), "newConsommateur: Le nombre de consommateurs max est déjà atteint ");
		}
		this.consommateurSet.add(c);
		verrou.unlock();
	}
	
	void newProducteur(_Producteur p) throws ControlException{
		verrou.lock();
		//test des arguments
		if(p == null){
			throw new ControlException(null, "newProducteur Paramètre: Producteur null ");
		}
		// On verifie que le nombre de producteurs soit respécté
		if (producteurSet.size() >= nbProd) {
			throw new ControlException(p.getClass(), "newProducteur: Le nombre de producteurs max est déjà atteint ");
		}
		this.producteurSet.add(p);
		verrou.unlock();
	}
	
	void productionMessage(_Producteur p, Message m, int tempsDeTraitement) throws ControlException{
		verrou.lock();
		//test des arguments
		if(p == null){
			throw new ControlException(null, "depotMessage Paramètre: Producteur null");
		}
		if(m == null){
			throw new ControlException(p.getClass(), "depotMessage Paramètre: Message null");
		}
		// On teste si le consommateur existe bien  
		if (producteurSet.contains(p) == false) {
			throw new ControlException(p.getClass(), "depotMessage: Producteur inexistant");
		}
		this.messagesProduit.put(m,p);
		verrou.unlock();
	}
	
	void retraitMessage(_Consommateur c, Message m) throws ControlException{
		verrou.lock();
		//test des arguments
		if(c == null){
			throw new ControlException(null, "retraitMessage Paramètre: Consommateur null ");
		}
		if(m == null){
			throw new ControlException(c.getClass(), "retraitMessage Paramètre: Message null");
		}
		// On teste si le consommateur existe bien  
		if (consommateurSet.contains(c) == false) {
			throw new ControlException(c.getClass(), "depotMessage: Producteur inexistant");
		}
		// On teste si le message n'a pas déjà été retiré 
		if(this.messagesDejaRetire.containsKey(m) == true){
			throw new ControlException(c.getClass(), "retraitMessage: Message déjà retiré");
		}
//		// On teste que le message que veut retirer le consommateur soit bien le premier message inséré dans la file
//		if (! (fileMessages.peek() == m )){
//			throw new ControlException(c.getClass(), "consommationMessage: Le message ne correspond pas au premier message mis dans le tampon");
//		}
		
		this.messagesDejaRetire.put(m, c);
		verrou.unlock();
	}
}
