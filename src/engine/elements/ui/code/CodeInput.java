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

    private boolean handleSelection() {
        if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            if (!this.state.getSelection().isActive()) {
                this.state.getSelection().enable();
                return true;
            }
        }
        return false;
    }

    public boolean handleSelectionAll() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_A)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.state.getSelection().all();
            return true;
        }
        return false;
    }

    private boolean handleCursorMove() {
        if (this.state.getSelection().isActive()) {
            if (!Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
                this.state.getSelection().disable();
            }
        }
        return true;
    }

    private boolean handleUpDown() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.state.getCursor().moveLine(-1);
            return this.handleCursorMove();
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.state.getCursor().moveLine(1);
            return this.handleCursorMove();
        }
        return false;
    }

    private boolean handleLeftRight() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.state.getCursor().moveColumn(-1);
            return this.handleCursorMove();
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.state.getCursor().moveColumn(1);
            return this.handleCursorMove();
        }
        return false;
    }

    private void insertOrReplaceText(Character ch) {
        int newIndex = -1;
        if (this.state.getSelection().isActive()) {
            if (ch == null) {
                newIndex = this.state.getSelection().replaceText("");
            } else {
                newIndex = this.state.getSelection().replaceText(String.valueOf(ch));
            }
            this.state.getSelection().disable();
        } else {
            if (ch == null) {
                newIndex = this.state.getText().delete();
            } else {
                newIndex = this.state.getText().insert(ch);
            }
        }
        this.state.getCursor().setTextIndex(newIndex);
    }

    private boolean handleTextTab() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_T)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.insertOrReplaceText(' ');
            this.insertOrReplaceText(' ');
            return true;
        }
        return false;

    }

    private boolean handleTextEnter() {
        if (!Application.getContext().getInput().isTyping()) {
            return false;
        }

        char ch = Application.getContext().getInput().getTypedChar();
        this.insertOrReplaceText(ch);
        return true;
    }

    private boolean handleTextDelete() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE)) {
            return false;
        }
        this.insertOrReplaceText(null);
        return true;

    }

    private boolean handleTextNewLine() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            return false;
        }

        this.insertOrReplaceText('\n');
        return true;
    }

    private boolean handleUndoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Z)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.state.getHistory().undo();
            return true;
        }
        return false;
    }

    private boolean handleRedoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Y)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            this.state.getHistory().redo();
            return true;
        }
        return false;
    }

    private boolean handleCopy() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_C)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.state.getSelection().getText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }
            return true;
        }
        return false;
    }

    private boolean handleCut() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_X)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.state.getSelection().getText();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }

            if (this.state.getSelection().isActive()) {
                this.state.getSelection().replaceText("");
                this.state.getSelection().disable();
            }

            return true;
        }
        return false;
    }

    private boolean handlePaste() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_V)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String text = Application.getContext().getClipboard().get();
            if (text == null || text.isEmpty()) {
                return false;
            }

            this.state.getCursor().setTextIndex(
                    this.state.getText().insert(text,
                            this.state.getCursor().getTextPosition(),
                            this.state.getCursor().getTextPosition()));

            return true;
        }
        return false;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void update(float dt) {
        boolean updated = handleSelection() ||
                handleSelectionAll() ||
                handleUpDown() ||
                handleLeftRight() ||
                handleTextTab() ||
                handleTextEnter() ||
                handleTextDelete() ||
                handleTextNewLine() ||
                handleUndoHistory() ||
                handleRedoHistory() ||
                handleCopy() ||
                handleCut() ||
                handlePaste();

        if (updated) {
            this.state.getView().markDirty();
        }
    }

    @Override
    public void draw(Renderer renderer) {
        throw new UnsupportedOperationException("CodeInput.draw should not be called");
    }

}
