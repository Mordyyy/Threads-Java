// Cracker.java
/*
 Generates SHA hashes of short strings in parallel.
*/

import java.lang.reflect.Array;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Cracker {
    // Array of chars used to produce strings
    public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();
    private static final int CHARS_LENGTH = 40;
    private static List<Worker> workers = new ArrayList<>();
    private static HashMap<Integer, ArrayList<Character>> ranges;
    private static int passwordLength, workersNumber;
    private static CountDownLatch countDownLatch;
    private static String hashString;
    private static String crackedPassword;
    private static boolean isFound;


    /*
     Given a byte[] array, produces a hex String,
     such as "234a6f". with 2 chars for each byte in the array.
     (provided code)
    */
    public static String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val < 16) buff.append('0'); // leading 0
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
//    public static byte[] hexToArray(String hex) {
//        byte[] result = new byte[hex.length() / 2];
//        for (int i = 0; i < hex.length(); i += 2) {
//            result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
//        }
//        return result;
//    }


    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
        if (args.length < 1) {
            throw new RuntimeException("Args: target length [workers]");
        }

        if (args.length == 1) {
            String targ = args[0];
            System.out.println(stringToHash(targ));
        } else {

            hashString = args[0];
            passwordLength = Integer.parseInt(args[1]);
            workersNumber = Integer.parseInt(args[2]);
            countDownLatch = new CountDownLatch(workersNumber);
            isFound = false;

            fillRanges();
            workersInit();

            System.out.println(crackedPassword);
            System.out.println("All done");
        }
        // a! 34800e15707fae815d7c90d49de44aca97e2d759
        // xyz 66b27417d37e024c46526c2f6d358a754fc552f3

    }

    private static void workersInit() throws InterruptedException {
        workers.clear();
        for (int i = 0; i < workersNumber; i++)
            workers.add(new Worker());
        for (int i = 0; i < workersNumber; i++)
            workers.get(i).setName(Integer.toString(i));
        for (int i = 0; i < workersNumber; i++) {
            workers.get(i).start();
        }

        countDownLatch.await();
        for (int i = 0; i < workersNumber; i++) {
            workers.get(i).interrupt();
        }
        for (int i = 0; i < workersNumber; i++) {
            workers.get(i).join();
        }

    }

    private static void fillRanges() {
        ranges = new HashMap<>();
        int remainder = CHARS_LENGTH % workersNumber;
        int res = CHARS_LENGTH / workersNumber;
        int workerId = 0;
        for (int i = 0; i < workersNumber; i++) {
            ranges.put(i, new ArrayList<>());
        }
        for (int i = 0; i < CHARS_LENGTH - remainder; i++) {
            ArrayList<Character> arr = ranges.get(workerId);
            arr.add(CHARS[i]);
            ranges.put(workerId, arr);
            if (arr.size() == res)
                workerId++;
        }

//        workerId = 0;
//        for(int i = CHARS_LENGTH - remainder; i < CHARS_LENGTH; i++){
//            ArrayList<Character> arr = ranges.get(workerId);
//            arr.add(CHARS[i]);
//            ranges.put(workerId, arr);
//            workerId++;
//        }
    }

    private static class Worker extends Thread {

        private void generate(int length, String current) throws NoSuchAlgorithmException {
            if (isFound) return;
            if (length == 0) {
                String currentHash = stringToHash(current);
                if (currentHash.equals(hashString)) {
                    isFound = true;
                    crackedPassword = current;
                }
                return;
            }
            for (int i = 0; i < CHARS.length; i++) {
                String tmp = current + CHARS[i];
                generate(length - 1, tmp);
            }
        }

        @Override
        public void run() {
            int id = Integer.parseInt(Thread.currentThread().getName());
            ArrayList<Character> arr = ranges.get(id);
            for (int i = 0; i < arr.size(); i++) {
                Character ch = arr.get(i);
                try {
                    generate(passwordLength - 1, "" + ch);
                } catch (NoSuchAlgorithmException e) {}
            }
            countDownLatch.countDown();
        }
    }

    private static String stringToHash(String str) throws NoSuchAlgorithmException {
        MessageDigest ms = MessageDigest.getInstance("SHA");
        byte[] bytes = ms.digest(str.getBytes());
        return hexToString(bytes);
    }
}
