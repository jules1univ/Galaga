package galaga.pages.files;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.Alignment;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.Text;
import engine.graphics.Renderer;
import engine.resource.sound.Sound;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.Size;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;
import galaga.GalagaSound;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
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
    private Object lastState = null;

    private Path currentPath;
    private int index = 0;
    private final List<Pair<String, Integer>> files = new ArrayList<>();

    private Renderer displayFilesRenderer;
    private Position displayFilesPosition;
    private Size displayFilesSize;

    private Font titleFont;
    private Input saveInput;

    private Text backText;
    private Text actionText;

    private Sound themeSound;
    private Sound selectSound;
    private Sound keyboardSound;

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
                    if (this.args.isSaveMode() && !item.toFile().isDirectory()) {
                        continue;
                    }

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
        this.themeSound = Galaga.getContext().getResource().get(GalagaSound.menu_theme);
        if (this.themeSound == null) {
            return false;
        }
        this.themeSound.setLoop(true);
        this.themeSound.play(0.2f);

        this.selectSound = Galaga.getContext().getResource().get(GalagaSound.menu_select);
        if (this.selectSound == null) {
            return false;
        }
        this.selectSound.setCapacity(4);

        this.keyboardSound = Galaga.getContext().getResource().get(GalagaSound.menu_keyboard);
        if (this.keyboardSound == null) {
            return false;
        }
        this.keyboardSound.setCapacity(4);

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
        this.saveInput.setCenter(Alignment.CENTER, Alignment.BEGIN);

        int textHeight = this.saveInput.getSize().getIntHeight();
        this.displayFilesSize = Size.of(
                Config.WINDOW_WIDTH - margin * 2,
                Config.SIZE_MAX_DISPLAY_FILES * textHeight);
        this.displayFilesRenderer = Renderer.ofSub(this.displayFilesSize);
        this.displayFilesPosition = Position.of(
                margin,
                this.saveInput.getSize().getIntHeight() + margin * 2);

        this.backText = new Text("BACK",
                Position.of(margin,
                        this.displayFilesPosition.getY() + this.displayFilesSize.getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.backText.init()) {
            return false;
        }
        this.backText.setCenter(Alignment.BEGIN, Alignment.END);

        this.actionText = new Text("SAVE",
                Position.of(Config.WINDOW_WIDTH - margin * 2,
                        this.displayFilesPosition.getY() + this.displayFilesSize.getHeight() + margin),
                Color.WHITE, this.titleFont);
        if (!this.actionText.init()) {
            return false;
        }
        this.actionText.setCenter(Alignment.END, Alignment.END);

        this.currentPath = Path.of(".").toAbsolutePath().normalize();
        this.option = FileExplorerOption.FILE_SAVE;
        this.updateFiles();
        this.updateMenuSelect();

        this.state = PageState.ACTIVE;
        return true;
    }

    @Override
    public boolean onDeactivate() {
        this.themeSound.stop();
        this.selectSound.stop();
        this.keyboardSound.stop();
        this.state = PageState.INACTIVE;
        return true;
    }

    @Override
    public void onReceiveArgs(Object... rawArgs) {
        if (rawArgs == null || rawArgs.length < 1) {
            Galaga.getContext().getApplication().setCurrentPage(GalagaPage.MAIN_MENU);
            return;
        }

        this.args = (FileExplorerArgs) rawArgs[0];
        this.lastState = rawArgs.length >= 2 ? rawArgs[1] : null;

        this.saveInput.setFocused(this.args.isSaveMode());

        if (this.args.isSaveMode()) {
            this.option = FileExplorerOption.FILE_SAVE;
            this.actionText.setText("SAVE");
            this.saveInput.setText(this.args.getDefaultSaveName());
        } else {
            this.option = FileExplorerOption.VIEW;
            this.actionText.setText("OPEN");
        }

        this.currentPath = Path.of(this.args.getBasePath()).toAbsolutePath().normalize();
        this.updateFiles();
        this.updateMenuSelect();
    }

    private void updateMenuSelect() {
        switch (this.option) {
            case FILE_SAVE -> {
                this.saveInput.setFocused(true);
                this.saveInput.setColor(Color.ORANGE);

                this.backText.setColor(Color.WHITE);
                this.actionText.setColor(Color.WHITE);
            }
            case VIEW -> {
                this.saveInput.setFocused(false);
                this.saveInput.setColor(Color.WHITE);

                this.backText.setColor(Color.WHITE);
                this.actionText.setColor(Color.WHITE);
            }
            case ACTION -> {
                this.saveInput.setFocused(false);
                this.saveInput.setColor(Color.WHITE);

                this.backText.setColor(Color.WHITE);
                this.actionText.setColor(Color.ORANGE);
            }
            case BACK -> {
                this.saveInput.setFocused(false);
                this.saveInput.setColor(Color.WHITE);

                this.backText.setColor(Color.ORANGE);
                this.actionText.setColor(Color.WHITE);
            }
        }

        this.rebuildDisplayFiles();
    }

    @Override
    public void update(float dt) {
        if (this.option == FileExplorerOption.FILE_SAVE && Galaga.getContext().getInput().isTyping()) {
           this.keyboardSound.play(.5f);
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_TAB)) {
            this.selectSound.play(2.f);
            switch (this.option) {
                case FILE_SAVE -> {
                    this.option = FileExplorerOption.VIEW;
                }
                case VIEW -> {
                    this.option = FileExplorerOption.ACTION;
                    this.saveInput.setFocused(true);
                }
                case ACTION -> {
                    this.option = FileExplorerOption.BACK;
                    this.saveInput.setFocused(true);
                }
                case BACK -> {
                    this.option = FileExplorerOption.FILE_SAVE;
                    this.saveInput.setFocused(true);
                }
            }
            this.updateMenuSelect();
        }

        if (this.option == FileExplorerOption.FILE_SAVE) {
            this.saveInput.update(dt);
            return;
        }

        if (this.option == FileExplorerOption.VIEW) {
            if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_UP)) {
                this.index--;
                this.rebuildDisplayFiles();
            } else if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_DOWN)) {
                this.index++;
                this.rebuildDisplayFiles();
            }
        }

        if (Galaga.getContext().getInput().isKeyPressed(KeyEvent.VK_ENTER)) {
            this.selectSound.play(2.f);
            if (this.option == FileExplorerOption.BACK) {
                Galaga.getContext().getApplication().setCurrentPage(this.args.getBackPage(), this.lastState);
                return;
            }

            if (this.option == FileExplorerOption.ACTION) {
                if (this.args.isSaveMode()) {
                    String filename = this.saveInput.getText().trim();
                    if (filename.isEmpty()) {
                        return;
                    }

                    String outputPath = this.currentPath.resolve(filename).toString();
                    if (this.args.getCallback().run(filename, outputPath)) {
                        Galaga.getContext().getApplication().setCurrentPage(this.args.getNextPage());
                    }
                } else {
                    String outputFilename = this.files.get(this.index).getFirst();
                    String outputPath = this.currentPath.resolve(outputFilename).toString();
                    
                    Galaga.getContext().getApplication().setCurrentPage(this.args.getNextPage(), FileExplorerResult.of(
                            outputFilename,
                            outputPath));
                }
                return;
            }

            if (this.option != FileExplorerOption.VIEW) {
                return;
            }

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
                    String outputPath = this.currentPath.resolve(selectedFile.getFirst()).toString();
                    if (!this.args.isSaveMode()) {
                        Galaga.getContext().getApplication().setCurrentPage(this.args.getNextPage(), FileExplorerResult.of(
                                selectedFile.getFirst(),
                                outputPath));
                    }
                }
            }
        }
    }

    private void rebuildDisplayFiles() {
        if (this.files.isEmpty()) {
            return;
        }

        this.displayFilesRenderer.beginSub();
        int margin = 10;
        int textHeight = this.displayFilesRenderer.getTextSize(this.files.reversed().get(0).getFirst(), this.titleFont)
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
            this.displayFilesRenderer.drawText(
                    displayText,
                    textPosition,
                    i == this.index ? selectColor : Color.WHITE,
                    this.titleFont);
        }

        this.displayFilesRenderer.end();
    }

    @Override
    public void draw(Renderer renderer) {
        if (this.args.isSaveMode()) {
            this.saveInput.draw(renderer);
        }

        renderer.draw(this.displayFilesRenderer, this.displayFilesPosition);

        this.backText.draw(renderer);
        this.actionText.draw(renderer);
    }

}
