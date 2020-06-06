import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import javax.swing.*;

public class WebWorker extends Thread {
    private String url;
    private Semaphore sem;
    private WebFrame webFrame;
    private int row;
    public WebWorker(String url, int row, Semaphore sem, WebFrame webFrame) {
        this.url = url;
        this.sem = sem;
        this.row = row;
        this.webFrame = webFrame;
    }

    @Override
    public void run() {
        try {
            sem.acquire();
            webFrame.increase();
            download(url);
            webFrame.deacrease();
            sem.release();
        } catch (InterruptedException e) {
            webFrame.completed(row,"Interrupted exception");
        }

    }

    //  This is the core web/download i/o code...
    private void download(String urlString) {
        long startTime = System.currentTimeMillis();
        InputStream input = null;
        StringBuilder contents = null;
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            input = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            char[] array = new char[1000];
            int len;
            contents = new StringBuilder(1000);
            while ((len = reader.read(array, 0, array.length)) > 0) {
                contents.append(array, 0, len);
                Thread.sleep(100);
            }
            Date currentTime = new Date();
            long endTime = System.currentTimeMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String status = dateFormat.format(currentTime) + " " + (endTime - startTime) + "ms " + contents.length() + "bytes";
            webFrame.completed(row, status);
        }
        // Otherwise control jumps to a catch...
        catch (MalformedURLException ignored) {
            webFrame.completed(row, "Url exception");
        } catch (InterruptedException exception) {
            webFrame.completed(row, "interrupted exception");
        } catch (IOException ignored) {
            webFrame.completed(row, "Ignored :(");
        }
        finally {
            try {
                if (input != null) input.close();
            } catch (IOException ignored) {
            }
        }
    }
}
