// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import javax.accessibility.AccessibleAction;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Bank {
	public static final int ACCOUNTS = 20;	 // number of accounts
	private ArrayList<Account> accs;
	private ArrayList<Transaction> Transactions;
	private CountDownLatch latch;
	private Semaphore empty;
	private Semaphore full;
	/*
	 Reads transaction data (from/to/amt) from a file for processing.
	 (provided code)
	 */
	public boolean testAccounts(ArrayList<Integer> balances){
		for(int i = 0;i < balances.size();i++){
			if(balances.get(i) != accs.get(i).getBalance())return false;
		}
		return true;
	}


	private void initialiseAccs(){
		accs = new ArrayList<>();
		for (int i = 0; i < ACCOUNTS; i++) {
			accs.add(new Account(this,i,1000));
		}
	}


	public void readFile(String file) {
			try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			// Use stream tokenizer to get successive words from file
			StreamTokenizer tokenizer = new StreamTokenizer(reader);
			while (true) {
				int read = tokenizer.nextToken();
				if (read == StreamTokenizer.TT_EOF) break;  // detect EOF
				int from = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int to = (int)tokenizer.nval;
				
				tokenizer.nextToken();
				int amount = (int)tokenizer.nval;
				Transactions.add(new Transaction(from,to,amount));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 Processes one file of transaction data
	 -fork off workers
	 -read file into the buffer
	 -wait for the workers to finish
	*/
	public void processFile(String file, int numWorkers) throws InterruptedException {
		latch = new CountDownLatch(numWorkers);
		Transactions = new ArrayList<Transaction>();
		readFile(file);
		initialiseAccs();
		ArrayBlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(Buffer.SIZE);
		empty = new Semaphore(Buffer.SIZE);
		full = new Semaphore(0);

		for (int i = 0; i < numWorkers; i++) {
			Thread newThread = new Thread(new worker(accs,latch,queue,empty,full));
			newThread.start();
		}

		for (int i = 0; i < Transactions.size(); i++) {
			empty.acquire();
			queue.add(Transactions.get(i));
			full.release();
		}

		for(int i = 0; i < numWorkers;i++){
			empty.acquire();
			queue.add(worker.nullTrans);
			full.release();
		}
		latch.await();
		for(int i = 0;i < accs.size();i++){
			System.out.println(accs.get(i).toString());
		}
	}

	
	
	/*
	 Looks at commandline args and calls Bank processing.
	*/
//	public static void main(String[] args) {
//		// deal with command-lines args
//		if (args.length == 0) {
//			System.out.println("Args: transaction-file [num-workers [limit]]");
//			System.exit(1);
//		}
//
//		String file = args[0];
//
//		int numWorkers = 1;
//		if (args.length >= 2) {
//			numWorkers = Integer.parseInt(args[1]);
//		}
//		Bank bank = new Bank();
//		try {
//			bank.processFile(args[0],Integer.parseInt(args[1]));
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}

