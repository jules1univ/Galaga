package galaga.pages.files;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import engine.elements.page.Page;
import engine.elements.page.PageState;
import engine.elements.ui.input.Input;
import engine.elements.ui.text.TextPosition;
import engine.graphics.Renderer;
import engine.utils.Pair;
import engine.utils.Position;
import engine.utils.logger.Log;
import galaga.Config;
import galaga.Galaga;
import galaga.GalagaPage;

public class FileExplorer extends Page<GalagaPage> {

    private FileExplorerArgs args = FileExplorerArgs.empty();

    private Path currentPath;
    private int index = 0;
    private List<Pair<String, Integer>> files = new ArrayList<>();
    
    private BufferedImage displayFiles;
    private Position displayFilesPosition;

    private Font titleFont;
    private Input saveInput;


    public FileExplorer() {
        super(GalagaPage.FILE_EXPLORER);
    }

    private void updateFiles() {
        this.currentPath = Path.of(this.args.getBasePath()).toAbsolutePath().normalize();
        this.files.clear();
        files.add(Pair.of("..", -1));
        try {
            DirectoryStream<Path> dir = Files.newDirectoryStream(this.currentPath);
            for (Path item : dir) {
                String name = item.getFileName().toString();
                int size = (int) Files.size(item);
                this.files.add(Pair.of(name, size));
            }
            dir.close();
        } catch (Exception e) {
            Log.error("Files", "Failed to read directory (%s): %s", this.currentPath.getFileName(), e.getMessage());
        }

        this.rebuildDisplayFiles();
    }

    @Override
    public boolean onActivate() {
        int margin = 20;

        this.titleFont = Galaga.getContext().getResource().get(Config.FONTS, Config.VARIANT_FONT_LARGE);
        if (this.titleFont == null) {
            return false;
        }

        this.saveInput = new Input(Position.of(
            Config.WINDOW_WIDTH/2,
            margin
        ), Config.WINDOW_WIDTH/2 - margin * 2, "Filename...", Color.WHITE, this.titleFont);
        if(!this.saveInput.init()) {
            return false;
        }
        this.saveInput.setCenter(TextPosition.CENTER, TextPosition.BEGIN);

        this.displayFiles = Galaga.getContext().getRenderer().createImage(            
            Config.WINDOW_WIDTH - margin * 2,
            Config.WINDOW_HEIGHT - this.saveInput.getSize().getIntHeight() - margin * 3
        );
        this.displayFilesPosition = Position.of(
            margin,
            this.saveInput.getSize().getIntHeight() + margin * 2
        );

        this.updateFiles();
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

        this.updateFiles();
    }

    @Override
    public void update(float dt) {
        if(this.args.isSaveMode()) {
            this.saveInput.update(dt);
        }
    }

    private void rebuildDisplayFiles() {
        Graphics2D gImg = Galaga.getContext().getRenderer().getImageGraphics(this.displayFiles);
        Renderer fileRenderer = new Renderer();
        fileRenderer.set(gImg);
        fileRenderer.begin();

        int margin = 10;
        int textHeight = fileRenderer.getTextSize(this.files.reversed().get(0).getFirst(), this.titleFont).getIntHeight();

        for (int i = this.index; i < Math.min(this.files.size(), this.index + Config.SIZE_MAX_DISPLAY_FILES); i++) {
            Pair<String, Integer> file = this.files.get(i);

            String displayText = String.format("%s (%d bytes)", file.getFirst(), file.getSecond());
            Position textPosition = Position.of(
                margin,
                margin + (i - this.index+2) * textHeight
            );
            fileRenderer.drawText(
                displayText,
                textPosition,
                Color.WHITE,
                this.titleFont
            );
        }

        fileRenderer.end();
    }

    @Override
    public void draw() {
        if(this.args.isSaveMode()) {
            this.saveInput.draw();
        }

        Galaga.getContext().getRenderer().drawImage(
            this.displayFiles,
            this.displayFilesPosition
        );
    }

}
