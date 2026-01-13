package engine;

import engine.graphics.Renderer;
import engine.input.InputKeyListener;
import engine.utils.ClipboardManager;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class AppFrame extends JFrame {
    private final AppCanvas canvas;
    private final InputKeyListener input;
    private final ClipboardManager clipboard;

    public AppFrame(Application<?> app) {
        super(app.getTitle());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(app.getWidth(), app.getHeight());
        this.setPreferredSize(new Dimension(app.getWidth(), app.getHeight()));
        this.setResizable(false);

        this.canvas = new AppCanvas(app);
        this.add(this.canvas);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.requestFocusInWindow();

        this.input = new InputKeyListener();
        this.canvas.setFocusTraversalKeysEnabled(false);
        this.canvas.addKeyListener(this.input);
        this.canvas.requestFocus();

        this.clipboard = new ClipboardManager(this.getToolkit().getSystemClipboard());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.stop();
            }
        });
    }

    public InputKeyListener getInput() {
        return this.input;
    }

    public Renderer getRenderer() {
        return this.canvas.getRenderer();
    }

    public ClipboardManager getClipboard() {
        return this.clipboard;
    }

    public void showMessage(String title, String message, Runnable onClose) {
        JDialog dialog = new JDialog(this, title, true);
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION);

        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (onClose != null) {
                    onClose.run();
                }
            }
        });

        optionPane.addPropertyChangeListener(evt -> {
            if (dialog.isVisible()
                    && evt.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {

                dialog.dispose();
                if (onClose != null) {
                    onClose.run();
                }
            }
        });

        dialog.setVisible(true);
    }

    public void start() {
        this.canvas.start();
    }

    public void stop() {
        this.canvas.stop();
        this.dispose();
    }
}
