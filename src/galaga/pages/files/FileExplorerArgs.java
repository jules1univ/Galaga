package galaga.pages.files;

import galaga.GalagaPage;

public class FileExplorerArgs {
    private final boolean saveMode;
    private final String basePath;
    private final GalagaPage back;
    private final GalagaPage next;
    private final FileExplorerCallback callback;

    public static FileExplorerArgs ofSaveMode(String basePath, GalagaPage back, GalagaPage next,
            FileExplorerCallback callback) {
        return new FileExplorerArgs(true, basePath, back, next, callback);
    }

    public static FileExplorerArgs ofOpenMode(String basePath, GalagaPage back, GalagaPage next,
            FileExplorerCallback callback) {
        return new FileExplorerArgs(false, basePath, back, next, callback);
    }

    public static FileExplorerArgs empty() {
        return new FileExplorerArgs(false, "", GalagaPage.MAIN_MENU, GalagaPage.MAIN_MENU, null);
    }

    private FileExplorerArgs(boolean saveMode, String basePath, GalagaPage back, GalagaPage next,
            FileExplorerCallback callback) {
        this.saveMode = saveMode;
        this.basePath = basePath;
        this.back = back;
        this.next = next;
        this.callback = callback;
    }

    public boolean isSaveMode() {
        return this.saveMode;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public GalagaPage getBackPage() {
        return this.back;
    }

    public GalagaPage getNextPage() {
        return this.next;
    }

    public FileExplorerCallback getCallback() {
        return this.callback;
    }

}
