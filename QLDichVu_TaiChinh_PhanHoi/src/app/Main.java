package app;

import app.view.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Điểm khởi chạy duy nhất của ứng dụng khách sạn. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}
