package galaga.pages.files;

import galaga.GalagaPage;

public class FileExplorerArgs {
    private final boolean saveMode;
    private final String basePath;
    private final String defaultSaveName;
    private final GalagaPage back;
    private final GalagaPage next;
    private final FileExplorerCallback callback;

    public static FileExplorerArgs ofSaveMode(String basePath,String saveName, GalagaPage back, GalagaPage next,
            FileExplorerCallback callback) {
        return new FileExplorerArgs(true, basePath,saveName, back, next, callback);
    }

    public static FileExplorerArgs ofOpenMode(String basePath, GalagaPage page) {
        return new FileExplorerArgs(false, basePath,"", page, page, null);
    }

    public static FileExplorerArgs empty() {
        return new FileExplorerArgs(false, "","", GalagaPage.MAIN_MENU, GalagaPage.MAIN_MENU, null);
    }

    private FileExplorerArgs(boolean saveMode, String basePath,String saveName, GalagaPage back, GalagaPage next,
            FileExplorerCallback callback) {
        this.saveMode = saveMode;
        this.basePath = basePath;
        this.defaultSaveName = saveName;
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

    public String getDefaultSaveName() {
        return this.defaultSaveName;
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
