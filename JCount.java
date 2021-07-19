// JCount.java

/*
 Basic GUI/Threading exercise.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CountDownLatch;

public class JCount extends JPanel {
	JButton StartButton;
	JButton StopButton;
	JTextField textField;
	JLabel label;
	private boolean isAlready = false;
	private boolean toStop = false;
	private Thread thread;

	public JCount() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		StartButton = new JButton("Start");
		StopButton = new JButton("Stop ");
		textField = new JTextField();
		textField.setSize(20,10);
		label = new JLabel("0");
		label.setSize(20,10);
		this.add(textField);
		this.add(Box.createRigidArea(new Dimension(0,4)));
		this.add(label);
		this.add(Box.createRigidArea(new Dimension(0,4)));
		this.add(StartButton);
		this.add(StopButton);
		setUpListeners();
	}


	public class counter implements Runnable{
		private int countTo;

		public counter(int countTo){
			this.countTo = countTo;
		}
		@Override
		public void run() {
			for(int i = 0;i <= countTo;i++){
				if(i % 10000 == 0 || i == countTo){
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						break;
					}
					int finalI = i;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							label.setText(Integer.toString(finalI));
						}
					});
				}
			}
		}
	}

	private void setUpListeners(){
		StartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(thread != null && thread.isAlive()){
					thread.interrupt();
				}
				int num = 100000000;
				if(textField.getText().length() != 0)num = Integer.parseInt(textField.getText());
				thread = new Thread(new counter(num));
				thread.start();
			}
		});

		StopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(thread != null && thread.isAlive()){
					thread.interrupt();

				}

			}
		});
	}



	static public void main(String[] args)  {
		// Creates a frame with 4 JCounts in it.
		// (provided)
		JFrame frame = new JFrame("The Count");
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.add(new JCount());
		frame.setSize(200,400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);


	}
}

