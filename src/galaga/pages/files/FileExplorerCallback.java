package galaga.pages.files;

@FunctionalInterface
public interface FileExplorerCallback {
    boolean run(String filename, String path);
}
