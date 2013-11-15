package ch.ethz.syslab.telesto.console.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ch.ethz.syslab.telesto.server.db.Database;

public class ConsoleWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private List<Panel> panels = new ArrayList<>(3);
    Database database = new Database();

    public ConsoleWindow() {
        // Main window
        setTitle("Telesto Management Console");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tabs
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Clients", createPanel("clients"));
        tabs.addTab("Queues", createPanel("queues"));
        tabs.addTab("Messages", createPanel("messages"));

        mainPanel.add(tabs, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new RefreshActionListener());
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);

        database.initialize();
        refresh();
    }

    private Panel createPanel(String table) {
        Panel panel = new Panel(table);
        panels.add(panel);
        return panel;
    }

    private void refresh() {
        for (Panel panel : panels) {
            try {
                panel.refresh(database);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refresh();
        }
    }
}
