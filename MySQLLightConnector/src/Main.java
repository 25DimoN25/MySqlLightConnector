import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;


@SuppressWarnings("serial")
public class Main {
	static Connection connection;
	static Statement statement;
	static String 
		lastShowingRequest,
		lastMakedRequest;
	static CustomTable[] tables;
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ConnectFrame.showWindow();

		new Timer().schedule(new TimerTask() {
			public void run() {
				System.gc();	
			}
		}, 0, 1000*30);
	}
	
	
	static JDialog dialog = new JDialog(MainFrame.mainFrame, true){
		{
			setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		}
	};
	
	static CustomTable requestTable = new CustomTable(false);
	static JScrollPane scrollPane1 = new JScrollPane(requestTable);
	
	static CustomTable addDialogTable = new CustomTable(true);
	static JScrollPane scrollPane2 = new JScrollPane(addDialogTable);
	
	static JButton addRowButton = new JButton("+");
	static JButton delRowButton = new JButton("-");
	static JButton okRowButton  = new JButton("Add rows");
	static JPanel  buttonGroup  = new JPanel(){
		{
			((JButton) add(addRowButton)).addActionListener(event -> {
				addDialogTable.getCustomTableModel().addRow(data[0]);
			});
			((JButton) add(delRowButton)).addActionListener(event -> {
				if (addDialogTable.getCustomTableModel().getRowCount() > 1)
					addDialogTable.getCustomTableModel().setRowCount(addDialogTable.getCustomTableModel().getRowCount()-1);
			});
			((JButton) add(okRowButton)).addActionListener(event -> {
				for (int j = 0; j < addDialogTable.getCustomTableModel().getRowCount(); j++) {
					String request = "INSERT INTO `"+tables[MainFrame.tabbledPane.getSelectedIndex()].getCustomTableModel().table+"` VALUES (";
					for (int k = 0; k < addDialogTable.getCustomTableModel().getColumnCount(); k++) {
						request += "'"+addDialogTable.getValueAt(j, k)+"', ";
					}
					request = request.substring(0, request.lastIndexOf(','))+")";
					try {
						statement.execute(request);
						dialog.setVisible(false);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(MainFrame.mainFrame, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	};
	
	static void showRequestDialog(String sql) throws SQLException {
		requestTable.getCustomTableModel().setStatement(statement);
		requestTable.getCustomTableModel().fillTableSQL(sql);
		
		requestTable.setPreferredScrollableViewportSize(new Dimension(
				requestTable.getColumnModel().getTotalColumnWidth(),
				requestTable.getRowHeight() * 10
				));
		
		dialog.getContentPane().removeAll();
		dialog.getContentPane().add(scrollPane1);
		dialog.pack();
		dialog.setLocationRelativeTo(MainFrame.mainFrame);
		dialog.setVisible(true);
	}

	static String[][] data;
	static void showAddRowDialog() throws SQLException {
		ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM `"+tables[MainFrame.tabbledPane.getSelectedIndex()].getCustomTableModel().table+"`");
		rs.last();
		String[] titles = new String[rs.getRow()];
		data = new String[1][rs.getRow()];
		rs.beforeFirst();
		
		int i = 0;
		while (rs.next()) {
			data[0][i] = rs.getString(5);
			titles[i++] = (rs.getString(4)!=null&&rs.getString(4).length()>0?"*":"")+rs.getString(1)+" [ "+rs.getString(2)+" ]";
		}

		addDialogTable.getCustomTableModel().fillTable(data, titles);
		addDialogTable.setPreferredScrollableViewportSize(new Dimension(
				addDialogTable.getColumnModel().getTotalColumnWidth(),
				addDialogTable.getRowHeight() * 10
				));
		
		dialog.getContentPane().removeAll();
		dialog.getContentPane().add(scrollPane2);
		dialog.getContentPane().add(buttonGroup);
		dialog.pack();
		dialog.setLocationRelativeTo(MainFrame.mainFrame);
		dialog.setVisible(true);

	}
	
	
	static void selectDB() {
		try {
			ResultSet rs = connection.getMetaData().getCatalogs();
			
			rs.last(); 
			String[] dbs = new String[rs.getRow()];
			rs.beforeFirst();
			
			int i = 0;
			while (rs.next()) 
				dbs[i++] = rs.getString(1);
			
			String temp;
			if ((temp = (String) JOptionPane.showInputDialog(MainFrame.mainFrame, "Select Database", "Databases", JOptionPane.INFORMATION_MESSAGE, null, dbs, dbs[0])) != null) {
				connection.setCatalog(temp);
				
				statement = connection.createStatement();
				MainFrame.mainFrame.setTitle("Database: "+temp);
				
				showTableTabs();
			}		
			
		} catch (Exception e) {	
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}	
	}


	private static String[] getTables(Statement statement) {
		try {
			ResultSet tables = statement.executeQuery("SHOW TABLES");
			
			tables.last();
			String[] dbTables = new String[tables.getRow()];
			tables.beforeFirst();
			
			int i = 0;
			while (tables.next()) {		
				dbTables[i++] = tables.getString(1);
			}
			return dbTables;
		} catch (Exception e) {
			return null;
		}
			
	}
	
	
	static void showTableTabs() {
		if (tables != null && tables.length > 0) {
			MainFrame.tabbledPane.removeAll();
			tables = null;
		}
		
		String[] table = getTables(statement);
		tables = new CustomTable[table.length];

		for (int i = 0; i < table.length; i++) {
			try {
				tables[i] = new CustomTable(statement, table[i]);
				MainFrame.tabbledPane.addTab( table[i], new JScrollPane(tables[i]));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
			
	}
	
}