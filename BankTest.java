import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class BankTest {

    private final InputStream si = System.in;
    private final PrintStream so = System.out;

    private ByteArrayOutputStream tout;

    @BeforeEach
    public void init() {
        tout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(tout));
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return tout.toString();
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(si);
        System.setOut(so);
    }
    @Test
    public void withoutArguments(){
        String[] args = {};
        assertThrows(RuntimeException.class, ()-> {Bank.main(args);});
    }

    @Test
    public void withOneArgument() throws IOException, InterruptedException {
        String[] args = {"/src/small.txt"};
        String testString = "All done\n" +
                "acct:0 bal:999 trans:1\n" +
                "acct:1 bal:1001 trans:1\n" +
                "acct:2 bal:999 trans:1\n" +
                "acct:3 bal:1001 trans:1\n" +
                "acct:4 bal:999 trans:1\n" +
                "acct:5 bal:1001 trans:1\n" +
                "acct:6 bal:999 trans:1\n" +
                "acct:7 bal:1001 trans:1\n" +
                "acct:8 bal:999 trans:1\n" +
                "acct:9 bal:1001 trans:1\n" +
                "acct:10 bal:999 trans:1\n" +
                "acct:11 bal:1001 trans:1\n" +
                "acct:12 bal:999 trans:1\n" +
                "acct:13 bal:1001 trans:1\n" +
                "acct:14 bal:999 trans:1\n" +
                "acct:15 bal:1001 trans:1\n" +
                "acct:16 bal:999 trans:1\n" +
                "acct:17 bal:1001 trans:1\n" +
                "acct:18 bal:999 trans:1\n" +
                "acct:19 bal:1001 trans:1\n";

        provideInput(testString);

        Bank.main(args);

        assertEquals(testString, getOutput());
    }
    @Test
    public void twoArguments() throws IOException, InterruptedException {
        String[] args = {"/src/5k.txt", "4"};
        final String testString = "All done\n" +
                "acct:0 bal:1000 trans:518\n" +
                "acct:1 bal:1000 trans:444\n" +
                "acct:2 bal:1000 trans:522\n" +
                "acct:3 bal:1000 trans:492\n" +
                "acct:4 bal:1000 trans:526\n" +
                "acct:5 bal:1000 trans:526\n" +
                "acct:6 bal:1000 trans:474\n" +
                "acct:7 bal:1000 trans:472\n" +
                "acct:8 bal:1000 trans:436\n" +
                "acct:9 bal:1000 trans:450\n" +
                "acct:10 bal:1000 trans:498\n" +
                "acct:11 bal:1000 trans:526\n" +
                "acct:12 bal:1000 trans:488\n" +
                "acct:13 bal:1000 trans:482\n" +
                "acct:14 bal:1000 trans:516\n" +
                "acct:15 bal:1000 trans:492\n" +
                "acct:16 bal:1000 trans:520\n" +
                "acct:17 bal:1000 trans:528\n" +
                "acct:18 bal:1000 trans:586\n" +
                "acct:19 bal:1000 trans:504\n";

        provideInput(testString);

        Bank.main(args);

        assertEquals(testString, getOutput());
    }
}