import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CrackerTest {

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
    public void emptyArgs(){
        String [] args = {};
        assertThrows(RuntimeException.class, ()-> {Cracker.main(args);});
    }

    @Test
    public void testHashCode() throws InterruptedException, NoSuchAlgorithmException {
        String [] args = {"a!"};
        String testString = "34800e15707fae815d7c90d49de44aca97e2d759\n";
        provideInput(testString);
        Cracker.main(args);
        assertEquals(getOutput(), testString);
    }
    @Test
    public void testCrack() throws InterruptedException, NoSuchAlgorithmException {
        String [] args = {"34800e15707fae815d7c90d49de44aca97e2d759", "2", "4"};
        String testString = "a!\n" +
                "All done\n";
        provideInput(testString);
        Cracker.main(args);
        assertEquals(getOutput(), testString);
    }

//    @Test
//    public void testCrack2() throws InterruptedException, NoSuchAlgorithmException {
//        String [] args = {"66b27417d37e024c46526c2f6d358a754fc552f3", "3", "3"};
//        String testString = "xyz\n" +
//                "All done\n";
//        provideInput(testString);
//        Cracker.main(args);
//        assertEquals(getOutput(), testString);
//    }


}