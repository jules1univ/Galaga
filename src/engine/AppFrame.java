package engine;

import javax.swing.JFrame;
import java.awt.Dimension;

public final class AppFrame extends JFrame {

    public AppFrame(Application app) {
        super(app.getTitle());

        this.setSize(app.getWidth(), app.getHeight());
        this.setTitle(app.getTitle());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setFocusTraversalKeysEnabled(false);
        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));
    }

    public void setPanel(AppPanel panel) {
        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
