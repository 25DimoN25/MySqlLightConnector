import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
class CustomTableModel extends DefaultTableModel {
	
	//********************************************fields***********************************
	
	private Object lastEditedItem, preLastEditedItem;
	private int lastEditedRow, lastEditedColumn;

	String 
		table, 
		keys[],
		titles[],
		data[][];
	
	private Statement statement;
	
	boolean editable = false;
	
	//********************************************constructors*****************************
	
	public CustomTableModel(boolean editable) {
		this.editable = editable;
	}
	
	public CustomTableModel(String sql, Statement statement) throws SQLException {
		this.statement = statement;
		editable = false;
		fillTableSQL(sql);
	}

	public CustomTableModel(Statement statement, String table) throws SQLException {
		this.statement = statement;
		this.table = table;
		fillTableSQL(); 
		
		
		addTableModelListener(new TableModelListener() {
			@Override	
			public void tableChanged(TableModelEvent e) {
				if (e.getColumn() != getColumnCount()-1) // <-- ************************deleting part
						if (e.getType() == TableModelEvent.UPDATE && e.getColumn() != -1 ) {
							try {
								String idValue = getValueAt( lastEditedRow, findColumn(keys[0]) ).equals(lastEditedItem) ?
										preLastEditedItem.toString() : getValueAt(lastEditedRow, findColumn(keys[0])).toString();
								
								statement.execute(
										"UPDATE `"
												+table+
									   "` SET "
												+getColumnName(e.getColumn())+"='"+lastEditedItem+
									   "' WHERE "
												+keys[0] +"='"+idValue+"'" 
								);
				
							} catch (Exception exception) {
								JOptionPane.showMessageDialog(null, exception instanceof ArrayIndexOutOfBoundsException?"Can't edit it":exception.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
								undo();
							}	
						}
			}
		});
	}
		
	
	//********************************************overrided********************************
		
	// deleting part
	@Override
	public Class<? extends Object> getColumnClass(int column){
		try {
			return  getValueAt(0, column).getClass();
		} catch (Exception e) {
			return super.getColumnClass(column);
		}
	}//*************
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return editable; 
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
			lastEditedItem = aValue;
			lastEditedRow = row;
			lastEditedColumn = column;
			preLastEditedItem = getValueAt(row, column);		
			super.setValueAt(aValue, row, column);
    }
	
	
	//***************************************setters and getters**************************
		
	void setStatement(Statement statement) {
		this.statement = statement;
	}
	
	
	private String[] getKeys() {
		try {
			ResultSet primary = statement.executeQuery("SHOW COLUMNS FROM `"+table+"` WHERE `key`='PRI' OR `key`='UNI'");
			
			primary.last();
			String[] keys = new String[primary.getRow()]; 
			primary.beforeFirst();	
			
			int i = 0;
			while (primary.next() ){
				keys[i] = primary.getString(1);
			}
			
			return keys;
			
		} catch (Exception e) {
			return null;
		}
	}
	
	
	//***************************************other methods********************************
	
	
	private void testEditable(){
		keys = getKeys();
		if (keys.length > 0) {
			editable = true;
		} else {
			editable = false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void undo(){
		( (java.util.Vector<Object>) getDataVector().elementAt(lastEditedRow) )
				.setElementAt(preLastEditedItem, lastEditedColumn);
	}
	

	void fillTable(String[][] data, String[] titles) {
		setColumnCount(0);
		setRowCount(0);
		
		for (int i = 0; i < titles.length; i++)
			addColumn(titles[i]);
		for (int i = 0; i < data.length; i++)
			addRow(data[i]);
		

	}
	
	
	void fillTableSQL() throws SQLException{	
			testEditable();
			
			ResultSet res = statement.executeQuery("SELECT * FROM `"+table+"`");
		
			int totalColumns = res.getMetaData().getColumnCount();
			
			res.last();
			int totalRows = res.getRow(); 
			res.beforeFirst();		
			
			titles = new String[totalColumns];
			data = new String[totalRows][totalColumns];
			
			for (int i = 0; i < totalColumns; i++) 
				titles[i]=res.getMetaData().getColumnLabel(i+1); 
				
			int currentRow = 0;
			while (res.next()) {
				for (int i = 0; i < totalColumns; i++) {
					data[currentRow][i]=res.getString(i+1);
				}
				currentRow++;
			}
			
			fillTable(data, titles);
			
	}
	
	
	void fillTableSQL(String sql) throws SQLException {		
			ResultSet res = statement.executeQuery(sql);
			
			int totalColumns = res.getMetaData().getColumnCount();
			
			res.last();
			int totalRows = res.getRow(); 
			res.beforeFirst();		
			
			titles = new String[totalColumns];
			data = new String[totalRows][totalColumns];
			
			for (int i = 0; i < totalColumns; i++) 
				titles[i]=res.getMetaData().getColumnLabel(i+1); 
			
			int currentRow = 0;
			while (res.next()) {
				for (int i = 0; i < totalColumns; i++) {
					data[currentRow][i]=res.getString(i+1);
				}
				currentRow++;
			}
						
			fillTable(data, titles);
			
	}
	
}


@SuppressWarnings("serial")
public class CustomTable extends JTable {
	private CustomTableModel model;
	
	public CustomTable(boolean editable) {
		model = new CustomTableModel(editable);
		settings();
	}
	
	public CustomTable(Statement statement, String table) throws SQLException {
		model = new CustomTableModel(statement, table);
		settings();
		setCheckBoxesIfItPossible();
	}
	

	public CustomTable(String sql, Statement statement) throws SQLException {
		model = new CustomTableModel(sql, statement);
		settings();
	}
	
	// deleting part
	void setCheckBoxesIfItPossible() {
		if (model.editable == true) {
			Boolean[] check = new Boolean[model.getRowCount()];
			for (int i = 0; i < check.length; i++) {
				check[i] = false;
			}
			model.addColumn("x", check);
			getColumn("x").setMaxWidth(20);
			getColumn("x").setMinWidth(20);
			moveColumn(model.getColumnCount()-1, 0);
		}
	}//***************
	

	void deleteSelected(Statement statement) throws SQLException {
		if (model.editable == true) {
			for (int i = 0; i < getRowCount(); i++) {
				if ( (boolean) getValueAt(i, 0) == true ) {
					Main.statement.execute("DELETE FROM `"+model.table+"` WHERE "+model.keys[0]+"='"+(model.getValueAt( i, model.findColumn(model.keys[0]))+"'" ));
				}
				
			}
		}
	}
	
	
	CustomTableModel getCustomTableModel(){
		return model;
	}
		
	private void settings() {
		setAutoResizeMode(AUTO_RESIZE_OFF);
		setCellSelectionEnabled(false);
		setShowGrid(true);
		setModel(model);
		getTableHeader().setReorderingAllowed(false);
	}
	
}


