// Account.java

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 Simple, thread-safe Account class encapsulates
 a balance and a transaction count.
*/
public class Account {
	private int id;
	private int balance;
	private int transactions;
	private Bank bank;
	private Lock lock;
	private Condition cond;

	// It may work out to be handy for the account to
	// have a pointer to its Bank.
	// (a suggestion, not a requirement)


	public Account(Bank bank, int id, int balance) {
		this.bank = bank;
		this.id = id;
		this.balance = balance;
		transactions = 0;
		lock = new ReentrantLock();
		cond = lock.newCondition();
	}

	public int getBalance(){
		return balance;
	}
	public void wirthDraw(int amount) throws InterruptedException {
		lock.lock();
		balance -= amount;
		transactions++;
		lock.unlock();
	}

	public void deposit(int amount){
		lock.lock();
		balance += amount;
		transactions++;
		lock.unlock();
	}

	public synchronized String toString(){
		return "Balance :" + Integer.toString(balance) + " Transactions: " + Integer.toString(transactions);
	}




}
