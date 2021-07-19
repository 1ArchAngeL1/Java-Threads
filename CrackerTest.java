import org.junit.Assert;
import org.junit.Test;

public class CrackerTest {
    @Test
    public void Test1(){
        Cracker cracker = new Cracker();
        String hash = "34800e15707fae815d7c90d49de44aca97e2d759";
        int passwordSize = 2;
        try {
            cracker.crackMode(hash,passwordSize,1);
            Assert.assertEquals("a!",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // xyz 66b27417d37e024c46526c2f6d358a754fc552f3
        try {
            cracker.crackMode(hash,passwordSize,40);
            Assert.assertEquals("a!",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void Test2(){
        Cracker cracker = new Cracker();
        String hash = "66b27417d37e024c46526c2f6d358a754fc552f3";
        int passwordSize = 3;
        try {
            cracker.crackMode(hash,passwordSize,1);
            Assert.assertEquals("xyz",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // xyz 66b27417d37e024c46526c2f6d358a754fc552f3
        hash = "66b27417d37e024c46526c2f6d358a754fc552f3";
        passwordSize = 3;
        try {
            cracker.crackMode(hash,passwordSize,40);
            Assert.assertEquals("xyz",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test3() {
        Cracker cracker = new Cracker();
        byte[] bytes = cracker.generateMode("abcxz");
        String hash = Cracker.hexToString(bytes);
        try {
            cracker.crackMode(hash,5,1);
            Assert.assertEquals("abcxz",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            cracker.crackMode(hash,5,40);
            Assert.assertEquals("abcxz",cracker.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
