import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class worker implements Runnable{
    private Transaction trans;
    private CountDownLatch latch;
    private ArrayBlockingQueue<Transaction> queue;
    public static final Transaction nullTrans = new Transaction(0,0,-1);
    private ArrayList<Account> accs;
    private Semaphore empty;
    private Semaphore full;

    public worker(ArrayList<Account> accs, CountDownLatch latch, ArrayBlockingQueue<Transaction> queue,Semaphore empty,Semaphore full){
        this.accs = accs;
        this.queue = queue;
        this.latch = latch;
        this.empty = empty;
        this.full = full;
    }

    @Override
    public void run() {
        while(true){
            try {
                full.acquire();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Transaction current = queue.poll();
            if(current.from == nullTrans.from && current.to == nullTrans.to && current.amount == nullTrans.amount){
                latch.countDown();
                empty.release();
                return;
            }
            Account from = accs.get(current.from);
            Account To = accs.get(current.to);
            int amount = current.amount;
            if(from != To){
                try {
                    from.wirthDraw(amount);
                    To.deposit(amount);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            empty.release();
        }

    }
}
