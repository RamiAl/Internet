import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

class HomeWindow extends JFrame{
	private JButton wifi;
	private JButton ip;
	private JButton dynamic;
	private JButton statik;
	private JButton cancel;
	
	public HomeWindow() {
		super ("Network");
		getContentPane().setLayout(null);
		
		wifi = new JButton("Select Wi-Fi");
		wifi.setBounds(316, 217, 164, 71);
		getContentPane().add(wifi);
		
		ip = new JButton("Set IP");
		ip.setBounds(316, 118, 164, 71);
		ip.setPreferredSize(new Dimension(100, 40));
		getContentPane().add(ip);
		
		cancel = new JButton("Cancel");
		cancel.setBounds(159, 356, 89, 35);
		getContentPane().add(cancel);
		
		TheHandler handler = new TheHandler();
		wifi.addActionListener(handler);
		ip.addActionListener(handler);
		cancel.addActionListener(handler);
	}
	
	class TheHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == ip) {
				IpSetOptions ipOptions = new IpSetOptions();
				ipOptions.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				ipOptions.setSize(800, 480);
				ipOptions.setVisible(true);
			}
			else if (event.getSource() == wifi){
				WifiList wifiList = new WifiList();
				wifiList.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				wifiList.setSize(800, 480);
				wifiList.setVisible(true);
				
			}else {
				System.exit(0);
			}
		}
	}
}