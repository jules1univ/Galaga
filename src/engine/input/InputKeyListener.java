package engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public final class InputKeyListener implements KeyListener {
    public static final char NO_CHAR = '\0';

    private final HashSet<Integer> keysDown = new HashSet<>();
    private char current = NO_CHAR;

    public InputKeyListener() {
    }

    public boolean isKeyDown(int key) {
        return keysDown.contains(key);
    }

    public boolean isKeyPressed(int... keys) {
        for (int key : keys) {
            if (keysDown.contains(key)) {
                keysDown.remove(key);
                return true;
            }
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
        if (e.getKeyChar() >= 32 && e.getKeyChar() <= 126) {
            this.current = e.getKeyChar();
        }
    }

    public char getTypedChar() {
        char temp = this.current;
        this.current = NO_CHAR;
        return temp;
    }

    public void resetPressedKeys() {
        this.keysDown.clear();
    }

    public boolean isTyping() {
        return this.current != NO_CHAR;
    }
}
