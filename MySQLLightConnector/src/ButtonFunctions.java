import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ButtonFunctions {}



class Connect implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.connection = DriverManager.getConnection("jdbc:"+
					ConnectFrame.textAdress.getText(),
					ConnectFrame.textUsername.getText(),
					ConnectFrame.textPassword.getText());

			Main.statement = Main.connection.createStatement();
			
			ConnectFrame.hideWindow();
			MainFrame.showWindow();
			Main.selectDB();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.mainFrame, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}


class Refresh implements ActionListener {
	static void doRefresh() {
		if (MainFrame.tabbledPane.getSelectedIndex() >= 0) {
			try {
				Main.tables[MainFrame.tabbledPane.getSelectedIndex()].getCustomTableModel().fillTableSQL();
				Main.tables[MainFrame.tabbledPane.getSelectedIndex()].setCheckBoxesIfItPossible();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else JOptionPane.showMessageDialog(MainFrame.mainFrame, "Nothing for refresh", "Error", JOptionPane.ERROR_MESSAGE);
	}
	public void actionPerformed(ActionEvent arg0) {
		doRefresh();
	}
}


class Request implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		String temp;
		if ( (temp = (String) JOptionPane.showInputDialog(MainFrame.mainFrame, "Enter SQL request:", "Request", JOptionPane.INFORMATION_MESSAGE, null, null, Main.lastMakedRequest)) != null ) {
			try {
				if (temp.toLowerCase().contains("select") || temp.toLowerCase().contains("show")) {
					Main.showRequestDialog(temp);
				} else
					Main.statement.execute(temp);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(MainFrame.mainFrame, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			Main.lastMakedRequest = temp;
		}
	}
}


class SelectDB implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		Main.selectDB();
	}
}


class ShowDatabaseInfo implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.showRequestDialog("SHOW TABLE STATUS");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.mainFrame, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}


class ShowFieldsInfo implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.showRequestDialog("SHOW COLUMNS FROM `"+Main.tables[MainFrame.tabbledPane.getSelectedIndex()].getCustomTableModel().table+"`");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(MainFrame.mainFrame, "No tables", "Error", JOptionPane.ERROR_MESSAGE);
		}		
	}
}


class DeleteKey implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.tables[MainFrame.tabbledPane.getSelectedIndex()].deleteSelected(Main.statement);
			Refresh.doRefresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}


class AddRowKey implements ActionListener {
	public void actionPerformed(ActionEvent arg0) {
		try {
			Main.showAddRowDialog();
			Refresh.doRefresh();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(MainFrame.mainFrame, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}





