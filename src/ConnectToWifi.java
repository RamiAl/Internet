import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JLabel;

class ConnectToWifi extends JFrame{
	private JButton join;
	private JButton cancel;
	private JTextField textField;
	private String wifiName;
	private JLabel messageToUser;
	
	public ConnectToWifi() {
		super ("Password");
		getContentPane().setLayout(null);
		
		join = new JButton("Join");
		join.setBounds(634, 357, 89, 32);
		getContentPane().add(join);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(159, 356, 89, 35);
		getContentPane().add(cancel);
		
		textField = new JTextField();
		textField.setToolTipText("password");
		textField.setBounds(329, 209, 146, 26);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(362, 148, 76, 45);
		getContentPane().add(lblPassword);
		
	    messageToUser = new JLabel();
	    messageToUser.setBounds(362, 336, 181, 72);
	    getContentPane().add(messageToUser);
	    messageToUser.setVisible(false);
		
		TheHandler handler = new TheHandler();
		join.addActionListener(handler);
		cancel.addActionListener(handler);
		
	}
	
	public void setWifiName(String name) {
		this.wifiName = name;
	}
	
	class TheHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == cancel) {
				dispose();
			}
			else if (event.getSource() == join) {
				try {
					Runtime rt = Runtime.getRuntime();
					Process proc = rt.exec("rm "+wifiName.replaceAll("\\s", "")+".conf");
					proc.waitFor(10, TimeUnit.SECONDS);
					
					ProcessBuilder builder = new ProcessBuilder();
					builder.command(new String[] {"bash", "-c", "sudo wpa_passphrase \""+wifiName+"\" >> "+wifiName.replaceAll("\\s", "")+".conf "+textField.getText()+""});
					proc = builder.start();
					proc.waitFor(10, TimeUnit.SECONDS);
					
					proc = rt.exec("sudo wpa_supplicant -B -D wext -i wlan0 -c "+wifiName.replaceAll("\\s", "")+".conf");
					proc.waitFor(15, TimeUnit.SECONDS);
					TimeUnit.SECONDS.sleep(21);
					
					proc = rt.exec("iw wlan0 link");
					proc.waitFor(15, TimeUnit.SECONDS);
					
					BufferedReader stdInput = new BufferedReader(new 
					     InputStreamReader(proc.getInputStream()));

					BufferedReader stdError = new BufferedReader(new 
					     InputStreamReader(proc.getErrorStream()));

					String s = null;
					while ((s = stdInput.readLine()) != null) {
						
					    if (s.toString().contains("Connected to")) {
					    	EventQueue.invokeLater( () -> {
					    		messageToUser.setText("Successfully!");
					    		messageToUser.setVisible(true);
					    	} );
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
					    }
					    else if (s.toString().contains("Not connected.")){
					    	textField.setText("");
					    	messageToUser.setForeground(Color.red);
					    	messageToUser.setText("Wrong password!");
					    	messageToUser.setVisible(true);
					    }
					}

					while ((s = stdError.readLine()) != null) {
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}