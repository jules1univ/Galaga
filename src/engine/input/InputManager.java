package engine.input;

import engine.AppPanel;

public final class InputManager {
    private InputKeyListener key = new InputKeyListener();
    private InputMouseListener mouse = new InputMouseListener();

    public InputManager() {

    }

    public void attachListeners(AppPanel panel) {
        panel.addKeyListener(key);
        panel.addMouseListener(mouse);
        // panel.addMouseMotionListener(mouse);
    }

    public boolean isKeyDown(int keyId) {
        return key.isKeyDown(keyId);
    }

    public boolean isMouseLeftDown() {
        return mouse.isLeftButtonDown();
    }

    public boolean isMouseRightDown() {
        return mouse.isRightButtonDown();
    }

    public int getMouseX() {
        return mouse.getX();
    }

    public int getMouseY() {
        return mouse.getY();
    }
}
