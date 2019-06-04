import javax.swing.JFrame;

class Main{
	public static void main (String [] args) {
		HomeWindow home = new HomeWindow();
		home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		home.setSize(800, 480);
		home.setVisible(true);
	}
}