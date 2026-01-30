package engine.elements.ui.code;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class HistoryManager {

    private final List<String> history = new ArrayList<>();
    private int index = -1;

    public HistoryManager() {
    }

    public void add(String state) {
        if (state == null) {
            return;
        }

        if (index >= 0 && history.get(index).equals(state)) {
            return;
        }

        history.subList(index + 1, history.size()).clear();

        history.add(state);
        index++;
    }

    public Optional<String> undo() {
        if (!canUndo()) {
            return Optional.empty();
        }
        index--;
        return Optional.of(history.get(index));
    }

    public Optional<String> redo() {
        if (!canRedo()) {
            return Optional.empty();
        }
        index++;
        return Optional.of(history.get(index));
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
