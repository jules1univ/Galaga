package galaga.pages.files;

public class FileExplorerArgs {
    private final boolean saveMode;
    private final String basePath;

    public static FileExplorerArgs ofSaveMode(String basePath) {
        return new FileExplorerArgs(true, basePath);
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

}
