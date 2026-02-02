package engine.elements.ui.code;

import java.awt.event.KeyEvent;

import engine.Application;
import engine.elements.ui.UIElement;
import engine.graphics.Renderer;

public class CodeInput extends UIElement {

    private final CodeState state;

    public CodeInput(CodeState state) {
        this.state = state;
    }

    private void handleSelection() {
        if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            if (!this.state.getSelection().isActive()) {
                this.state.getSelection().enable();
                return;
            }
        }
    }

    private void handleSelectionUpdate() {
        if (this.state.getSelection().isActive()) {
            if (!Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
                this.state.getSelection().disable();
            }
            this.state.getView().markDirty();
        }
    }

    private void handleUpDown() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.state.getCursor().moveLine(-1);
            this.handleSelectionUpdate();
            return;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.state.getCursor().moveLine(1);
            this.handleSelectionUpdate();
            return;
        }
    }

    private void handleLeftRight() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.state.getCursor().moveColumn(-1);
            this.handleSelectionUpdate();
            return;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.state.getCursor().moveColumn(1);
            this.handleSelectionUpdate();
            return;
        }
    }

    private void handleTextEnter() {
        if (!Application.getContext().getInput().isTyping()) {
            return;
        }

        char ch = Application.getContext().getInput().getTypedChar();
        if (this.state.getSelection().isActive()) {
            this.state.getSelection().replaceText(String.valueOf(ch));
            this.state.getSelection().disable();
        } else {
            this.state.getText().insert(ch);
        }

        this.state.getView().markDirty();
    }

    private void handleTextDelete() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE)) {
            return;
        }

        if (this.state.getSelection().isActive()) {
            this.state.getSelection().replaceText("");
            this.state.getSelection().disable();
        } else {
            this.state.getText().delete();
        }

        this.state.getView().markDirty();

    }

    private void handleTextNewLine() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            return;
        }

        this.state.getText().insert('\n');
        this.state.getView().markDirty();
    }

    public void handleTextTab() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_TAB)) {
            return;
        }

        this.state.getText().insert('\t');
        this.state.getView().markDirty();
    }

    private void handleUndoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Z)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.state.getHistory().undo();
            this.state.getView().markDirty();
            return;
        }
    }

    private void handleRedoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Y)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.state.getHistory().redo();
            this.state.getView().markDirty();
            return;
        }
    }

    private void handleCopy() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_C)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.state.getSelection().getText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }
        }
    }

    private void handleCut() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_X)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.state.getSelection().getText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }

            if (this.state.getSelection().isActive()) {
                this.state.getSelection().replaceText("");
                this.state.getSelection().disable();
                this.state.getView().markDirty();
            }
        }
    }

    private void handlePaste() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_V)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String text = Application.getContext().getClipboard().get();
            if (text == null || text.isEmpty()) {
                return;
            }

            this.state.getText().insert(text,
                    this.state.getCursor().getTextPosition(),
                    this.state.getCursor().getTextPosition());

            this.state.getView().markDirty();
        }
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(float dt) {
        handleSelection();

        handleUpDown();
        handleLeftRight();

        handleTextEnter();
        handleTextDelete();
        handleTextNewLine();
        handleTextTab();   

        handleUndoHistory();
        handleRedoHistory();

        handleCopy();
        handleCut();
        handlePaste();
    }

    @Override
    public void draw(Renderer renderer) {
        throw new UnsupportedOperationException("CodeInput.draw should not be called");
    }

}
