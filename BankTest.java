import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class BankTest {
    @Test
    public void Test1() throws InterruptedException {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            temp.add(1000);
        }
        Bank bank = new Bank();
        bank.processFile("5k.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));
        bank = new Bank();
        bank.processFile("5k.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));
    }

    @Test
    public void Test2() throws InterruptedException {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            temp.add(1000);
        }
        Bank bank = new Bank();
        bank.processFile("100k.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));
        bank = new Bank();
        bank.processFile("100k.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));

    }

    @Test
    public void Test3() throws InterruptedException {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if(i % 2 == 0)temp.add(999);
            else temp.add(1001);
        }
        Bank bank = new Bank();
        bank.processFile("small.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));
        bank = new Bank();
        bank.processFile("small.txt",100);
        Assert.assertTrue(bank.testAccounts(temp));
    }











}
