package engine.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class InputMouseListener implements MouseListener {

    private boolean leftButtonDown = false;
    private boolean rightButtonDown = false;
    private int mouseX = 0;
    private int mouseY = 0;

    public InputMouseListener() {
    }

    public int getX() {
        return mouseX;
    }

    public int getY() {
        return mouseY;
    }

    public boolean isLeftButtonDown() {
        return leftButtonDown;
    }

    public boolean isRightButtonDown() {
        return rightButtonDown;
    }

    private void updatePosition(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.updatePosition(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
            leftButtonDown = true;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightButtonDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.updatePosition(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
            leftButtonDown = false;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            rightButtonDown = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.updatePosition(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.updatePosition(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.updatePosition(e);
    }

}
