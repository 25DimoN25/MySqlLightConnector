import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ConnectFrame {

	static JTextField textAdress, textUsername, textPassword;

	static JFrame frame = new JFrame("Settings") {
		{
			setLayout(null);
			setSize(440, 130);
			setLocationRelativeTo(null);

			setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			add(new JLabel("Server adress: ") {
				{
					setHorizontalAlignment(SwingConstants.RIGHT);
					setBounds(5, 15, 100, 10);
				}
			});

			add(new JLabel("Username: ") {
				{
					setHorizontalAlignment(SwingConstants.RIGHT);
					setBounds(5, 45, 100, 10);
				}
			});

			add(new JLabel("Password: ") {
				{
					setHorizontalAlignment(SwingConstants.RIGHT);
					setBounds(5, 75, 100, 10);
				}
			});

			add(textAdress   = new JTextField("mysql://localhost/"))
				.setBounds(105, 5, 200, 30);
			add(textUsername = new JTextField("root"))
				.setBounds(105, 35, 200, 30);
			add(textPassword = new JTextField("root"))
				.setBounds(105, 65, 200, 30);

			add(new JButton("Connect") {
				{
					addActionListener(new Connect());
					setBounds(310, 5, 100, 90);
				}
			});


		}
	};

	static void showWindow() {
		frame.setVisible(true);
	}

	static void hideWindow() {
		frame.setVisible(false);
	}

}
