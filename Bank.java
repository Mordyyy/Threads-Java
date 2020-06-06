// Bank.java

/*
 Creates a bunch of accounts and uses threads
 to post transactions to the accounts concurrently.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Bank {
    public static final int ACCOUNTS = 20;     // number of accounts
    private static final Transaction nullTrans = new Transaction(-1, 0, 0);
    private static final int defaultSalary = 1000;
    private static List<Account> accounts;
    private static List<Worker> workerList;
    private static BlockingQueue<Transaction> transactions;
    private static CountDownLatch countDownLatch;


    public Bank() {
        transactions = new LinkedBlockingQueue<>();
        accounts = new ArrayList<>();
        for (int i = 0; i < ACCOUNTS; i++) {
            accounts.add(new Account(i, defaultSalary));
        }
    }

    /*
     Reads transaction data (from/to/amt) from a file for processing.
     (provided code)
     */
    public void readFile(String file) throws IOException {
        countDownLatch = new CountDownLatch(workerList.size());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        while (true) {
            int read = tokenizer.nextToken();
            if (read == StreamTokenizer.TT_EOF) {
                for (int i = 0; i < workerList.size(); i++) {
                    transactions.add(nullTrans);
                }
                break;
            }
            int from = (int) tokenizer.nval;

            tokenizer.nextToken();
            int to = (int) tokenizer.nval;

            tokenizer.nextToken();
            int amount = (int) tokenizer.nval;

            transactions.add(new Transaction(from, to, amount));
        }
    }

    private static void waitForWorkers() throws InterruptedException {
        countDownLatch.await();
    }

    /*
     Processes one file of transaction data
     -fork off workers
     -read file into the buffer
     -wait for the workers to finish
    */
    public void processFile(String file, int numWorkers) throws IOException {
        workerList = new ArrayList<>();
        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker();
            workerList.add(worker);
            workerList.get(i).start();
        }
        readFile(file);
    }

    private static void printResult() {
        System.out.println("All done");
        for (int i = 0; i < ACCOUNTS; i++) {
            System.out.printf("acct:" + i + " bal:" + accounts.get(i).getBalance() + " trans:" + accounts.get(i).getTransactions() + "\n");
        }
    }

    private static class Worker extends Thread {
        @Override
        public void run(){
            while (true) {

                Transaction transaction = null;
                try {
                    transaction = transactions.take();
                } catch (InterruptedException e) {};
                if (transaction.equals(nullTrans)) {
                        countDownLatch.countDown();
                        break;
                    }
                    int from = transaction.getFrom();
                    int to = transaction.getTo();
                    int amount = transaction.getAmount();
                    accounts.get(from).makeTransaction(-amount);
                    accounts.get(to).makeTransaction(amount);

            }
        }
    }

	/*
	 Looks at commandline args and calls Bank processing.
	*/

    public static void main(String[] args) throws InterruptedException, IOException, RuntimeException {
        if (args.length == 0) {
            System.out.println("Args: transaction-file [num-workers [limit]]");
            throw new RuntimeException("Args: transaction-file [num-workers [limit]]");
        }
        String file = args[0];
        int numWorkers = 1;
        if (args.length == 2) {
            numWorkers = Integer.parseInt(args[1]);
        }
        Bank bank = new Bank();
        String fileName = System.getProperty("user.dir") + "/" + file;
        bank.processFile(fileName, numWorkers);
        waitForWorkers();
        printResult();
    }
}

