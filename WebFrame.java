import javax.sql.rowset.WebRowSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class WebFrame extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private JButton singleThreadFetch,concurrentFetch,stopButton;
    private JTextField textField;
    private JLabel completed,elapsed,running;
    private JProgressBar progressBar;
    private int runningThreads,completedThreads;
    private TheThread theThread = null;

    public WebFrame(String fileName) throws IOException {
        runningThreads = 0;
        completedThreads = 0;
        createAndShowGUI();
        loadFile(fileName);
    }

    private void loadFile(String fileName) throws IOException {
        BufferedReader bf = new BufferedReader(new FileReader(new File(fileName)));
        String line = bf.readLine();
        while(line != null){
            Object[] obj = {line, ""};
            model.addRow(obj);
            line = bf.readLine();
        }
        progressBarInit();
    }

    private void createAndShowGUI(){
        setTitle("WebLoader");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        tableInit();
        buttonsInit();
        textFieldInit();
        labelsInit();
        finalInit();
    }

    private void progressBarInit() {
        progressBar = new JProgressBar();
        progressBar.setMaximum(model.getRowCount());
        add(progressBar);
        add(stopButton);
    }

    private void labelsInit() {
        running = new JLabel("Running:0");
        add(running);
        completed = new JLabel("Completed:0");
        add(completed);
        elapsed = new JLabel("Elapsed:");
        add(elapsed);
    }

    private void textFieldInit() {
        textField = new JTextField("4",4);
        textField.setMaximumSize(textField.getPreferredSize());
        add(textField);
    }

    private void buttonsInit() {
        singleThreadFetch = new JButton("Single Thread Fetch");
        add(singleThreadFetch);
        singleThreadFetch.addActionListener(new SingleThreadFetchListener());
        concurrentFetch = new JButton("Concurrent Fetch");
        add(concurrentFetch);
        concurrentFetch.addActionListener(new ConcurrentThreadFetchListener());
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new StopButtonListener());


    }

    private void tableInit(){
        model = new DefaultTableModel(new String[] {"url", "status"}, 0);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));
        add(scrollpane);
    }

    private void finalInit(){
        setLocationByPlatform(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public class ConcurrentThreadFetchListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            runningThreads = 0;
            completedThreads = 0;
            lockButtons("bla");
            int threadsNumber = Integer.parseInt(textField.getText());
            theThread = new TheThread(threadsNumber);
            theThread.start();
        }
    }
    public class SingleThreadFetchListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            runningThreads = 0;
            completedThreads = 0;
            lockButtons("bla");
            theThread = new TheThread(1);
            theThread.start();
        }
    }

    public class StopButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            lockButtons("stop");
            theThread.interrupt();
        }
    }

    private void lockButtons(String buttonName){{
        if(buttonName.equals("stop")){
            singleThreadFetch.setEnabled(true);
            concurrentFetch.setEnabled(true);
            stopButton.setEnabled(false);
            if(runningThreads == 0 && completedThreads == 0){
                running.setText("Running:0");
                completed.setText("Completed:0");
            }
        }else{
            elapsed.setText("Elapsed:");
            singleThreadFetch.setEnabled(false);
            concurrentFetch.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    }

    public class TheThread extends Thread{
        private Semaphore sem;
        public TheThread(int threadNumber){
            sem = new Semaphore(threadNumber);
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            ArrayList<WebWorker> workers = new ArrayList<>();
            int rowNumber = model.getRowCount();
            for(int i = 0; i < rowNumber; i++){
                String url = (String)model.getValueAt(i,0);
                WebFrame webFrame = WebFrame.this;
                WebWorker webWorker = new WebWorker(url,i,sem,webFrame);
                workers.add(webWorker);
                webWorker.start();
            }
            for(WebWorker webWorker : workers){
                try {
                    webWorker.join();
                } catch (InterruptedException e) {
                    for (WebWorker thread : workers) {
                        thread.interrupt();
                    }
                    break;
                }
            }
            long endTime = System.currentTimeMillis();
            elapsed.setText("Elapsed:" + (endTime - startTime) + "ms");
            lockButtons("stop");
        }
    }

    public synchronized void increase() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                running.setText("Running:"+ Integer.toString(++runningThreads));
            }
        });
    }

    public synchronized void deacrease(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                running.setText("Running:"+ Integer.toString(--runningThreads));
            }
        });
    }

    public synchronized void completed(int row, String str){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String output = "Completed:" + (++completedThreads);
                completed.setText(output);
                progressBar.setValue(completedThreads);
                model.setValueAt(str,row,1);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        String fileName = System.getProperty("user.dir") + "/src/" + "links.txt";
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new WebFrame(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
