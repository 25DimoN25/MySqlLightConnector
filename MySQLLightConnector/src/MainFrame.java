
import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class MainFrame {
 
	static JTabbedPane tabbledPane = new JTabbedPane(){
		{
			setFont(getFont().deriveFont(9f));
		}
	};
	
	static JFrame mainFrame = new JFrame("Database") {
		{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			setSize(300, 300);
			setLocationRelativeTo(null);

			setJMenuBar(new JMenuBar() {
				{ 
					add(new JMenu("Menu") {
						{
							setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/OrderedListNumbers_16x16_JFX.png")));
							
							add(new JMenuItem("Make request"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/Paste_16x16_JFX.png")));
								}
							}).addActionListener(new Request());
							
							addSeparator();
							
							add(new JMenuItem("Add row"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/IncreaseIndent_16x16_JFX.png")));
								}
							}).addActionListener(new AddRowKey());;
							
							add(new JMenuItem("Delete selected"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/DecreaseIndent_16x16_JFX.png")));
								}
								
							}).addActionListener(new DeleteKey());
							
							addSeparator();
							
							add(new JMenuItem("Show fields info"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/com/sun/deploy/uitoolkit/impl/fx/ui/resources/image/graybox_error.png")));		
								}
							}).addActionListener(new ShowFieldsInfo());
							
							
							add(new JMenuItem("Show current db info"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/com/sun/deploy/uitoolkit/impl/fx/ui/resources/image/graybox_error.png")));
								}
							}).addActionListener(new ShowDatabaseInfo());
							
							addSeparator();
							
							add(new JMenuItem("Select database"){
								{
									setIcon(new ImageIcon(Frame.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));
								}
							}).addActionListener(new SelectDB());
						}
					});
					add(new JButton("Refresh") {
						{
							setFocusable(false);
							setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
							addActionListener(new Refresh());
						}
					});
				}
			});

			
			add(tabbledPane);
				
		}
	};
	
	static void showWindow() {
		mainFrame.setVisible(true);
	}

	static void hideWindow() {
		mainFrame.setVisible(false);
	}

	

	
}
