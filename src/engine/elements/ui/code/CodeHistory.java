package engine.elements.ui.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CodeHistory {
    private final CodeState state;
    private final List<String> history = new ArrayList<>();
    private int index = -1;

    public CodeHistory(CodeState state) {
        this.state = state;
    }

    public void add(String content) {
        if (content == null) {
            return;
        }

        if (index != -1 && content.equals(history.get(index))) {
            return;
        }

        history.add(content);
        index++;

        if (this.history.size() > CodeState.HISTORY_MAX_SIZE) {
            for (int i = 0; i < CodeState.HISTORY_RANGE; i++) {
                history.remove(0);
                index--;
            }
        }
    }

    public void undo() {
        if (!canUndo()) {
            return;
        }

        index--;
        this.state.getText().setContent(history.get(index));
    }

    public void redo() {
        if (!canRedo()) {
            return;
        }
        index++;
        this.state.getText().setContent(history.get(index));
    }

    public boolean canUndo() {
        return index > 0;
    }

    public boolean canRedo() {
        return index < history.size() - 1;
    }

    public Optional<String> current() {
        return index >= 0 ? Optional.of(history.get(index)) : Optional.empty();
    }

    public void clear() {
        history.clear();
        index = -1;
    }
}
