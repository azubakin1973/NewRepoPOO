package main;

import javax.swing.SwingUtilities;
import view.EmployeFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                EmployeFrame frame = new EmployeFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro detalhado: " + e.getMessage());
            }
        });
    }
}