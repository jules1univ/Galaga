package engine.elements.ui.code;

import engine.Application;
import engine.elements.ui.code.cursor.TextPosition;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Optional;

public class InputManager {

    private final CodeContext context;

    public InputManager(CodeEditor codeInput) {
        this.context = codeInput.getContext();
    }

    public void update() {
        handleSelection();
        handleUpDown();
        handleLeftRight();
        handleTextEnter();
        handleTextDelete();
        handleTextNewLine();
        handleUndoHistory();
        handleRedoHistory();
        handleCopy();
        handleCut();
        handlePaste();
    }

    private void handleSelection() {
        if (Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            if (!this.context.selection().isActive()) {
                this.context.selection().enable(this.context.cursor().getPosition());
                return;
            }
        }
    }

    private void handleSelectionUpdate() {
        if (this.context.selection().isActive()) {
            if (!Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
                this.context.selection().reset();
            }
            this.context.view().markDirty();
        }
    }

    private void handleUpDown() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.context.cursor().moveLine(-1);
            this.handleSelectionUpdate();
            return;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.context.cursor().moveLine(1);
            this.handleSelectionUpdate();
            return;
        }
    }

    private void handleLeftRight() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_LEFT)) {
            this.context.cursor().moveColumn(-1);
            if (this.context.cursor().getPosition().column() < 0) {
                this.context.cursor().moveLine(-1);
                this.context.cursor().endColumn();
            }
            this.handleSelectionUpdate();
            return;
        } else if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_RIGHT)) {
            this.context.cursor().moveColumn(1);
            String line = this.context.text().getCursor();
            if (this.context.cursor().getPosition().column() > line.length()) {
                int newLine = Math.min(this.context.lines().size() - 1, this.context.cursor().getPosition().line() + 1);
                this.context.cursor().setPosition(new TextPosition(newLine, 0));
            }
            this.handleSelectionUpdate();
            return;
        }
    }

    private void handleTextEnter() {
        if (!Application.getContext().getInput().isTyping()) {
            return;
        }

        char ch = Application.getContext().getInput().getTypedChar();
        if (this.context.selection().isActive()) {
            replaceSelection(String.valueOf(ch));
            this.context.selection().reset();
        } else {
            this.context.text().insert(this.context.cursor().getPosition(), ch);
            this.context.cursor().moveColumn(1);
        }

        this.context.view().markDirty();
    }

    private void handleTextDelete() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE)) {
            return;
        }

        if (this.context.selection().isActive()) {
            replaceSelection("");
            this.context.selection().reset();
            this.context.view().markDirty();
            return;
        }

        String line = this.context.text().getCursor();
        if (this.context.cursor().getPosition().column() == 0 && line.isEmpty() && this.context.lines().size() > 1) {
            this.context.text().delete(this.context.cursor().getPosition().line());
            this.context.cursor().moveLine(-1);
            this.context.cursor().startColumn();
        } else if (this.context.cursor().getPosition().column() == 0
                && this.context.cursor().getPosition().line() > 0) {

            String previousLine = this.context.text().getCursor(-1);
            this.context.text().merge(this.context.cursor().getPosition().line(), -1);

            this.context.cursor().setPosition(new TextPosition(
                    this.context.cursor().getPosition().line() - 1,
                    previousLine.length()));

        } else if (this.context.cursor().getPosition().column() > 0) {
            this.context.text().delete(new TextPosition(
                    this.context.cursor().getPosition().line(),
                    this.context.cursor().getPosition().column() - 1));
            this.context.cursor().moveColumn(-1);
        }

        this.context.view().markDirty();
    }

    private void handleTextNewLine() {
        if (!Application.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            return;
        }

        this.context.text().split(this.context.cursor().getPosition());
        this.context.cursor().moveLine(-1);
        this.context.cursor().startColumn();
        this.context.view().markDirty();
    }

    private void handleCopy() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_C)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.context.selection().text();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }
        }
    }

    private void handleCut() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_X)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            String selectedText = this.context.selection().text();
            if (!selectedText.isEmpty()) {
                Application.getContext().getClipboard().set(selectedText);
            }

            if (this.context.selection().isActive()) {
                this.replaceSelection("");
                this.context.selection().reset();
                this.context.view().markDirty();
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

            String[] pasteLines = text.split("\n");
            int startColumn = this.context.cursor().getPosition().column();

            this.context.text().insert(
                    this.context.cursor().getPosition(),
                    List.of(pasteLines));

            this.context.cursor().setPosition(new TextPosition(
                    this.context.cursor().getPosition().line() + pasteLines.length - 1,
                    pasteLines.length == 1
                            ? startColumn + pasteLines[0].length()
                            : pasteLines[pasteLines.length - 1].length()));

            this.context.view().markDirty();
        }
    }

    private void handleUndoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Z)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            Optional<String> previousText = this.context.history().undo();
            if (previousText.isPresent()) {
                this.context.view().markDirty();
                return;
            }
        }
    }

    public void handleRedoHistory() {
        if (Application.getContext().getInput().isKeyPressed(KeyEvent.VK_Y)
                && Application.getContext().getInput().isKeyDown(KeyEvent.VK_CONTROL)) {
            Optional<String> nextText = this.context.history().redo();
            if (nextText.isPresent()) {
                this.context.view().markDirty();
                return;
            }
        }
    }

    private void replaceSelection(String replacement) {
        TextPosition selectionStart = this.context.selection().start().copy();
        this.context.selection().replace(replacement);

        this.context.cursor().setPosition(selectionStart);
        this.context.selection().reset();

        this.context.view().markDirty();
    }

}