import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JLabel;

class WifiList extends JFrame{
	private JButton select;
	private JButton cancel;
	private JCheckBox chckbxNewCheckBox;
	private DefaultListModel model;
	private JList list_1;
	private JLabel messageToUser;
	private ConnectToWifi connectToWifi;
	private String wifiName;
	private Runtime rt;
	private ArrayList<String> secureWifi = new ArrayList<String>();
	
	public WifiList() {
		super ("Wi-Fi");
		getContentPane().setLayout(null);
		
		select = new JButton("Select");
		select.setBounds(634, 357, 89, 32);
		getContentPane().add(select);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(159, 356, 89, 35);
		getContentPane().add(cancel);
		
		chckbxNewCheckBox = new JCheckBox("Enable");
		chckbxNewCheckBox.setBounds(351, 37, 98, 51);
		getContentPane().add(chckbxNewCheckBox);
		
		model = new DefaultListModel();
	    JScrollPane s = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
	    								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		s.setBounds(297, 100, 238, 241);
	    getContentPane().add(s);
	    list_1 = new JList(model);
	    s.setViewportView(list_1);
	    
	    messageToUser = new JLabel("You have to choose Wi-Fi");
	    messageToUser.setBounds(438, 337, 190, 73);
	    getContentPane().add(messageToUser);
	    messageToUser.setVisible(false);		
		
		TheHandler handler = new TheHandler();
		select.addActionListener(handler);
		cancel.addActionListener(handler);
		chckbxNewCheckBox.addActionListener(handler);
	}
	
	class TheHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == select) {
				if (list_1.getSelectedValue() == null) {
					messageToUser.setForeground (Color.red);
					messageToUser.setVisible(true);
				}else {
			    	if (secureWifi.contains(list_1.getSelectedValue().toString())) {
						connectToWifi = new ConnectToWifi();
						connectToWifi.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
						connectToWifi.setSize(800, 480);
						connectToWifi.setVisible(true);
						messageToUser.setVisible(false);
						connectToWifi.setWifiName((String) list_1.getSelectedValue());
					}else {
						rt = Runtime.getRuntime();
						try {
							ProcessBuilder builder = new ProcessBuilder();
							builder.command(new String[] {"bash", "-c", "sudo iwconfig wlan0 essid \""+list_1.getSelectedValue()+"\""});
							Process proc = builder.start();
							proc.waitFor(10, TimeUnit.SECONDS);
							TimeUnit.SECONDS.sleep(4);
							
							proc = rt.exec("sudo iwconfig wlan0");
							proc.waitFor(10, TimeUnit.SECONDS);
							
							BufferedReader stdInput = new BufferedReader(new 
								     InputStreamReader(proc.getInputStream()));
	
							BufferedReader stdError = new BufferedReader(new 
								     InputStreamReader(proc.getErrorStream()));
		
							String s = null;
							while ((s = stdInput.readLine()) != null) {
								if (s.toString().contains(list_1.getSelectedValue().toString())) {
									EventQueue.invokeLater( () -> {
										messageToUser.setForeground(Color.BLACK);
										messageToUser.setText("Successfully!");
										messageToUser.setVisible(true);
									});
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
							    }else if (!s.toString().contains(list_1.getSelectedValue().toString())){
						    		messageToUser.setText("Somthing went wrong");
						    		messageToUser.setForeground(Color.RED);
						    		messageToUser.setVisible(true);
							    }
							}}
						 catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else if (event.getSource() == chckbxNewCheckBox) {
				if (chckbxNewCheckBox.isSelected()) {
					try {
						rt = Runtime.getRuntime();
						Process proc = rt.exec("sudo ifconfig wlan0 up");
						proc.waitFor(10, TimeUnit.SECONDS);
						proc = rt.exec("sudo iwlist wlan0 scan");
						proc.waitFor(10, TimeUnit.SECONDS);
						
						BufferedReader stdInput = new BufferedReader(new 
						     InputStreamReader(proc.getInputStream()));

						BufferedReader stdError = new BufferedReader(new 
						     InputStreamReader(proc.getErrorStream()));

						String s = null;
						while ((s = stdInput.readLine()) != null) {
						    if (s.toString().contains("Encryption key:on")) {
						    	s = stdInput.readLine();
						    	if (s.toString().contains("ESSID")){
							    	wifiName = s.toString().replaceFirst("\\s{1,}ESSID:\"", "");
							    	wifiName = wifiName.replaceAll("\"$", "");
							    	secureWifi.add(wifiName);
							    	model.addElement(wifiName);
						    	}
							}else if (s.toString().contains("Encryption key:off")){
								s = stdInput.readLine();
						    	if (s.toString().contains("ESSID")){
							    	wifiName = s.toString().replaceFirst("\\s{1,}ESSID:\"", "");
							    	wifiName = wifiName.replaceAll("\"$", "");
							    	model.addElement(wifiName);
						    	}
							}
						}while ((s = stdError.readLine()) != null) {
						    System.out.println(s);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}if (!chckbxNewCheckBox.isSelected()) {
					model.clear();
					list_1.removeAll();
					rt = Runtime.getRuntime();
					try {
						Process proc = rt.exec("sudo killall wpa_supplicant");
						proc.waitFor(10, TimeUnit.SECONDS);
						proc = rt.exec("sudo ifconfig wlan0 down");
						proc.waitFor(10, TimeUnit.SECONDS);
						TimeUnit.SECONDS.sleep(2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				dispose();
			}
		}
		
	}
}