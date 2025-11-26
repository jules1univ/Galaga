package engine.graphics.sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public final class Sprite {

    private BufferedImage image = null;

    public Sprite() {
    }

    public boolean load(String path, float scale) {
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        int height = lines.size();
        int width = lines.get(0).length();

        if (height == 0 || width == 0) {
            return false;
        }

        boolean isValidSpriteSize = lines.stream().filter(line -> line.length() == width).toList()
                .size() == height;
        if (!isValidSpriteSize) {
            return false;
        }

        BufferedImage base = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                Color color = charToColor(c);
                base.setRGB(x, y, color.getRGB());
            }
        }

        if (scale <= 1) {
            this.image = base;
            return true;
        }

        BufferedImage scaled = new BufferedImage(
                (int) (width * scale),
                (int) (height * scale),
                BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int rgb = base.getRGB(x, y);

                for (int dy = 0; dy < scale; dy++) {
                    for (int dx = 0; dx < scale; dx++) {
                        scaled.setRGB(
                                (int) (x * scale) + dx,
                                (int) (y * scale) + dy,
                                rgb);
                    }
                }
            }
        }

        this.image = scaled;
        return true;
    }

    public int getWidth() {
        if (image == null) {
            return 0;
        }
        return image.getWidth();
    }

    public int getHeight() {
        if (image == null) {
            return 0;
        }
        return image.getHeight();
    }

    private Color charToColor(char c) {
        return switch (c) {
            case 'N' -> Color.BLACK;
            case 'W' -> Color.WHITE;
            case 'B' -> Color.BLUE;
            case 'R' -> Color.RED;
            case 'Y' -> Color.YELLOW;
            case 'G' -> Color.GREEN;
            default -> new Color(0, 0, 0, 0);
        };
    }

    public BufferedImage getImage() {
        return image;
    }
}
