package engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public final class InputKeyListener implements KeyListener {

    private final HashSet<Integer> keysDown = new HashSet<>();
    private StringBuilder sb = new StringBuilder();
    private boolean recordTyping = false;

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
        if(this.recordTyping){
            if((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE)  && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
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

        if (e.getKeyChar() >= 32 && e.getKeyChar() <= 126) {
            this.sb.append(e.getKeyChar());
        }
    }

    public boolean isTyping() {
        return this.recordTyping;
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

    public void resetPressedKeys() {
        this.keysDown.clear();
    }
}
