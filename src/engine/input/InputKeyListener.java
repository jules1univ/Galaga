package engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public final class InputKeyListener implements KeyListener {
    private HashSet<Integer> keysDown = new HashSet<>();
    private StringBuilder sb = new StringBuilder();
    private boolean recordTyping = false;

    public InputKeyListener() {
    }

    public boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }

    public boolean isKeyPressed(int key) {
        if (keysDown.contains(key)) {
            keysDown.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysDown.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!this.recordTyping) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
            this.sb.deleteCharAt(this.sb.length() - 1);
            return;
        }
        if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
            this.sb.append(e.getKeyChar());
        }
    }

    public void startTyping() {
        if (this.recordTyping) {
            return;
        }
        this.sb = new StringBuilder();
        this.recordTyping = true;
    }

    public void stopTyping() {
        this.recordTyping = false;
    }

    public String getTypedText() {
        return this.sb.toString();
    }
}
