package ch.ethz.syslab.telesto.console;

import javax.swing.SwingUtilities;

import ch.ethz.syslab.telesto.console.gui.ConsoleWindow;

public class Main implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Main());
    }

    @Override
    public void run() {
        ConsoleWindow window = new ConsoleWindow();
        window.setVisible(true);
    }
}
