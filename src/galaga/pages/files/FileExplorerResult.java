package galaga.pages.files;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class FileExplorerResult {

    private final String fileName;
    private final String filePath;

    public static FileExplorerResult of(String fileName, String filePath) {
        return new FileExplorerResult(fileName, filePath);
    }

    private FileExplorerResult(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public Optional<String> getFileContent() {
        String content = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                content += line + "\n";
            }
            return Optional.of(content);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
