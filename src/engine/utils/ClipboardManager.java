package engine.utils;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

public final class ClipboardManager {
    private final Clipboard clipboard;

    public ClipboardManager(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public void set(String content) {
        this.clipboard.setContents(new StringSelection(content), null);
    }

    public String get() {
        if (this.clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                return (String) this.clipboard.getData(DataFlavor.stringFlavor);
            } catch (Exception e) {
            }
        }

        return null;
    }

}
