import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

class IpSetOptions extends JFrame{
	private JButton dynamic;
	private JButton statik;
	private JButton cancel;
	private JLabel successfullyMessage;
	
	public IpSetOptions() {
		super ("Set IP");
		getContentPane().setLayout(null);
		
		dynamic = new JButton("Dynamic");
		dynamic.setBounds(345, 121, 148, 64);
		getContentPane().add(dynamic);
		
		statik = new JButton("Static");
		statik.setBounds(345, 220, 148, 64);
		getContentPane().add(statik);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(159, 356, 89, 35);
		getContentPane().add(cancel);
		
	    successfullyMessage = new JLabel("Successfully!");
	    successfullyMessage.setBounds(317, 317, 201, 112);
	    getContentPane().add(successfullyMessage);
	    successfullyMessage.setVisible(false);
		
		TheHandler handler = new TheHandler();
		statik.addActionListener(handler);
		dynamic.addActionListener(handler);
		cancel.addActionListener(handler);
	}
	
	class TheHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == statik) {
				StatikIpSetUp statikIp = new StatikIpSetUp();
				statikIp.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				statikIp.setSize(800, 480);
				statikIp.setVisible(true);
			}
			else if (event.getSource() == dynamic) {
				Runtime rt = Runtime.getRuntime();
				try {
					Process proc = rt.exec("sudo ifconfig eth0 0.0.0.0 0.0.0.0 && dhclient");
					proc.waitFor(10, TimeUnit.SECONDS);
					
					proc = rt.exec("rm resolv.conf");
					proc.waitFor(10, TimeUnit.SECONDS);
					successfullyMessage.setVisible(true);
					new Thread(){
			    		 public void run(){
				    		EventQueue.invokeLater( () -> {
				    			try {
									TimeUnit.SECONDS.sleep(3);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
				    			dispose();
				    		});
			    		}
		    		 } .start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else  {
				dispose();
			}
		}
		
	}
	
}