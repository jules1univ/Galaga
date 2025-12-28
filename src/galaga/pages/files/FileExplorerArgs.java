package galaga.pages.files;

public class FileExplorerArgs {
    private final boolean saveMode;
    private final String basePath;
    private FileExplorerCallback callback;

    public static FileExplorerArgs ofSaveMode(String basePath) {
        return new FileExplorerArgs(true, basePath);
    }

    public static FileExplorerArgs ofOpenMode(String basePath) {
        return new FileExplorerArgs(false, basePath);
    }

    public static FileExplorerArgs empty() {
        return new FileExplorerArgs(false, ".");
    }

    private FileExplorerArgs(boolean saveMode, String basePath) {
        this.saveMode = saveMode;
        this.basePath = basePath;
    }

    public boolean isSaveMode() {
        return this.saveMode;
    }

    public String getBasePath() {
        return this.basePath;
    }

    public FileExplorerCallback getCallback() {
        return this.callback;
    }

}
