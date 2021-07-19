// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
	private static String crackedPassword;
	CountDownLatch latch;
	boolean isFound;

	public synchronized void  Found(){
			isFound = true;
	}

	public String getPassword(){
		return crackedPassword;
	}

	public synchronized  void FoundPassword(String password){
		crackedPassword = new String(password);
	}
	/*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/

	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff;  // remove higher bits, sign
			if (val<16) buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}
	/*
	 Given a string of hex byte values such as "24a26f", creates
	 a byte[] array of those values, one byte value -128..127
	 for each 2 chars.
	 (provided code)
	*/
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length()/2];
		for (int i=0; i<hex.length(); i+=2) {
			result[i/2] = (byte) Integer.parseInt(hex.substring(i, i+2), 16);
		}
		return result;
	}
	
	public byte[] generateMode(String password){
		byte[] bytes = password.getBytes();
		byte[] hashcode = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			hashcode = md.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashcode;
	}

	public String toString(){
		return crackedPassword;
	}

	public void crackMode(String Hashpassword,int passwordLength,int numThreads) throws InterruptedException {
		latch = new CountDownLatch(numThreads);
		int div = CHARS.length / numThreads;
		int rem = CHARS.length % numThreads;
		int startIndex = 0;
		for (int i = 0; i < numThreads; i++) {
			if(rem > 0){
				new Thread(new crackerThread(startIndex,startIndex + div + 1,Hashpassword,passwordLength)).start();
//				System.out.println(startIndex + " " + (startIndex + div + 1));
				startIndex += div + 1;
				rem--;
			}else{
				new Thread(new crackerThread(startIndex,startIndex + div,Hashpassword,passwordLength)).start();
//				System.out.println(startIndex + " " + (startIndex + div));
				startIndex += div;
			}
		}
		latch.await();
		isFound = false;
	}


	public class crackerThread implements Runnable{
		private int startIndex;
		private int endIndex;
		String hashPassword;
		private int passwordLength;

		public crackerThread(int startIndex,int endIndex,String hashPassword,int passwordLength) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.hashPassword = hashPassword;
			this.passwordLength = passwordLength;
		}


		private void crack(String curr,int size){
			if(isFound){
				return;
			}
			if(size == 0){
				if(hexToString(hexToArray(hashPassword)).equals(hexToString(generateMode(curr))) ){
					FoundPassword(curr);
					Found();
					Thread.currentThread().interrupt();
				}
				return;
			}
			for(int i = 0;i < CHARS.length;i++){
				crack(curr + CHARS[i],size - 1);
			}
		}

		@Override
		public void run() {
			String temp = "";
			for(int i = startIndex;i < endIndex;i++){
				crack(temp + CHARS[i],passwordLength - 1);
			}
			latch.countDown();
		}

	}

//	public static void main(String[] args) {
////		System.out.println(hexToString(crack.generateMode("a!")));
////		System.out.println(hexToString(crack.generateMode("xyz")));
//		if (args.length < 2) {
//			System.out.println("Args: target length [workers]");
//			System.exit(1);
//		}
//		// args: targ len [num]
//		String targ = args[0];
//		int len = Integer.parseInt(args[1]);
//		int num = 1;
//		if (args.length>2) {
//			num = Integer.parseInt(args[2]);
//		}
//		Cracker crack = new Cracker();
//		try {
//			crack.crackMode(targ,len,num);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println(crack.toString());
//
//		// a! 34800e15707fae815d7c90d49de44aca97e2d759
//		// xyz 66b27417d37e024c46526c2f6d358a754fc552f3
//
//	}
}
