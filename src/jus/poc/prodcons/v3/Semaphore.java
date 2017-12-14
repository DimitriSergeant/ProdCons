package jus.poc.prodcons.v3;

public class Semaphore {
    
    private int residu;

    public Semaphore(int n) {
	residu = n;
    }
    
    public synchronized void P() throws InterruptedException {
	if(--residu < 0) wait();
    }
    
    public synchronized void V() {
	if(++residu <= 0) notify();
    }

}
