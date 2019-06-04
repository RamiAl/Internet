import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;

class StatikIpSetUp extends JFrame{
	private JButton submit;
	private JButton cancel;
	private JTextField ipAddress;
	private JTextField subnetMask;
	private JTextField gateway;
	private JTextField dnsServer;
	private JLabel messageToUser;

	public StatikIpSetUp() {
		super ("Set IP");
		getContentPane().setLayout(null);
		
		submit = new JButton("OK");
		submit.setBounds(634, 357, 89, 32);
		getContentPane().add(submit);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(159, 356, 89, 35);
		getContentPane().add(cancel);
		
		JLabel lblIpAddress = new JLabel("IP address");
		lblIpAddress.setBounds(313, 91, 79, 20);
		getContentPane().add(lblIpAddress);
		
		JLabel lblNewLabel = new JLabel("Subnet mask");
		lblNewLabel.setBounds(295, 148, 97, 20);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Default gateway");
		lblNewLabel_1.setBounds(274, 206, 118, 20);
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("DNS servers");
		lblNewLabel_2.setBounds(301, 265, 91, 20);
		getContentPane().add(lblNewLabel_2);
		
		ipAddress = new JTextField();
		ipAddress.setBounds(444, 88, 146, 26);
		getContentPane().add(ipAddress);
		ipAddress.setColumns(10);
		
		subnetMask = new JTextField();
		subnetMask.setBounds(444, 145, 146, 26);
		getContentPane().add(subnetMask);
		subnetMask.setColumns(10);
		
		gateway = new JTextField();
		gateway.setBounds(444, 203, 146, 26);
		getContentPane().add(gateway);
		gateway.setColumns(10);
		
		dnsServer = new JTextField();
		dnsServer.setBounds(444, 262, 146, 26);
		getContentPane().add(dnsServer);
		dnsServer.setColumns(10);
	    
	    messageToUser = new JLabel("Some input are missing!");
	    messageToUser.setBounds(465, 331, 205, 90);
	    getContentPane().add(messageToUser);
	    messageToUser.setVisible(false);
		
		TheHandler handler = new TheHandler();
		submit.addActionListener(handler);
		cancel.addActionListener(handler);
	}
	
	class TheHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {

			if ((event.getSource() == submit)) {
				if (ipAddress.getText().equals("") | gateway.getText().equals("") | dnsServer.getText().equals("") 
						| subnetMask.getText().equals("")) {
					messageToUser.setForeground (Color.red);
					messageToUser.setText("Some input are missing!");
					messageToUser.setVisible(true);
					
				}else {
					Runtime rt = Runtime.getRuntime();
					try {
						Process proc = rt.exec("sudo ifconfig eth0 "+ipAddress.getText()+" "
								+ "netmask "+subnetMask.getText());
						proc.waitFor(10, TimeUnit.SECONDS);
						
						proc = rt.exec("sudo route add default gw "+gateway.getText());
						proc.waitFor(10, TimeUnit.SECONDS);
						
						ProcessBuilder builder = new ProcessBuilder();
						builder.command(new String[] {"bash", "-c", "echo \""+dnsServer.getText()+"\" > resolv.conf"});
						proc = builder.start();
						proc.waitFor(10, TimeUnit.SECONDS);
						
						proc = rt.exec("sudo cp resolv.conf /etc/resolv.conf");
						proc.waitFor(10, TimeUnit.SECONDS);
						
						messageToUser.setText("Successfully!");
						messageToUser.setVisible(true);
						new Thread(){
				    		 public void run(){
					    		EventQueue.invokeLater( () -> {
					    			try {
										TimeUnit.SECONDS.sleep(5);
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
				}
				
			}else  {
				dispose();
			}
		}
		
	}
}