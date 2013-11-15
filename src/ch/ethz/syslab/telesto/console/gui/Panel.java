package ch.ethz.syslab.telesto.console.gui;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ch.ethz.syslab.telesto.server.db.Database;

public class Panel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable table;
    private String tableName;

    public Panel(String tableName) {
        super(new BorderLayout());
        this.tableName = tableName;
        table = new JTable();
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(getMaximumSize());
    }

    public void refresh(Database database) throws SQLException {
        Connection connection = database.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("SELECT * FROM " + tableName); // We don't have to worry about escaping here
        ResultSet results = statement.getResultSet();

        ResultSetMetaData meta = results.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = meta.getColumnName(i + 1);
        }

        DefaultTableModel model = new TableModel(new Object[0][], columnNames);
        table.setModel(model);

        while (results.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = results.getObject(i + 1);
            }
            model.addRow(row);
        }
    }

    private static class TableModel extends DefaultTableModel {
        public TableModel(Object[][] objects, String[] columnNames) {
            super(objects, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
