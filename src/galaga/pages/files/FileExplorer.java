package galaga.pages.files;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.Text;
import engine.elements.ui.text.TextPosition;
import engine.graphics.Renderer;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileExplorer extends Page<GalagaPage> {

    private static final int FILE_DIRECTORY = -1;
    private static final int FILE_PARENT_DIRECTORY = -2;

    private FileExplorerArgs args = FileExplorerArgs.empty();

    private Path currentPath;
    private int index = 0;
    private final List<Pair<String, Integer>> files = new ArrayList<>();

    private BufferedImage displayFiles;
    private Position displayFilesPosition;

    private Font titleFont;
    private Input saveInput;

    private Text backText;
    private Text actionText;

    private FileExplorerOption option;

    public FileExplorer() {
        super(GalagaPage.FILE_EXPLORER);
    }

    private void updateFiles() {
        this.index = 0;
        this.files.clear();
        try {
            try (DirectoryStream<Path> dir = Files.newDirectoryStream(this.currentPath)) {
                for (Path item : dir) {
                    String name = item.getFileName().toString();
                    int fileSize = item.toFile().isDirectory() ? FILE_DIRECTORY : (int) Files.size(item);
                    this.files.add(Pair.of(name, fileSize));
                }

                this.files.sort((a, b) -> {
                    if (a.getSecond() == FILE_DIRECTORY && b.getSecond() != FILE_DIRECTORY) {
                        return -1;
                    } else if (a.getSecond() != FILE_DIRECTORY && b.getSecond() == FILE_DIRECTORY) {
                        return 1;
                    } else {
                        return a.getFirst().compareToIgnoreCase(b.getFirst());
                    }
                });

                if (this.currentPath.getParent() != null) {
                    this.files.add(0, Pair.of("..", FILE_PARENT_DIRECTORY));
                }
            }

            this.rebuildDisplayFiles();
        } catch (IOException e) {
            Log.error("Failed to read directory: %s", e.getMessage());

            Path newPath = Path.of(".").toAbsolutePath().normalize();
            if (!this.currentPath.equals(newPath)) {
                this.currentPath = newPath;
                this.updateFiles();
            }
        }
    }

    @Override
    public boolean onActivate() {
        int margin = 20;

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.titleFont == null) {
            return false;
        }

        this.saveInput = new Input(Position.of(
                Config.WINDOW_WIDTH / 2,
                margin), Config.WINDOW_WIDTH / 2 - margin * 2, "Filename...", Color.WHITE, this.titleFont);
        if (!this.saveInput.init()) {
            return false;
        }
        this.saveInput.setCenter(TextPosition.CENTER, TextPosition.BEGIN);

        this.displayFiles = Galaga.getContext().getRenderer().createImage(
                Config.WINDOW_WIDTH - margin * 2,
                Config.WINDOW_HEIGHT - this.saveInput.getSize().getIntHeight() - margin * 3);
        this.displayFilesPosition = Position.of(
                margin,
                this.saveInput.getSize().getIntHeight() + margin * 2);

        this.currentPath = Path.of(".").toAbsolutePath().normalize();
        this.updateFiles();

        this.backText = new Text("BACK",
                Position.of(margin,
                        this.displayFilesPosition.getY() + this.displayFiles.getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.backText.init()) {
            return false;
        }
        this.backText.setCenter(TextPosition.BEGIN, TextPosition.END);

        this.actionText = new Text("SAVE",
                Position.of(Config.WINDOW_WIDTH - margin,
                        this.displayFilesPosition.getY() + this.displayFiles.getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.actionText.init()) {
            return false;
        }
        this.actionText.setCenter(TextPosition.END, TextPosition.END);

        this.option = FileExplorerOption.VIEW;
        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {

        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... rawArgs) {
        if (rawArgs == null || rawArgs.length != 1) {
            return;
        }

        this.args = (FileExplorerArgs) rawArgs[0];
        this.saveInput.setFocused(this.args.isSaveMode());

        if (this.args.isSaveMode()) {
            this.option = FileExplorerOption.FILE_SAVE;
            this.actionText.setText("SAVE");
        }else {
            this.option = FileExplorerOption.VIEW;
            this.actionText.setText("OPEN");
        }

        this.currentPath = Path.of(this.args.getBasePath()).toAbsolutePath().normalize();
        this.updateFiles();
    }

    @Override
    public void update(float dt) {

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_TAB)) {
            this.option = this.option == FileExplorerOption.VIEW ? FileExplorerOption.FILE_SAVE
                    : FileExplorerOption.VIEW;

            if (this.option == FileExplorerOption.FILE_SAVE) {
                this.saveInput.setFocused(true);
                this.saveInput.setColor(Color.ORANGE);
            } else {
                this.saveInput.setFocused(false);
                this.saveInput.setColor(Color.WHITE);
            }

            this.rebuildDisplayFiles();
        }

        if (this.option == FileExplorerOption.FILE_SAVE) {
            this.saveInput.update(dt);
            return;
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
            this.index--;
            this.rebuildDisplayFiles();
        } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
            this.index++;
            this.rebuildDisplayFiles();
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            Pair<String, Integer> selectedFile = this.files.get(this.index);

            switch (selectedFile.getSecond()) {
                case FILE_PARENT_DIRECTORY -> {
                    this.currentPath = this.currentPath.getParent();
                    this.updateFiles();
                }
                case FILE_DIRECTORY -> {
                    this.currentPath = this.currentPath.resolve(selectedFile.getFirst());
                    this.updateFiles();
                }
                default -> {
                    if (!this.args.isSaveMode()) {
                        this.args.getCallback().run(
                                this.currentPath.resolve(selectedFile.getFirst()).toString());
                    }
                }
            }
        }
    }

    private void rebuildDisplayFiles() {
        if (this.files.isEmpty()) {
            return;
        }

        Graphics2D gImg = Galaga.getContext().getRenderer().getImageGraphics(this.displayFiles);
        Renderer fileRenderer = new Renderer();
        fileRenderer.set(gImg);
        fileRenderer.begin();

        int margin = 10;
        int textHeight = fileRenderer.getTextSize(this.files.reversed().get(0).getFirst(), this.titleFont)
                .getIntHeight();

        Color selectColor = this.option == FileExplorerOption.VIEW ? Color.ORANGE : Color.WHITE;
        this.index = Math.clamp(this.index, 0, this.files.size() - 1);

        for (int i = this.index; i < this.files.size(); i++) {
            if (i - this.index >= Config.SIZE_MAX_DISPLAY_FILES) {
                break;
            }
            Pair<String, Integer> file = this.files.get(i);

            int fileSize = file.getSecond();
            String displayText;

            String fileSizeStr;
            if (fileSize >= 1_000_000_000) {
                fileSizeStr = String.format("%.2f GB", fileSize / 1_000_000_000.0);
            } else if (fileSize >= 1_000_000) {
                fileSizeStr = String.format("%.2f MB", fileSize / 1_000_000.0);
            } else if (fileSize >= 1_000) {
                fileSizeStr = String.format("%.2f KB", fileSize / 1_000.0);
            } else {
                fileSizeStr = String.format("%d B", fileSize);
            }
            switch (fileSize) {
                case FILE_PARENT_DIRECTORY -> displayText = "[..]";
                case FILE_DIRECTORY -> displayText = String.format("[DIR] %s", file.getFirst());
                default -> displayText = String.format("[FILE] %s (%s)", file.getFirst(), fileSizeStr);
            }

            Position textPosition = Position.of(
                    margin,
                    margin + (i - this.index + 2) * textHeight);
            fileRenderer.drawText(
                    displayText,
                    textPosition,
                    i == this.index ? selectColor : Color.WHITE,
                    this.titleFont);
        }

        fileRenderer.end();
    }

    @Override
    public void draw() {
        if (this.args.isSaveMode()) {
            this.saveInput.draw();
        }

        Galaga.getContext().getRenderer().drawImage(
                this.displayFiles,
                this.displayFilesPosition);

        this.backText.draw();
        this.actionText.draw();
    }

}
