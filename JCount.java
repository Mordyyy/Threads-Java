// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JCount extends JPanel {
	private WorkerThread workerThread = null;
	private JTextField inputField;
	private JLabel label;
	private JButton startButton,stopButton;

	public JCount() {
		// Set the JCount to use Box layout
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		inputField = new JTextField();
		inputField.setText("100000000");
		label = new JLabel("0");
		startButton = new JButton("START");
		startButton.addActionListener(new StartButtonListener());
		stopButton = new JButton("STOP");
		stopButton.addActionListener(new StopButtonListener());

		add(inputField);
		add(label);
		add(startButton);
		add(stopButton);
	}

	public class StartButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(workerThread != null){
				workerThread.interrupt();
			}
			workerThread = new WorkerThread(Integer.parseInt(inputField.getText()));
			workerThread.start();
		}
	}

	public class StopButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(workerThread != null){
				workerThread.interrupt();
				workerThread = null;
			}
		}
	}

	private static void createAndShowGUI(){
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.add(new JCount());
		frame.add(Box.createRigidArea(new Dimension(0,40)));
		frame.add(new JCount());
		frame.add(Box.createRigidArea(new Dimension(0,40)));
		frame.add(new JCount());
		frame.add(Box.createRigidArea(new Dimension(0,40)));
		frame.add(new JCount());

		frame.setLocationByPlatform(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private class WorkerThread extends Thread{
		private int countNumber;
		private final int refreshInterval = 10000;
		public WorkerThread(int countNumber){
			this.countNumber = countNumber;
		}
		@Override
		public void run() {
			for(int i = 0; i <= countNumber; i++){
				if (isInterrupted())
					break;
				if(i % refreshInterval == 0){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
					String currentNumber = Integer.toString(i);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							label.setText(currentNumber);
						}
					});
				}
			}
		}
	}

	static public void main(String[] args)  {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

