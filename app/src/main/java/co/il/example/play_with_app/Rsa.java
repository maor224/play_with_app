package co.il.example.play_with_app;

import java.math.BigInteger;
import java.util.Arrays;

public class Rsa {
    private static final int p = 19;
    private static final int q = 29;
    public static BigInteger n = new BigInteger(String.valueOf(p * q));
    private static final int phi = getPhi(Integer.parseInt(String.valueOf(n)));
    public static BigInteger e = new BigInteger(String.valueOf(17));
    private static BigInteger d = new BigInteger(String.valueOf(0));


    public static int getPhi(int n) {

        // Create and initialize an array to store
        // phi or totient values
        int[] phi = new int[n + 1];
        for (int i = 1; i <= n; i++)
            phi[i] = i; // indicates not evaluated yet
        // and initializes for product
        // formula.

        // Compute other Phi values
        for (int p = 2; p <= n; p++) {

            // If phi[p] is not computed already,
            // then number p is prime
            if (phi[p] == p) {

                // Phi of a prime number p is
                // always equal to p-1.
                phi[p] = p - 1;

                // Update phi values of all
                // multiples of p
                for (int i = 2 * p; i <= n; i += p) {

                    // Add contribution of p to its
                    // multiple i by multiplying with
                    // (1 - 1/p)
                    phi[i] = (phi[i] / p) * (p - 1);
                }
            }
        }


        return phi[phi.length - 1];
    }


    public static String doEnc (String message){
        int d1 = 0;
        for (int i = 0; i <= 9; i++) {
            // d * e = 1 + (i  * phi)
            int x = 1 + (i * phi);

            // d is for private key exponent
            if (x % Integer.parseInt(e.toString()) == 0) {
                d1 = x / Integer.parseInt(e.toString());
                break;
            }
        }
        d = new BigInteger(String.valueOf(d1));

        BigInteger[] private_key = {d, n};
        BigInteger[] public_key = {e, n};

        BigInteger[] arr = new BigInteger[message.length()];
        BigInteger num;
        for (int i = 0; i < arr.length; i++) {
            num = new BigInteger(String.valueOf(((int) message.charAt(i))));
            arr[i] = num;
        }
        BigInteger[] enc = encryption(arr);
        String encrypt = "";
        for (int i = 0;i < enc.length;i++){
            encrypt += enc[i].toString() + ",";
        }
        return encrypt;
    }

    public static String doDec (String message){
        int d1 = 0;
        for (int i = 0; i <= 9; i++) {
            int x = 1 + (i * phi);

            // d is for private key exponent
            if (x % Integer.parseInt(e.toString()) == 0) {
                d1 = x / Integer.parseInt(e.toString());
                break;
            }
        }
        d = new BigInteger(String.valueOf(d1));

        BigInteger[] private_key = {d, n};
        BigInteger[] public_key = {e, n};
        String[] enc = message.split(",");
        BigInteger[] encrypt = new BigInteger[enc.length];
        for (int i = 0; i < encrypt.length; i++) {
            encrypt[i] = new BigInteger(enc[i]);
        }

        String messageD = decryption(encrypt);
        return messageD;
    }

    public static BigInteger[] encryption (BigInteger[] arr){
        BigInteger[] a = new BigInteger[arr.length];
        for (int i = 0;i < arr.length;i++){
            a[i] = arr[i].modPow(e, n);
        }
        String message = "";
        for (int i = 0;i < a.length;i++){
            message += (char) (Integer.parseInt(a[i].toString()));
        }
        return a;
    }
    public static String decryption (BigInteger[] arr){
        BigInteger[] a = new BigInteger[arr.length];
        for (int i = 0;i < arr.length;i++){
            a[i] = arr[i].modPow(d, n);
        }
        String message = "";
        for (int i = 0;i < a.length;i++){
            message += (char) (Integer.parseInt(a[i].toString()));
        }
        return message;
    }
}
